#ifndef __EVENTHANDLER__
#define __EVENTHANDLER__

#include "async_fsm/async_fsm.h"
#include "Led/Led.h"
#include "RollerBlinds/RollerBlinds.h"
#include "SerialPort/SerialPortImpl.h"
#include "BluetoothPort/BluetoothPortImpl.h"

#include <ctype.h>

#define BUFFER_SIZE 40

class EventHandler : public AsyncFSM {

  private:
    Led* led;
    SerialPort* comPort;
    BTPort* btPort;
    char buffer[BUFFER_SIZE];
    RollerBlinds* blinds;

  public:
    EventHandler(SerialPort* comPort, Led* led, RollerBlinds* blinds, BTPort* btPort){
      this->comPort = comPort;
      this->comPort->registerObserver(this);
      this->btPort = btPort;
      this->btPort->registerObserver(this);
      this->led = led;
      this->blinds = blinds;
      this->led->switchOff();   //Default Start: led OFF
    }
  
    void handleEvent(Event* ev) {
      /*Listening the msg*/
      int evType = ev->getType();
      String msg = read(evType);

      /*Command handling*/
      bool flag = true;
      for (int i = 1; i < msg.length(); i++) {
        if (!isDigit(msg.charAt(i))) {
          flag = false;
        }
      }
      char first = msg.charAt(0);
      if (flag && (isDigit(first) || first == '-')) {
        this->blinds->open(msg.toInt());
      } else if (msg == "ON") {
        led->switchOn();
      } else if (msg == "OFF") {
        led->switchOff();
      }
      this->broadcastCommand(msg);
      msg = "";
    }

    String read(int evType) {
      String msg;
      int ch;
        do {
          ch = evType == DATA_AVAILABLE_EVENT ? Serial.read() : evType == BLUETOOTH_EVENT ? this->btPort->readData() : -1;
          if (ch != -1) {
            msg += (char) ch;
          }
        } while(ch != '\n');
      /*Formatting the msg*/
      msg.remove(msg.length()-1);
      return msg;
    }

    /*
    Communication protocols:
    Arduino to Java - must have "/" before and after the command
    Java to Arduino - must have "\n" at the end of the command
    */
    void broadcastCommand(String command) {
      Serial.print("/" + command + "/");
      this->btPort->println(command);
    }
};

#endif