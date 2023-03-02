#include <WiFi.h>
#include <HTTPClient.h>
#include "devices/PirImpl.h"
#include "devices/LightSensorImpl.h"

#define LSPIN 4  //Photoresistor pin
#define PIRPIN 5 //Pir pin

PirImpl* pir;
LightSensorImpl* lightSensor;

const char* ssid = "Galaxy A519DFD";
const char* password = "aivaivaiv";
const char* serviceURI = "http://192.168.6.20:8080/api/ESPdata";

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

  http.addHeader("Content-Type", "application/json");
  String msg = "{ \"presence\": " + String(presence) + ", \"brightness\": \"" + String(brightness) +"\" }";

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
    int code = sendData(serviceURI, pirVal, lsVal);   
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
