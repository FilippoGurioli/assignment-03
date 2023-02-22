 #ifndef __SERIALPORTIMPL__
#define __SERIALPORTIMPL__

#include "SerialPort.h"

class SerialPortImpl: public SerialPort {
 
public: 
  SerialPortImpl(int pin);
  virtual bool isDataAvailable();
  virtual void notifyInterrupt(int pin);
  
private:
  int pin;
  long lastEventTime;
};

#endif
