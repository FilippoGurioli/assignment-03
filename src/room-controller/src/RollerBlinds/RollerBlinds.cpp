#include "RollerBlinds.h"
#include "Arduino.h"

RollerBlinds::RollerBlinds(int pin) {
    this->servo.attach(pin);
    this->servo.write(2250);
    this->opening = 180;
}

int RollerBlinds::getOpening() {
    return this->opening;
}

void RollerBlinds::open(int angle) {
    this->opening = angle;
    this->servo.write(angle >= 0 ? (angle <= 180 ? map(angle,0,180,750,2250) : 2250) : 750);
};