#include "LightSensorImpl.h"
#include "Arduino.h"

LightSensorImpl::LightSensorImpl(int pin){
  this->pin = pin;
  pinMode(pin, INPUT);
}

double LightSensorImpl::getLightIntensity(){
  int value = analogRead(pin);
  double valueInVolt = ((double) value) * 5/1024;
  lightIntensity = valueInVolt/5.0;
  return lightIntensity;  
}

bool LightSensorImpl::isBright(){
  return lightIntensity >= 1.5;
}