package roomService;

import java.util.LinkedList;

import dashboard.Data;
import dashboard.HttpServer;
import io.vertx.core.Vertx;

/**
 * Application main class, it contains the main logic of information exchanging between other subsystems.
 */
public class RoomService {
	
	private final static int WAITING_TIME = 1000;
	private static final int MAX_HISTORY_SIZE = 10;
	
	private final TimeThread time = new TimeThread();
	private final Peripherals p = new Peripherals();
	private final SerialPortCommunicator serialComm = new SerialPortCommunicator(this);
	private final HttpServer httpServer = new HttpServer(this);
	
	private LinkedList<Data> valuesHistory = new LinkedList<>();
	private boolean btPrivilege = false;
	private boolean dashPrivilege = false;
	
	public RoomService() throws Exception {
		time.start();
		
		//Starts the HTTP server
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(httpServer);
		
		while (true) {
			Thread.sleep(WAITING_TIME*3);
			if (!btPrivilege && !dashPrivilege) {
				this.printStatus();
				//-----------------------SERVO AUTO-HANDLING------------------
				if (time.isMorning() && this.p.isPresent() && this.p.getServo() == Peripherals.MAX_DEG) {
					this.executeCommand(Peripherals.MIN_DEG);
				} else if (!time.isMorning() && !this.p.isPresent() && this.p.getServo() < Peripherals.MAX_DEG) {
					this.executeCommand(Peripherals.MAX_DEG);
				}
				//-----------------------LED AUTO-HANDLING--------------------
				if (!this.p.isBright() && this.p.isPresent() && this.p.getLed() == Led.OFF) {
					this.executeCommand(Led.ON);
				} else if (this.p.getLed() == Led.ON) {
					this.executeCommand(Led.OFF);
				}
			} else {
				Thread.sleep(WAITING_TIME);
			}
		}
	}
	
	/**
	 * Orders to switch on/off the lights in Arduino.
	 * 
	 * @param servo The new lights value.
	 */
	public void executeCommand(final Led light) {
		if (!light.equals(this.p.getLed())) {
			this.serialComm.send(light.toString());
		}
	}
	
	/**
	 * Orders to change the servo position in Arduino.
	 * 
	 * @param servo The new servo value.
	 */
	public void executeCommand(final int servo) {
		if (servo != this.p.getServo()) {
			this.serialComm.send(String.valueOf(servo));
		}
	}
	
	/**
	 * Updates the servo value in class Peripherals.
	 * 
	 * @param servo The new servo value.
	 */
	public void updatePeripheral(final int servo) {
		this.p.setServo(servo);
		log("Servo set at " + this.p.getServo());
	}
	
	/**
	 * Updates the lights value in class Peripherals.
	 * 
	 * @param light The new lights value.
	 */
	public void updatePeripheral(final Led light) {
		this.p.setLed(light);
		log("LED turned " + light);
	}
	
	public Peripherals getPeripherals() {
		return this.p;
	}
	
	/**
	 * Changes the privileges to modify the state of the room of Android and dashboard applications.
	 * 
	 * @param master The privilege to swap.
	 */
	public void changePrivilegeOf(final Master master) {
		switch(master) {
		case BT:
			btPrivilege = !btPrivilege;
			break;
		case DASH:
			this.serialComm.send("DASH");
			dashPrivilege = !dashPrivilege;
			break;
		}
	}
	
	/**
	 * Adds a new data entry in the history list. History list contains at most {@link MAX_HISTORY_SIZE} data records.
	 * 
	 * @param newData The new data to add.
	 */
	public void addToHistory(Data newData) {
		valuesHistory.addFirst(newData);
		if (valuesHistory.size() > MAX_HISTORY_SIZE) {
			valuesHistory.removeLast();
		}
	}
	
	public LinkedList<Data> getHistory() {
		return valuesHistory;
	}
	
	private void printStatus() {
		log("----- STATUS -----");
		log("Time: " + time);
		log("BTMaster: " + btPrivilege);
		log("DASHMaster: " + dashPrivilege);
		log(this.p.toString());
		log("------------------");
	}
	
	private void log(final String msg) {
		System.out.println("[ROOM SERVICE] " + msg);
	}
}
