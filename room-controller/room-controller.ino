#include "async_fsm.h"
#include "Led.h"
#include "SerialPortImpl.h"
#include "EventHandler.h"
//#include "BluetoothPortImpl.h"

#include <ServoTimer2.h>

#define SERIAL_IN_PIN 0
#define SERVOPIN 5
#define LEDPIN 7

EventHandler* eventHandler;

void setup() {
  Led* led = new Led(LEDPIN);
  SerialPort* comPort = new SerialPortImpl(SERIAL_IN_PIN);
  ServoTimer2 servo;
  servo.attach(SERVOPIN);
  eventHandler = new EventHandler(comPort, led, servo);
}

void loop() {
  eventHandler->checkEvents();
}
