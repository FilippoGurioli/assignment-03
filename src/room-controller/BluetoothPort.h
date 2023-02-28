#ifndef __BLUETOOTHPORT__
#define __BLUETOOTHPORT__

#include "async_fsm.h"
#include "Arduino.h"

#define BLUETOOTH_EVENT 2

class BTPort : public EventSource {
    public:
        virtual bool isBTDataAvailable() = 0;
        virtual int readData() = 0;
        virtual void println(String s) = 0;
};

class BTDataAvailable : public Event {
    public:
        BTDataAvailable(BTPort* source) : Event(BLUETOOTH_EVENT) {
            this->source = source;
        }

        BTPort* getSource() {
            return this->source;
        }
    
    private:
        BTPort* source;
};

#endif