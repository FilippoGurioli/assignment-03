const int ledPin = 7; // Built in LED in Arduino board
String msg,cmd;

#include "AltSoftSerial.h"
#include <ServoTimer2.h>

AltSoftSerial channel;
ServoTimer2 servo;

void setup() {
  // Initialization
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  Serial.begin(9600); // Communication rate of the Bluetooth Module
  channel.begin(9600);
  servo.attach(5);
  msg = "";
}

void loop() {
  int ch;
  while(!channel.available()) {}
  do {
    ch = channel.read();
    if (ch != -1) {
      msg += (char) ch;
    }
  } while(ch != '>');
  Serial.println("Ricevuto: " + msg);
  
  // Control LED in Arduino board
  if (msg == "<turn on>"){
    digitalWrite(ledPin, HIGH); // Turn on LED
    Serial.println("LED is turned on\n"); // Then send status message to Android
    channel.println("LED is turned on\n");
    msg = ""; // reset command
    servo.write(750);
  } else {
    if (msg == "<turn off>"){
      digitalWrite(ledPin, LOW); // Turn off LED
      Serial.println("LED is turned off\n"); // Then send status message to Android
      channel.println("LED is turned off\n");
      msg = ""; // reset command
      servo.write(2250);
    }
  }
}