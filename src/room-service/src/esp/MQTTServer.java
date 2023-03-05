package esp;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import roomService.RoomService;

/**
 * An MQTT Server instance that handles MQTT communication with ESP32.
 * 
 * @param rs Room-Service instance, used to update peripherals values.
 */
public class MQTTServer {

	private final RoomService rs;
	
	public MQTTServer(RoomService rs) {
		this.rs = rs;
		Vertx vertx = Vertx.vertx();
		MqttServer mqttServer = MqttServer.create(vertx);
		
		mqttServer.endpointHandler(endpoint -> {
			endpoint.accept(false);
            endpoint.publishHandler(message -> {
            	log(message.topicName() + ": " + message.payload().toString());
            	if (message.topicName().equals("PIR")) {
            		this.rs.getPeripherals().setPresence(message.payload().toString().equals("true") ? true : false);
            	}
            	if (message.topicName().equals("LS")) {
            		this.rs.getPeripherals().setBrightness(message.payload().toString().equals("true") ? true : false);
            	}
            });
	
		})
		  .listen(ar -> {
			  
		    if (ar.succeeded()) {
	
		      log("MQTT server is listening on port " + ar.result().actualPort());
		    } else {
	
		      log("Error on starting the server");
		      ar.cause().printStackTrace();
		    }
		  });
	}
	
	private void log(final String msg) {
		System.out.println("[MQTT SERVER] " + msg);
	}
	
}
