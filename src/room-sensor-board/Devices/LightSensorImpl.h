#ifndef __PHOTORESISTOR__
#define __PHOTORESISTOR__

#include "LightSensor.h"

class LightSensorImpl : public LightSensor {

  public: 
    LightSensorImpl(int pin);
    double getLightIntensity();
    bool isBright();
    
  private:
    int pin;
    double lightIntensity;

};

#endif