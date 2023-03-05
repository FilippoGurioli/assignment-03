#ifndef __ROLLERBLINDS__
#define __ROLLERBLINDS__

#include "Arduino.h"
#include "../lib/ServoTimer2/ServoTimer2.h"
#include "Servo.h"

class RollerBlinds: public Servo {
    public:
        RollerBlinds(int pin);
        void open(int angle);
        int getOpening();
    private:
        ServoTimer2 servo;
        int opening;
};

#endif