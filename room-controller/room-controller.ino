#include "async_fsm.h"
#include "Led.h"
#include "SerialPortImpl.h"
#include "EventHandler.h"

#include <Servo.h>

#define SERIAL_IN_PIN 0
#define SERVOPIN 3
#define LEDPIN 13

EventHandler* eventHandler;

void setup() {
  Led* led = new Led(LEDPIN);
  SerialPort* comPort = new SerialPortImpl(SERIAL_IN_PIN);
  Servo servo;
  servo.attach(SERVOPIN);
  eventHandler = new EventHandler(comPort, led, servo);
}

void loop() {
  eventHandler->checkEvents();
}
