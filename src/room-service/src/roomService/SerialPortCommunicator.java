package roomService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialPortCommunicator {

	/*
	Communication protocols:
	Arduino to Java - must have "/" before and after the command (sends both debugging strings and commands)
	Java to Arduino - must have "\n" at the end of the command (sends only commands)
	*/
	private final static int BAUD_RATE = 9600;
	private String data = "";
	private boolean stream = false;
	private SerialPort comPort;
	
	public SerialPortCommunicator(final RoomService caller) throws Exception {
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
			Log("Connected to " + comPort + " port");
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
						caller.executeCommand(Optional.of(Led.ON), Optional.empty());
						data = "";
					} else if (data.equals("OFF")) {
						caller.executeCommand(Optional.of(Led.OFF), Optional.empty());
						data = "";
					} else {					
						try {
							caller.executeCommand(Optional.empty(), Optional.of(Integer.parseInt(data)));
							data = "";
						} catch (final NumberFormatException e) {
							//if data isn't "ON" or "OFF" nor a value it should be a debugging message
							Log(msg.replace("/", ""));
						}
					}
				}
			}
		});
	}
	
	public void send(final String msg) {
		final String prtMsg = msg + "\n";
		Log("Send: " + msg);
		this.comPort.writeBytes(prtMsg.getBytes(), prtMsg.getBytes().length);
	}
	
	private void Log(String msg) {
		System.out.println("[SERIAL PORT] " + msg);
	}
}
