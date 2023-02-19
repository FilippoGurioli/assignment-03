#include <Servo.h>

#define LEDPIN 2
#define SERVO 3

bool a = false;
Servo myservo;
int deg = 0;

void setup() {
  pinMode(LEDPIN, OUTPUT);
  myservo.attach(SERVO);
  Serial.begin(9600);
}


void loop() {
  delay(1000);
  digitalWrite(LEDPIN, a);
  deg += 10;
  myservo.write(deg % 180);
  Serial.println(deg);
  a = !a;
}
