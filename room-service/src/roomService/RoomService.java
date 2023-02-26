package roomService;

import java.nio.charset.StandardCharsets;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Application main class, it contains the main logic of information exchanging between other subsystems.
 */
public class RoomService {
	
	/*
	Communication protocols:
	Arduino to Java - must have "/" before and after the command (sends both debugging strings and commands)
	Java to Arduino - must have "\n" at the end of the command (sends only commands)
	*/
	
	private final static int BAUD_RATE = 9600;
	private final TimeThread time = new TimeThread();
	private Peripherals p;
	private final ESPEmulator esp = new ESPEmulator();
	private String data = "";
	private boolean stream = false;

	public RoomService() throws Exception {
		time.start();
		this.p = new Peripherals(180, false); //Arduino starts always at Servo 180 and led off
		SerialPort comPort = null;
		for (var port : SerialPort.getCommPorts()) {
			if (port.toString().contains("Arduino Uno")) {
				comPort = port;
			}
		}
		if (comPort == null) {
			System.err.println("PortNotFoundException. There is no serial port communicating with Arduino Uno available right now");
			throw new Exception();
		}
		comPort.setComPortParameters(BAUD_RATE, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		if (comPort.openPort()) {			
			System.out.println("Connected to " + comPort + " port");
		} else {
			System.err.println("UnopenablePortException. Cannot open the " + comPort + " port. Maybe it's already in use.");
			throw new Exception();
		}
		
		comPort.addDataListener(new SerialPortDataListener() {
		   @Override
		   public int getListeningEvents() {
			   return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		   }

		   @Override
			public void serialEvent(final SerialPortEvent event) {
				String msg = new String(event.getReceivedData(), StandardCharsets.UTF_8);
				msg.chars().forEach(ch -> {
					if (ch == '/') {
						stream = !stream;
					} else if (stream) {
						data += (char) ch;
					}
				});
				if (!stream) {					
					if (data.equals("ON")) {
						p = new Peripherals(p.getServo(), true);
						System.out.println("LED turned on");
						data = "";
					} else if (data.equals("OFF")) {
						p = new Peripherals(p.getServo(), false);
						System.out.println("LED turned off");
						data = "";
					} else {					
						try {
							p = new Peripherals(Integer.parseInt(data), p.getLed());
							System.out.println("Servo set at: " + p.getServo());
							data = "";
						} catch (final NumberFormatException e) {
							//if data isn't "ON" or "OFF" nor a value it should be a debugging message
							System.out.print(msg.replace("/", ""));
						}
					}
				}
			}
		});
		while (true) {
			//Starting from Servo 180, PIR NOONE, Morning false, PR BLACK, LED off
			Thread.sleep(10_000);
			String pir = esp.getCommandPIR();
			String pr = esp.getCommandPR();
			this.printStatus(pir, pr);
			//-----------------------SERVO AUTO-HANDLING------------------
			if (time.isMorning() && pir.equals("PEOPLE") && this.p.getServo() == 180) {
				System.out.println("J-send: 0");
				comPort.writeBytes("0\n".getBytes(), "0\n".getBytes().length);
			} else if (!time.isMorning() && pir.equals("NOONE") && this.p.getServo() < 180) {
				System.out.println("J-send: 180");
				comPort.writeBytes("180\n".getBytes(), "180\n".getBytes().length);
			}
			//-----------------------LED AUTO-HANDLING--------------------
			if (pr.equals("BLACK") && pir.equals("PEOPLE") && !this.p.getLed()) {
				System.out.println("J-send: ON");
				comPort.writeBytes("ON\n".getBytes(), "ON\n".getBytes().length);
			} else if (this.p.getLed()) {
				System.out.println("J-send: OFF");
				comPort.writeBytes("OFF\n".getBytes(), "OFF\n".getBytes().length);
			}
		}
	}
	
	private void printStatus(final String pir, final String pr) {
		System.out.println("\nMorning: " + time.isMorning());
		System.out.println("PIR: " + pir);
		System.out.println("Light sensor: " + pr);
		System.out.println("-----------------");
		System.out.println(this.p + "\n");
	}
}
