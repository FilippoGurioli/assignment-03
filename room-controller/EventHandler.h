#ifndef __EVENTHANDLER__
#define __EVENTHANDLER__

#include "async_fsm.h"
#include "Led.h"
#include "SerialPortImpl.h"

#include <ServoTimer2.h>
#include <ctype.h>

#define BUFFER_SIZE 40
#define MAX_COMMANDS 5

class EventHandler : public AsyncFSM {

  private:
    Led* led;
    SerialPort* comPort;
    char buffer[BUFFER_SIZE];
    ServoTimer2 servo;

  public:
    EventHandler(SerialPort* comPort, Led* led, ServoTimer2 servo){
      this->comPort = comPort;
      this->comPort->registerObserver(this);
      this->led = led;
      led->switchOff();
      this->servo = servo;
    }
  
    void handleEvent(Event* ev) {
      Serial.println("Evento");
      /*String commands[MAX_COMMANDS];
      bool flag = true;
      int j = 0;
      int rlen = Serial.readBytes(buffer, BUFFER_SIZE);
      String info = String(buffer);
      //Communication Protocol: remove white spaces and separate different instructions
      String formatted;
      for (int i = 0; i < info.length(); i++) {
        if (info.charAt(i) != ' ' && info.charAt(i) != '\n') {
          formatted += info.charAt(i);
        }
        if (info.charAt(i) == '\n') {
          commands[j++] = formatted;
          formatted = "";
        }
      }
      //--------------------end of CommP----------------------------------------------

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
          led->switchOn();
        } else if (commands[i] == "OFF") {
          Serial.println("Turning off led");
          led->switchOff();
        }
      }*/
    }
};

#endif