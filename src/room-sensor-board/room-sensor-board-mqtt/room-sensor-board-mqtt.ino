#include <WiFi.h>
#include <PubSubClient.h>
#include "./src/devices/PirImpl.h"
#include "./src/devices/LightSensorImpl.h"

#define MSG_BUFFER_SIZE  50
#define LSPIN 33  //Photoresistor pin
#define PIRPIN 26 //Pir pin

PirImpl* pir;
LightSensorImpl* lightSensor;
bool prevPirVal = false;
bool prevLsVal = false;

const char* ssid = "WINTERFIRE-5G";
const char* password = "matt1a51lv1avanna5andr00";
const char* mqtt_server = "192.168.178.22";

/* MQTT topic */
const char* topic = "pir/pr";

/* MQTT client management */
WiFiClient espClient;
PubSubClient client(espClient);

unsigned long lastMsgTime = 0;
char msg[MSG_BUFFER_SIZE];

void setup_wifi() {
  delay(10);
  Serial.println(String("Connecting to ") + ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

/* MQTT subscribing callback */
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.println(String("Message arrived on [") + topic + "] len: " + length );
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = String("esiot-2022-client-")+String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      client.subscribe(topic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void sendAllData(bool presence, bool brightness){
  //Creating a msg in the buffer
  snprintf (msg, MSG_BUFFER_SIZE, "{ \"presence\": %s, \"brightness\": %s }", presence ? "true" : "false", brightness ? "true" : "false");
  //Publishing the msg
  Serial.println(String("Publishing message: ") + msg);
  client.publish(topic, msg); 
}

void sendData(char* newTopic, String value){
  topic = newTopic;
  //Creating a msg in the buffer
  snprintf (msg, MSG_BUFFER_SIZE, "%s", value);
  //Publishing the msg
  Serial.println(String("Publishing message: ") + msg);
  client.publish(topic, msg); 
}

void setup() {
  Serial.begin(115200);
  pir = new PirImpl(PIRPIN);
  lightSensor = new LightSensorImpl(LSPIN);
  setup_wifi();
  randomSeed(micros());
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  unsigned long now = millis();
  if (now - lastMsgTime > 10000) {
    lastMsgTime = now;

    //Reading and printing of PIR's value.
    bool pirVal = pir->isDetected();
    Serial.print("Valore del pir: ");
    Serial.println(pirVal);

    //Reading and printing of photoresistor's value.
    bool lsVal = lightSensor->isBright();
    Serial.print("Valore del fotoresistore: ");
    Serial.println(lsVal);

    //Sending retrieved data
    /*if ((pirVal != prevPirVal) || (lsVal != prevLsVal)) {
      sendData(pirVal, lsVal);   
      prevPirVal = pirVal;
      prevLsVal = lsVal;
    }*/ 

    if (pirVal != prevPirVal) {
      sendData("PIR", pirVal ? "true" : "false");   
      prevPirVal = pirVal;
    } 
    if (lsVal != prevLsVal) {
      sendData("LS", lsVal ? "true" : "false");
      prevLsVal = lsVal;
    } 
  }
}
