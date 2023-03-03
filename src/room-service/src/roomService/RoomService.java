package roomService;

import dashboard.HttpServer;
import io.vertx.core.Vertx;

/**
 * Application main class, it contains the main logic of information exchanging between other subsystems.
 */
public class RoomService {
	
	private final TimeThread time = new TimeThread();
	private final Peripherals p = new Peripherals();
	private final ESPEmulator esp = new ESPEmulator();
	private final SerialPortCommunicator serialComm = new SerialPortCommunicator(this);
	private final HttpServer httpServer = new HttpServer(this);

	public RoomService() throws Exception {
		time.start();
		
		//Starts the HTTP server
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(httpServer);
		
		while (true) {
			Thread.sleep(10_000);
			String pir = esp.getCommandPIR();
			String pr = esp.getCommandPR();
			this.printStatus(pir, pr);
			//-----------------------SERVO AUTO-HANDLING------------------
			if (time.isMorning() && pir.equals("PEOPLE") && this.p.getServo() == 180) {
				this.serialComm.send("0");
			} else if (!time.isMorning() && pir.equals("NOONE") && this.p.getServo() < 180) {
				this.serialComm.send("180");
			}
			//-----------------------LED AUTO-HANDLING--------------------
			if (pr.equals("BLACK") && pir.equals("PEOPLE") && this.p.getLed() == Led.OFF) {
				this.serialComm.send("ON");
			} else if (this.p.getLed() == Led.ON) {
				this.serialComm.send("OFF");
			}
		}
	}
	
	public void executeCommand(final Led light) {
		if (!light.equals(this.p.getLed())) {
			this.p.setLed(light);
			log("LED turned " + light);
		}
	}
	
	public void executeCommand(final int servo) {
		if (servo != this.p.getServo()) {
			this.p.setServo(servo);
			log("Servo set at " + this.p.getServo());
		}
	}
	
	private void printStatus(final String pir, final String pr) {
		log("----- STATUS -----");
		log("Time: " + time);
		log("PIR: " + pir);
		log("Light sensor: " + pr);
		log(this.p.toString());
		log("------------------");
	}
	
	private void log(final String msg) {
		System.out.println("[ROOM SERVICE] " + msg);
	}
}
