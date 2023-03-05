package esp;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import roomService.RoomService;

public class MQTTServer {

	private final RoomService rs;
	
	public MQTTServer(RoomService rs) {
		this.rs = rs;
		Vertx vertx = Vertx.vertx();
		MqttServer mqttServer = MqttServer.create(vertx);
		
		mqttServer.endpointHandler(endpoint -> {
			endpoint.accept(false);
            endpoint.publishHandler(message -> {
            	System.out.println(message.topicName());
            	System.out.println(message.payload().toJson().toString());
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
	
		      System.out.println("MQTT server is listening on port " + ar.result().actualPort());
		    } else {
	
		      System.out.println("Error on starting the server");
		      ar.cause().printStackTrace();
		    }
		  });
	}
	
}
