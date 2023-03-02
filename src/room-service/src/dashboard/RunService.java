package dashboard;

import io.vertx.core.Vertx;

/*
 * Data Service as a vertx event-loop 
 */
public class RunService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HttpServer service = new HttpServer(8080);
		vertx.deployVerticle(service);
	}
	
}