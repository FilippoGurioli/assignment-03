#include "src/async_fsm/async_fsm.h"
#include "src/Led/Led.h"
#include "src/SerialPort/SerialPortImpl.h"
#include "src/EventHandler.h"
#include "src/BluetoothPort/BluetoothPortImpl.h"
#include "src/RollerBlinds/RollerBlinds.h"

#define SERIAL_IN_PIN 0
#define SERVOPIN 5
#define LEDPIN 7
#define BT_IN_PIN 8

EventHandler* eventHandler;

void setup() {
  Led* led = new Led(LEDPIN);
  SerialPort* comPort = new SerialPortImpl(SERIAL_IN_PIN);
  BTPort* btPort = new BTPortImpl(BT_IN_PIN);
  RollerBlinds* rb = new RollerBlinds(SERVOPIN);
  eventHandler = new EventHandler(comPort, led, rb, btPort);
}

void loop() {
  eventHandler->checkEvents();
}
