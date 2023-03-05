#ifndef __SERVO__
#define __SERVO__

class Servo {
    public:
    virtual int getOpening() = 0;
    virtual void open(int perc) = 0;
};

#endif