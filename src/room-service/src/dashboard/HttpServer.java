package dashboard;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import roomService.Led;
import roomService.Master;
import roomService.RoomService;

/**
 * An HTTP Server instance that handles HTTP requests from the dashboard HTTP client or ESP32, as a vertx event-loop.
 * 
 * @param rs Room-Service instance, used to update peripherals values.
 */
public class HttpServer extends AbstractVerticle {
	
	private static final int PORT = 8080;
	private final RoomService rs;
	
	public HttpServer(RoomService rs) {
		this.rs = rs;
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(this);
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handlePostNewData);
		router.post("/api/ESPdata").handler(this::handlePostESPData);
		router.get("/api/data").handler(this::handleGetHistory);
		router.get("/api/currentData").handler(this::handleGetControls);
		vertx
			.createHttpServer()
			.requestHandler(router)
			.listen(PORT);
		log("Service ready on port: " + PORT);
	}
	
	/**
	 * Handles POST request from dashboard controls' page: sets new values for lights or blinds.
	 * @param routingContext
	 */
	private void handlePostNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			//Forms data content
			String content = "";
			if (res.getString("type").equals("light")) {
				content = "Luci: " + res.getString("value");
				//Update lights value
				if (res.getString("value").equals("ON")) {
					rs.executeCommand(Led.ON);
				}
				if (res.getString("value").equals("OFF")) {
					rs.executeCommand(Led.OFF);
				}
			}
			if (res.getString("type").equals("blind")) {
				content = "Tende: " + res.getString("value") + "%";
				rs.executeCommand(Integer.parseInt(res.getString("value"))); //Update blinds value
			}
			if (res.getString("type").equals("master")) {
				rs.changePrivilegeOf(Master.DASH);
			}
			
			//Add POST's record to history list
			if(!content.equals("")) {
				Data newData = new Data(content);
				rs.addToHistory(newData);	
			}
			
			//Send response
			log("\n" + res.encodePrettily());
			final String origin = routingContext.request().getHeader("Origin");
			response.putHeader("Access-Control-Allow-Origin", origin)
					.setStatusCode(200)
					.end();
		}
	}
	
	/**
	 * Handles GET request from dashboard history's page: sends back a JSON object with the last ten values executed in arduino.
	 * 
	 * @param routingContext
	 */
	private void handleGetHistory(RoutingContext routingContext) {
		//Build response
		JsonArray arr = new JsonArray();
		if (!rs.getHistory().isEmpty()) {
			for (Data data : rs.getHistory()) {
				JsonObject allData = new JsonObject();
				allData.put("Date", data.getDate());
				allData.put("Time", data.getTime());
				allData.put("Content", data.getContent());
				arr.add(allData);
			}
		}
		
		//Send response
		HttpServerResponse response = routingContext.response();
		final String origin = routingContext.request().getHeader("Origin");
		response.putHeader("Access-Control-Allow-Origin", origin);
		response.putHeader("content-type", "application/json")
				.end(arr.encodePrettily());
	}
	
	/**
	 * Handles GET request form dashboard controls' page: sends back the lights and blinds current values.
	 * 
	 * @param routingContext
	 */
	private void handleGetControls(RoutingContext routingContext) {
		//Build response
		JsonObject allData = new JsonObject();
		allData.put("light", rs.getPeripherals().getLed().toString());
		allData.put("degrees", rs.getPeripherals().getServo());
		allData.put("master", rs.getMaster().toString());
		
		//Send response
		HttpServerResponse response = routingContext.response();
		final String origin = routingContext.request().getHeader("Origin");
		response.putHeader("Access-Control-Allow-Origin", origin);
		response.putHeader("content-type", "application/json")
				.end(allData.encodePrettily());
	}
	
	/**
	 * Handles POST request from ESP: sets presence and brightness values in peripherals.
	 * 
	 * @param routingContext
	 */
	private void handlePostESPData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			//Update presence and darkness values;
			rs.getPeripherals().setPresence(res.getBoolean("presence"));
			rs.getPeripherals().setBrightness(res.getBoolean("brightness"));

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

	private void log(final String msg) {
		System.out.println("[HTTP SERVER] "+msg);
	}

}