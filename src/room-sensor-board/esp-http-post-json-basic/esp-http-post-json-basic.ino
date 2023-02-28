/*
 * HTTPClient lib --  Performing an HTTP POST to our REST service
 *
 * Remark:
 * - Going through ngrok
 *
 */
#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "WINTERFIRE-5G";
const char* password = "matt1a51lv1avanna5andr00";

//const char *serviceURI = "https://localhost:8080/api/ESPdata";

void connectToWifi(const char* ssid, const char* password){
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void setup() {
  Serial.begin(115200); 
  connectToWifi(ssid, password);
}

int sendData(bool presence, bool darkness){  
  
   HTTPClient http;

   http.begin("https://localhost:8080/api/ESPdata");

   http.addHeader("Content-Type", "application/json");
   String msg = 
    String("{ \"presence\": ") + presence + 
    ", \"darkness\": \"" + darkness +"\" }";
   

   //http.addHeader("Content-Type", "text/plain");
   //int httpResponseCode = http.POST("Hello, World!");
   
   int retCode = http.POST(msg);   
   http.end();  
      
   return retCode;
}

void loop() {
  if (WiFi.status()== WL_CONNECTED){      

    int code = sendData(true, true);
Serial.println("inviato");    
    if (code == 200){
       Serial.println("ok");   
     } else {
       Serial.println(String("error: ") + code);
     }
    
    delay(5000);

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}
