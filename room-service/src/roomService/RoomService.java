package roomService;

import java.nio.charset.StandardCharsets;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Application main class, it contains the main logic of information exchanging between other subsystems.
 */
public class RoomService {
	
	private final static int BAUD_RATE = 9600;
	private final TimeThread time = new TimeThread();
	private Peripherals p;
	private final ESPEmulator esp = new ESPEmulator();

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
			throw new Exception(); //da modificare con un'eccezione più specifica
		}
		comPort.setComPortParameters(BAUD_RATE, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		if (comPort.openPort()) {			
			System.out.println("Connected to " + comPort + " port");
		} else {
			System.err.println("CannotOpenPortException.");
			throw new Exception(); //da modificare con un'eccezione più specifica
		}
		
		comPort.addDataListener(new SerialPortDataListener() {
		   @Override
		   public int getListeningEvents() {
			   return SerialPort.LISTENING_EVENT_DATA_RECEIVED | SerialPort.LISTENING_EVENT_DATA_WRITTEN;
		   }

		   @Override
			public void serialEvent(final SerialPortEvent event) {
				if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
					System.out.print(new String(event.getReceivedData(), StandardCharsets.UTF_8));
				} else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
					//System.out.println("All bytes were successfully transmitted!");
				}
			}
		   
		});
		//Communication Protocol: 
		//the info must be always the same size -> take the largest info as a reference, the other must fill the extra space with white spaces (" ")
		//the info must end with new line (\n)
		//i.e.: send 2 msgs, "180" and "ON". The largest is "180" that becomes "180\n", "ON" becomes "ON\n " to fill extra space
		while (true) {
			//Starting from Servo 180, PIR false, Morning false
			Thread.sleep(10_000);
			String pir = esp.getCommandPIR();
			System.out.println("Morning: " + time.isMorning());
			System.out.println("PIR: " + pir);
			if (time.isMorning() && pir.equals("PEOPLE") && this.p.getServo() == 180) {
				System.out.println("Sending: 0");
				this.p = new Peripherals(0, this.p.getLed());
				comPort.writeBytes("0\n  ".getBytes(), "0\n  ".getBytes().length);
			}
			if (!time.isMorning() && pir.equals("NOONE") && this.p.getServo() > 0) {
				System.out.println("Sending: 180");
				this.p = new Peripherals(180, this.p.getLed());
				comPort.writeBytes("180\n".getBytes(), "180\n".getBytes().length);
			}
		}
	}
}
