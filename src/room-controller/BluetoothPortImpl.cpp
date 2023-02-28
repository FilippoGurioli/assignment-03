#include "BluetoothPortImpl.h"
#include "Arduino.h"

BTPortImpl::BTPortImpl(int pin) {
    this->channel.begin(9600);
    this->pin = pin;
    bindInterrupt(pin);
}

bool BTPortImpl::isBTDataAvailable() {
    return this->channel.available() > 0;
}

int BTPortImpl::readData() {
    return this->channel.read();
}

void BTPortImpl::println(String s) {
    this->channel.println(s);
}

void BTPortImpl::notifyInterrupt(int pin) {
    Event* ev;
    if (isBTDataAvailable()) {
        ev = new BTDataAvailable(this);
        this->generateEvent(ev);
    }
}