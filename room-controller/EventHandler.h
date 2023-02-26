#ifndef __EVENTHANDLER__
#define __EVENTHANDLER__

#include "async_fsm.h"
#include "Led.h"
#include "SerialPortImpl.h"
#include "BluetoothPortImpl.h"

#include <ServoTimer2.h>
#include <ctype.h>

#define BUFFER_SIZE 40
#define MAX_COMMANDS 5

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
      String formatted;
      int j = 0;
      String commands[MAX_COMMANDS];
      for (int i = 0; i < msg.length(); i++) {
        if (msg.charAt(i) == '\n') {
          commands[j++] = formatted;
          formatted = "";
        } else {
          formatted += msg.charAt(i);
        }
      }

      /*Response for debugging*/
      Serial.print("Received: ");
      for (int i = 0; i < MAX_COMMANDS && commands[i] != NULL; i++) {
        Serial.print(commands[i] + " ");
      }
      Serial.print("from ");
      if (evType == DATA_AVAILABLE_EVENT){
        Serial.println("Serial Port");
      } else {
        Serial.println("BT Port");
      }

      /*Command handling*/
      bool flag = true;
      for (int i = 0; i < MAX_COMMANDS && commands[i] != NULL; i++) {
        for (j = 1; j < commands[i].length(); j++) {
          if (!isDigit(commands[i].charAt(j))) {
            flag = false;
          }
        }
        char first = commands[i].charAt(0);
        if (flag && (isDigit(first) || first == '-')) {
          int val = commands[i].toInt();
          val = (val >= 0 ? (val <= 180 ? map(val,0,180,750,2250) : 2250) : 750);
          servo.write(val);
        } else if (commands[i] == "ON") {
          this->btPort->println("LED is turned on\n");
          led->switchOn();
        } else if (commands[i] == "OFF") {
          this->btPort->println("LED is turned off\n");
          led->switchOff();
        }
        commands[i] = "";
      }
    }
};

#endif