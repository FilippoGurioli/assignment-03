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

const int BUFFER_SIZE = 40;
const int MAX_COMMANDS = 5;
char buffer[BUFFER_SIZE];

void loop() {
  String commands[MAX_COMMANDS];
  bool flag = false;
  Serial.println("Arduino listening.");
  while (!Serial.available()) {}
  int rlen = Serial.readBytes(buffer, BUFFER_SIZE);
  String info = String(buffer);

  //Communication Protocol: remove white spaces and separate different instructions
  String formatted;
  int j = 0;
  for (int i = 0; i < info.length(); i++) {
    if (info.charAt(i) != ' ' && info.charAt(i) != '\n') {
      formatted += info.charAt(i);
    }
    if (info.charAt(i) == '\n') {
      commands[j++] = formatted;
      formatted = "";
    }
  }
  //--------------------end of CommP----------------------------------------------

  for (int i = 0; i < MAX_COMMANDS; i++) {
    if (commands[i] != NULL) {
      Serial.print("Executing: ");
      Serial.print("\"");
      Serial.print(commands[i]);
      Serial.println("\"");
      flag = true;
      for (int j = 1; j < commands[i].length(); j++) {
        if (!isDigit(commands[i].charAt(j))) {
          flag = false;
        }
      }
      if (flag && (isDigit(commands[i].charAt(0)) || commands[i].charAt(0) == '-')) {
        int val = commands[i].toInt();
        val = (val >= 0 ? (val <= 180 ? val : 180) : 0);
        Serial.print("Servo rotation: ");
        Serial.println(val);
        myservo.write(val);
      } else if (commands[i] == "ON") {
        Serial.println("Turning on led");
        digitalWrite(LEDPIN, HIGH);
      } else if (commands[i] == "OFF") {
        Serial.println("Turning off led");
        digitalWrite(LEDPIN, LOW);
      } else {
        Serial.println("This command does not exist, try again...");
      }
    }
  }
}
