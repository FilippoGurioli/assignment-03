#include "async_fsm.h"
#include "Led.h"
#include "SerialPortImpl.h"

#include <Servo.h>
#include <ctype.h>

#define SERIAL_IN_PIN 0
#define SERVOPIN 3
#define LEDPIN 13

#define BUFFER_SIZE 40
#define MAX_COMMANDS 5

class EventHandler : public AsyncFSM {
  public:
    EventHandler(SerialPort* comPort, Led* led){
      this->comPort = comPort;
      this->comPort->registerObserver(this);
      this->led = led;
      led->switchOff();
      myservo.attach(SERVOPIN);
    }
  
    void handleEvent(Event* ev) {
      String commands[MAX_COMMANDS];
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
        if (flag && (isDigit(commands[i].charAt(0)) || commands[i].charAt(0) == '-')) {
          int val = commands[i].toInt();
          val = (val >= 0 ? (val <= 180 ? val : 180) : 0);
          Serial.println("Servo rotation");
          myservo.write(val);
        } else if (commands[i] == "ON") {
          Serial.println("Turning on led");
          led->switchOn();
        } else if (commands[i] == "OFF") {
          Serial.println("Turning off led");
          led->switchOff();
        } else {
          Serial.println("This command does not exist, try again...");
        }
      }
    }

  private:
    Led* led;
    SerialPort* comPort;
    char buffer[BUFFER_SIZE];
    Servo myservo;
};

EventHandler* eventHandler;

void setup() {
  Led* led = new Led(LEDPIN);
  SerialPort* comPort = new SerialPortImpl(SERIAL_IN_PIN);
  eventHandler = new EventHandler(comPort, led);
}

void loop() {
  eventHandler->checkEvents();
}
