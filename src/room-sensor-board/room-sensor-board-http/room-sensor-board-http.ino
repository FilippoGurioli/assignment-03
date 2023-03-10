#include <WiFi.h>
#include <HTTPClient.h>
#include "./src/devices/PirImpl.h"
#include "./src/devices/LightSensorImpl.h"

#define LSPIN 33  //Photoresistor pin
#define PIRPIN 26 //Pir pin

PirImpl* pir;
LightSensorImpl* lightSensor;
bool prevPirVal = false;
bool prevLsVal = false;

const char* ssid = "WINTERFIRE-5G";/*"Galaxy A519DFD";"ReteDiFilo";*/
const char* password = "matt1a51lv1avanna5andr00";/*"aivaivaiv";"pappapoppa";*/
const char* serviceURI = "http://192.168.178.22:8080/api/ESPdata";/*"http://26.70.255.91/8:8080/api/ESPdata";"http://192.168.6.20:8080/api/ESPdata";*/

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

int sendData(String adderss, bool presence, bool brightness){
  HTTPClient http;
  http.begin(adderss);

  //Converts from boolean to string
    String pres = presence ? "true" : "false";
    String bright = brightness ? "true" : "false";

  http.addHeader("Content-Type", "application/json");
  String msg = "{ \"presence\": " + pres + ", \"brightness\": " + bright +" }";

  int responseCode = http.POST(msg);   
  http.end();  
  return responseCode;
}

void setup() {
  Serial.begin(115200);
  pir = new PirImpl(PIRPIN);
  lightSensor = new LightSensorImpl(LSPIN);
  connectToWifi(ssid, password);
}

void loop() {
  if (WiFi.status()== WL_CONNECTED){   
    //Reading and printing of PIR's value.
    bool pirVal = pir->isDetected();
    Serial.print("Valore del pir: ");
    Serial.println(pirVal);

    //Reading and printing of photoresistor's value.
    bool lsVal = lightSensor->isBright();
    Serial.print("Valore del fotoresistore: ");
    Serial.println(lsVal);

    //Sending retrieved data
    if ((pirVal != prevPirVal) || (lsVal != prevLsVal)) {
      int code = sendData(serviceURI, pirVal, lsVal);   
      if (code == 200){
        Serial.println("ok");
      } else {
        Serial.println(String("error: ") + code);
      }

      prevPirVal = pirVal;
      prevLsVal = lsVal;
    }

    delay(2000);
  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}
