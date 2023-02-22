#include "async_fsm.h"
#include "SerialPortImpl.h"
#include "led.h"

#define LED_PIN 13
#define BUTTON_PIN 7

class ButtonLedAsyncFSM : public AsyncFSM {
  public:
    ButtonLedAsyncFSM(SerialPort* comPort, Led* led){
      count = 0;  
      currentState = OFF;
      this->comPort = comPort;
      this->led = led;
      led->switchOff();
      comPort->registerObserver(this);
    }
  
    void handleEvent(Event* ev) {
      switch (currentState) {
      case OFF:  
        if (ev->getType() == DATA_AVAILABLE_EVENT){
          delay(20);
          led->switchOn();
          count = count + 1;
          currentState = ON;
        }
        break; 
      case ON: 
        if (ev->getType() == BUTTON_RELEASED_EVENT){
          delay(20);
          led->switchOff();
          count = count + 1;
          currentState = OFF;
        }
      }
    }

  private:
    int count; 
    SerialPort* comPort;
    Led* led;
    enum  { ON, OFF} currentState;
};

ButtonLedAsyncFSM* myAsyncFSM;

void setup() {
  SerialPort* comPort = new SerialPortImpl(BUTTON_PIN);
  Led* led = new Led(LED_PIN);
  myAsyncFSM = new ButtonLedAsyncFSM(comPort, led);
}

void loop() {
  myAsyncFSM->checkEvents();
}