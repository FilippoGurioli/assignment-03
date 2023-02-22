#ifndef __SERIALPORT__
#define __SERIALPORT__

#include "async_fsm.h"

#define DATA_AVAILABLE_EVENT 1
#define BUTTON_RELEASED_EVENT 2

class SerialPort : public EventSource {
public: 
  virtual bool isDataAvailable() = 0;
};

class DataAvailable: public Event {
public:
  DataAvailable(SerialPort* source) : Event(DATA_AVAILABLE_EVENT){
    this->source = source;  
  } 
 
  SerialPort* getSource(){
    return source;
  } 
private:
  SerialPort* source;  
};

#endif
