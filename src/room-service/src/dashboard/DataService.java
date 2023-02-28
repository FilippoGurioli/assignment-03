package dashboard;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/*
 * Data Service as a vertx event-loop 
 */
public class DataService extends AbstractVerticle {

	private int port;
	private static final int MAX_SIZE = 10;
	private LinkedList<DataPoint> values;
	
	private String light = "OFF";
	private int degrees = 0;
	private String presence = "0";
	private String darkness = "0";
	
	public DataService(int port) {
		values = new LinkedList<>();		
		this.port = port;
	}

	@Override
	public void start() {		
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);
		router.post("/api/ESPdata").handler(this::handleESPData);
		router.get("/api/data").handler(this::handleGetData);
		router.get("/api/currentData").handler(this::handleGetCurrentData);
		vertx
			.createHttpServer()
			.requestHandler(router)
			.listen(port);
		log("Service ready on port: " + port);
	}
	
	private void handleAddNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			//Add POST's record to list
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
			String date = formatter.format(new Date());
			String content = "";
			if (res.getString("type").equals("light")) {
				content = "Luci: " + res.getString("value");
				light = res.getString("value"); //Update lights value
			}
			if (res.getString("type").equals("blind")) {
				content = "Tende: " + res.getString("value") + "%";
				degrees = Integer.parseInt(res.getString("value")); //Update blinds value
			}
			formatter = new SimpleDateFormat("HH:mm:ss");
			String time = formatter.format(new Date());
			
			values.addFirst(new DataPoint(date, time, content));
			if (values.size() > MAX_SIZE) {
				values.removeLast();
			}
			
			//Send response
			log(res.encodePrettily());
			final String origin = routingContext.request().getHeader("Origin");
			response.putHeader("Access-Control-Allow-Origin", origin)
					.setStatusCode(200)
					.end();
		}
	}
	
	private void handleGetData(RoutingContext routingContext) {
		//Build response
		JsonArray arr = new JsonArray();
		for (DataPoint data : values) {
			JsonObject allData = new JsonObject();
			allData.put("Date", data.getDate());
			allData.put("Time", data.getTime());
			allData.put("Content", data.getContent());
			arr.add(allData);
		}
		
		//Send response
		HttpServerResponse response = routingContext.response();
		final String origin = routingContext.request().getHeader("Origin");
		response.putHeader("Access-Control-Allow-Origin", origin);
		response.putHeader("content-type", "application/json")
				.end(arr.encodePrettily());
	}
	
	private void handleGetCurrentData(RoutingContext routingContext) {
		//Build response
		JsonObject allData = new JsonObject();
		allData.put("light", light);
		allData.put("degrees", degrees);
		
		//Send response
		HttpServerResponse response = routingContext.response();
		final String origin = routingContext.request().getHeader("Origin");
		response.putHeader("Access-Control-Allow-Origin", origin);
		response.putHeader("content-type", "application/json")
				.end(allData.encodePrettily());
	}
	
	private void handleESPData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			//Update presence and darkness values;
			presence = res.getString("presence");
			darkness = res.getString("darkness");
			//System.out.println(res);
			
			log("Presence: " + presence + " - Darkness: " + darkness);

			//Send response
			final String origin = routingContext.request().getHeader("Origin");
			response.putHeader("Access-Control-Allow-Origin", origin)
					.setStatusCode(200)
					.end();
		}
	}
	
	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void log(String msg) {
		System.out.println("[DATA SERVICE] "+msg);
	}

}