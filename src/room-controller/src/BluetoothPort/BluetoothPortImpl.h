#ifndef __BTPORTIMPL__
#define __BTPORTIMPL__

#include "BluetoothPort.h"
#include "../lib/AltSoftSerial/AltSoftSerial.h"

class BTPortImpl : public BTPort {
    public:
        BTPortImpl(int pin);
        virtual bool isBTDataAvailable();
        virtual int readData();
        virtual void println(String s);
        virtual void notifyInterrupt(int pin);

    private:
        int pin;
        AltSoftSerial channel;
};

#endif