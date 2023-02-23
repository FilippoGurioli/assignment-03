#ifndef __BTPORTIMPL__
#define __BTPORTIMPL__

#include "BluetoothPort.h"
//#include "AltSoftSerial.h"

class BTPortImpl : public BTPort {
    public:
        BTPortImpl(int pin);
        virtual bool isBTDataAvailable();
        virtual void notifyInterrupt(int pin);

    private:
        int pin;
        //AltSoftSerial channel;
};

#endif