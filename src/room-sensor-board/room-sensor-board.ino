#include <WiFi.h>
#include <HTTPClient.h>

#define LSPIN 4  //Photoresistor pin
#define PIRPIN 5 //Pir pin

int pir, lightSensor;
double lsVolt;
bool presence;
bool brightness;

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
    pinMode(LSPIN,INPUT);
  pinMode(PIRPIN,INPUT);
  connectToWifi(ssid, password);
}

void loop() {
  if (WiFi.status()== WL_CONNECTED){   
    //Reading and printing of PIR's value.
    pir = digitalRead(PIRPIN);
    Serial.print("Valore del pir: ");
    Serial.println(pir);
    presence = false;
    if (pir == 1) {
      presence = true;
    }

    //Reading and printing of photoresistor's value.
    lightSensor = analogRead(LSPIN);
    lsVolt = ((double) lightSensor) * 5/1024;
    lsVolt = lsVolt/5.0;  
    Serial.print("Valore del fotoresistore: ");
    Serial.println(lsVolt);
    brightness = false;
    if (lsVolt >= 1.5) {
      brightness = true;
    }

    //Sending retrieved data
    int code = sendData(serviceURI, presence, brightness);   
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
