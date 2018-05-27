int sensorPin = 4;
int counter = 0;
unsigned long lastTime=0;
unsigned long currentTime;
unsigned long timeDifference;
boolean sensorState = false;

void setup() 
{
  // setup serial - diagnostics - port
  Serial.begin(9600);
  pinMode(sensorPin, INPUT);
  digitalWrite(sensorPin, HIGH);
}

void loop() 
{
  if(magnetPresent(sensorPin) && !sensorState)
  {
    sensorState = true;
    printMessage("Count");
    counter++;
  }
  else if(!magnetPresent(sensorPin) && sensorState)
  {
    sensorState = false;
    //printMessage("Magnet Gone");
  }
}

void printMessage(String message){
  //Serial.println(message);
  //Serial.print("Time Difference: ");
  currentTime = millis();
  timeDifference = currentTime - lastTime;
  lastTime = currentTime;
 // Serial.println(timeDifference);
  //Serial.print("Estimated velocity: ");
  Serial.println(50000/timeDifference);
   //Serial.println(" ");
  //Serial.print(counter);
  //Serial.print(" ");
  
//  delay(1000);
}

boolean magnetPresent(int pin){
  return digitalRead(pin) == LOW;
}

