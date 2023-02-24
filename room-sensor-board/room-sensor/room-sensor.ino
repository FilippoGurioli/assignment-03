#define LSPIN 4  //Photoresistor pin
#define PIRPIN 5 //Pir pin

int pir, lightSensor;
double lsVolt;

void setup() {
  Serial.begin(115200);
  pinMode(LSPIN,INPUT);
  pinMode(PIRPIN,INPUT);
}

void loop() {
  //Reading and printing of PIR's value.
  pir = digitalRead(PIRPIN);
  Serial.print("Valore del pir: ");
  Serial.println(pir);

  //Reading and printing of photoresistor's value.
  lightSensor = analogRead(LSPIN);
  lsVolt = ((double) lightSensor) * 5/1024;
  lsVolt = lsVolt/5.0;  
  Serial.print("Valore del fotoresistore: ");
  Serial.println(lsVolt);

  delay(1000);
}
