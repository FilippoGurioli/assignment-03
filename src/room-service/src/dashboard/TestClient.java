package dashboard;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class TestClient extends AbstractVerticle {
	
	public static void main(String[] args) throws Exception {		
	
		String host = "localhost"; // "b1164b27.ngrok.io";
		int port = 80;

		Vertx vertx = Vertx.vertx();
		
		JsonObject item = new JsonObject();
		item.put("lightVal", "ON");
		item.put("blindsVal","138");

		WebClient client = WebClient.create(vertx);

		System.out.println("Posting new data item... ");
		client
		.post(port, host, "/assignment-03/room-dashboard/Functioning%20Test/Smarthome-Dashboard.php")
		.putHeader("content-type", "application/x-www-form-urlencoded")
		.addQueryParam("blindsVal", "139")
		.send()
		//.sendJson(item)
		.onSuccess(response -> {
			System.out.println(item);
			System.out.println("Posting - Received response with status code: " + response.statusCode());
		})
		.onFailure(err ->
	    System.out.println("Something went wrong " + err.getMessage()));
		
		Thread.sleep(1000);
		/*
		System.out.println("Getting data items... ");
		client
		  .get(port, host, "/api/data")
		  .send()
		  .onSuccess(res -> { 
			  System.out.println("Getting - Received response with status code: " + res.statusCode());
			  JsonArray response = res.bodyAsJsonArray();
		      System.out.println(response.encodePrettily());
		  })
		  .onFailure(err ->
		    System.out.println("Something went wrong " + err.getMessage()));*/
	}
	
}
