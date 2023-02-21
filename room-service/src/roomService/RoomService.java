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

	public RoomService() throws Exception {
		
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
		while (true) {
			try { Thread.sleep(5000); } catch (Exception e) { e.printStackTrace(); }
			comPort.writeBytes("ciao".getBytes(), 1000);
		}
		//comPort.closePort();
		//System.out.println("Disconnected from " + comPort + " port");
		
	}
}
