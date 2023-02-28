#include "SerialPortImpl.h"
#include "Arduino.h"

SerialPortImpl::SerialPortImpl(int pin) {
    Serial.begin(9600);
    while(!Serial) {}
    this->pin = pin;
    bindInterrupt(pin);
}

bool SerialPortImpl::isDataAvailable() {
    return Serial.available() > 0;
}

void SerialPortImpl::notifyInterrupt(int pin) {
    Event* ev;
    if (isDataAvailable()) {
        ev = new DataAvailable(this);
        this->generateEvent(ev);
    }
}