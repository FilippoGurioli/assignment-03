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
      led->switchOff();
      this->servo = servo;
    }
  
    void handleEvent(Event* ev) {
      /*Listening the msg from the right port*/
      int evType = ev->getType();
      String msg;
      if (evType == DATA_AVAILABLE_EVENT) {
        Serial.readBytes(buffer, BUFFER_SIZE);
        msg = String(buffer);
      } else if (evType == BLUETOOTH_EVENT) {
        int ch;
        do {
          ch = this->btPort->readData();
            if (ch != -1) {
              msg += (char) ch;
            }
          } while(ch != '\n');
      }

      /*Formatting the msg to something useful to Arduino*/
      String formatted;
      int j = 0;
      String commands[MAX_COMMANDS];
      for (int i = 0; i < msg.length(); i++) {
        if (msg.charAt(i) != ' ' && msg.charAt(i) != '\n') {
          formatted += msg.charAt(i);
        }
        if (msg.charAt(i) == '\n') {
          commands[j++] = formatted;
          formatted = "";
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
          Serial.println("Servo rotation");
          servo.write(val);
        } else if (commands[i] == "ON") {
          Serial.println("Turning on led");
          this->btPort->println("LED is turned on\n");
          led->switchOn();
        } else if (commands[i] == "OFF") {
          Serial.println("Turning off led");
          this->btPort->println("LED is turned off\n");
          led->switchOff();
        }
      }
    }
};

#endif