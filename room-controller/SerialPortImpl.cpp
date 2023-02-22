#include "SerialPortImpl.h"
#include "Arduino.h"

#define DEBOUNCING_TIME 20

SerialPortImpl::SerialPortImpl(int pin){
  this->pin = pin;
  pinMode(pin, INPUT);  
  bindInterrupt(pin);
  lastEventTime = millis();
} 
  
bool SerialPortImpl::isDataAvailable(){
  return digitalRead(pin) == HIGH;
}

void SerialPortImpl::notifyInterrupt(int pin){
  long curr = millis();
  if (curr - lastEventTime > DEBOUNCING_TIME){
        lastEventTime = curr;
        Event* ev;
        if (isDataAvailable()){
          ev = new DataAvailable(this);
        } else {
          ev = new ButtonReleased(this);
        }
        this->generateEvent(ev);
  }
}
