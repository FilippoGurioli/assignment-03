#include <Servo.h>
#include <ctype.h>

#define LEDPIN 2
#define SERVO 3

bool a = false;
Servo myservo;
int deg = 0;

void setup() {
  pinMode(LEDPIN, OUTPUT);
  myservo.attach(SERVO);
  Serial.begin(9600);
  delay(1000);
}

bool flag = false;
void loop() {
  Serial.write("Halleluja\n");
  delay(3000);
  digitalWrite(LEDPIN, flag);
  flag = !flag;
  /*bool flag = false;
  //Serial.println("Inserisci un comando:");
  while (!Serial.available()) {
    digitalWrite(LEDPIN, flag);
    flag = !flag;
    delay(500);
  }     //wait for data available
  String teststr = Serial.readString();  //read until timeout
  if (teststr != NULL) {
    digitalWrite(LEDPIN, HIGH);
    delay(5000);
    Serial.write("pong");
  }
  teststr.trim();
  bool flag = true;
  for (int i = 1; i < teststr.length(); i++) {
    if (!isDigit(teststr.charAt(i))) {
      flag = false;
    }
  }

  if (flag && (isDigit(teststr.charAt(0)) || teststr.charAt(0) == '-')) {
    int val = teststr.toInt();
    val = (val >= 0 ? (val <= 180 ? val : 180) : 0);
    Serial.print("Rotazione servo di ");
    Serial.println(val);
    myservo.write(val);
  } else if (teststr == "ON") {
    Serial.println("Accensione led");
    digitalWrite(LEDPIN, HIGH);
  } else if (teststr == "OFF") {
    Serial.println("Spegnimento led");
    digitalWrite(LEDPIN, LOW);
  } else {
    Serial.println("Comando errato, ritenta..");
  }*/
}