/*
Communication protocols:
Arduino to Java - must have "/" before and after the command
Java to Arduino - must have "\n" at the end of the command
*/

#ifndef __EVENTHANDLER__
#define __EVENTHANDLER__

#include "async_fsm.h"
#include "Led.h"
#include "SerialPortImpl.h"
#include "BluetoothPortImpl.h"

#include <ServoTimer2.h>
#include <ctype.h>

#define BUFFER_SIZE 40

class EventHandler : public AsyncFSM {

  private:
    Led* led;
    SerialPort* comPort;
    BTPort* btPort;
    char buffer[BUFFER_SIZE];
    ServoTimer2 servo;

  public:
    EventHandler(SerialPort* comPort, Led* led, ServoTimer2 servo, BTPort* btPort){
      this->comPort = comPort;
      this->comPort->registerObserver(this);
      this->btPort = btPort;
      this->btPort->registerObserver(this);
      this->led = led;
      this->led->switchOff(); //Default Start: LED off
      this->servo = servo;
      this->servo.write(2250); //Default Start: servo fully closed (roller blinds closed)
    }
  
    void handleEvent(Event* ev) {
      /*Listening the msg from the right port*/
      int evType = ev->getType();
      String msg;
      int ch;
        do {
          ch = evType == DATA_AVAILABLE_EVENT ? Serial.read() : evType == BLUETOOTH_EVENT ? this->btPort->readData() : -1;
          if (ch != -1) {
            msg += (char) ch;
          }
          } while(ch != '\n');
      /*Formatting the msg to something useful to Arduino*/
      msg.remove(msg.length()-1);

      /*Response for debugging*/
      Serial.print("A-received: " + msg + '\n');

      /*Command handling*/
      bool flag = true;
      for (int i = 1; i < msg.length(); i++) {
        if (!isDigit(msg.charAt(i))) {
          flag = false;
        }
      }
      char first = msg.charAt(0);
      if (flag && (isDigit(first) || first == '-')) {
        int val = msg.toInt();
        Serial.print("/" + msg + "/");
        val = (val >= 0 ? (val <= 180 ? map(val,0,180,750,2250) : 2250) : 750);
        servo.write(val);
      } else if (msg == "ON") {
        this->btPort->println("LED is turned on\n");
        Serial.print("/ON/");
        led->switchOn();
      } else if (msg == "OFF") {
        this->btPort->println("LED is turned off\n");
        Serial.print("/OFF/");
        led->switchOff();
      }
      msg = "";
    }
};

#endif