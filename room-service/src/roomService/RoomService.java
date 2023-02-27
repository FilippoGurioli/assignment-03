package roomService;

import java.util.Optional;

/**
 * Application main class, it contains the main logic of information exchanging between other subsystems.
 */
public class RoomService {
	
	private final TimeThread time = new TimeThread();
	private final Peripherals p = new Peripherals();
	private final ESPEmulator esp = new ESPEmulator();
	private final SerialPortCommunicator serialComm = new SerialPortCommunicator(this);

	public RoomService() throws Exception {
		time.start();
		
		while (true) {
			Thread.sleep(10_000);
			String pir = esp.getCommandPIR();
			String pr = esp.getCommandPR();
			this.printStatus(pir, pr);
			//-----------------------SERVO AUTO-HANDLING------------------
			if (time.isMorning() && pir.equals("PEOPLE") && this.p.getServo() == 180) {
				System.out.println("J-send: 0");
				this.serialComm.send("0");
			} else if (!time.isMorning() && pir.equals("NOONE") && this.p.getServo() < 180) {
				System.out.println("J-send: 180");
				this.serialComm.send("180");
			}
			//-----------------------LED AUTO-HANDLING--------------------
			if (pr.equals("BLACK") && pir.equals("PEOPLE") && this.p.getLed() == Led.OFF) {
				System.out.println("J-send: ON");
				this.serialComm.send("ON");
			} else if (this.p.getLed() == Led.ON) {
				System.out.println("J-send: OFF");
				this.serialComm.send("OFF");
			}
		}
	}
	
	public void executeCommand(final Optional<Led> light, final Optional<Integer> servo) {
		if (light.isPresent() && !light.get().equals(this.p.getLed())) {
			this.p.setLed(light.get());
			System.out.println("LED turned " + light.get());
		}
		if (servo.isPresent() && !servo.get().equals(this.p.getServo())) {
			this.p.setServo(servo.get());
			System.out.println("Servo set at " + servo.get());
		}
	}
	
	private void printStatus(final String pir, final String pr) {
		System.out.println("\nTime: " + time);
		System.out.println("PIR: " + pir);
		System.out.println("Light sensor: " + pr);
		System.out.println("-----------------");
		System.out.println(this.p + "\n");
	}
}
