package roomService;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Application main class, it contains the main logic of information exchanging between other subsystems.
 */
public class RoomService {

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
		System.out.println("Connected to " + comPort + " port");
		comPort.openPort();
		comPort.addDataListener(new SerialPortDataListener() {
		   @Override
		   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_WRITTEN; }
		   @Override
		   public void serialEvent(SerialPortEvent event)
		   {
		      if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN)
		         System.out.println("All bytes were successfully transmitted!");
		   }
		});
		
		comPort.writeBytes("180".getBytes(), "180".length());
	}
}
