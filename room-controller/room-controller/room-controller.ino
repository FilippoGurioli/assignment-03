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

const int BUFFER_SIZE = 20;
char buffer[BUFFER_SIZE];
int i = 0;
void loop() {
  bool flag = false;
  Serial.print("Inserisci un comando:");
  Serial.println(i++);
  while (!Serial.available()) {}     //wait for data available
  Serial.println(Serial.available());
  int rlen = Serial.readBytes(buffer, BUFFER_SIZE);
  Serial.end();
  Serial.begin(9600);
  /*String info = "";
  for(int i = 0; i < rlen; i++)
    info += buffer[i];
  if (info != NULL) {
    info.trim();
    Serial.println("Ricevuto: ");
    Serial.println(info);
  }*/
  /*flag = true;
  for (int i = 1; i < info.length(); i++) {
    if (!isDigit(info.charAt(i))) {
      flag = false;
    }
  }

  if (flag && (isDigit(info.charAt(0)) || info.charAt(0) == '-')) {
    int val = info.toInt();
    val = (val >= 0 ? (val <= 180 ? val : 180) : 0);
    Serial.print("Rotazione servo di ");
    Serial.println(val);
    myservo.write(val);
  } else if (info == "ON") {
    Serial.println("Accensione led");
    digitalWrite(LEDPIN, HIGH);
  } else if (info == "OFF") {
    Serial.println("Spegnimento led");
    digitalWrite(LEDPIN, LOW);
  } else {
    Serial.println("Comando errato, ritenta..");
  }*/
}
