#include "DHT.h"

#define DHTPIN 7  
#define DHTTYPE DHT11
#define MOISTUREPIN A4
#define WATERPIN A3
#define LIGHTPIN A5
#define RELAYPIN 2

// Initialize DHT sensor for normal 16mhz Arduino
DHT dht(DHTPIN, DHTTYPE);

boolean waterFlag = false;
int waterCycles = 0;
char message = ' ';
float humidity = 0;
float temperature = 0;
int moistureAnalog = 0;
String moistureLevel = "dry";
int waterAnalog = 0;
String lowWater = "false";
int lightAnalog = 0;
String sunlight = "false";

void setup() {
  Serial.begin(9600); 
  dht.begin();
  pinMode(RELAYPIN, OUTPUT);
  digitalWrite(RELAYPIN, HIGH);
}

void loop() {
  
  readSerial();
  
  // Wait a few seconds between measurements.
  delay(1000);

  // Reading temperature or humidity takes about 250 milliseconds
  humidity = dht.readHumidity();
  // Read temperature as Celsius
  temperature = dht.readTemperature();
  
  // Check if any reads failed
  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("dhtFail");
  }

  // Reading soil moisture
  moistureAnalog = analogRead(MOISTUREPIN); // Dry: >500   Wet: >350    Water <350

    // Check if any reads failed (not as useful for analog)
  if (isnan(moistureAnalog)) {
    Serial.println("soilFail");
  }

  if(moistureAnalog > 500) {
    moistureLevel = "dry";
  } else if(moistureAnalog > 350) {
    moistureLevel = "wet";
  } else {
    moistureLevel = "water";
  }
  
  // Reading Water Sensor
  waterAnalog = analogRead(WATERPIN);

  // Check if any reads failed (not as useful for analog)
  if (isnan(waterAnalog)) {
    Serial.println("waterFail");
  }
  
  if(waterAnalog > 550) {
    lowWater = "false";
  } else {
    lowWater = "true";
  }

  // Reading Light Sensor (not as useful for analog)
  int lightLevel = analogRead(LIGHTPIN);

  // Check if any reads failed
  if (isnan(lightLevel)) {
    Serial.println("lightFail");
  }
  
  if(lightLevel > 800) {
    sunlight = "true";
  } else {
    sunlight = "false";
  }

  String json = "{";
  json += "\"temperature\":" + String(temperature);
  json += ",\"humidity\":" + String(humidity);
  json += ",\"soilMoisture\":\"" + moistureLevel + "\"";
  json += ",\"sunlight\":" + sunlight;
  json += ",\"lowWater\":" + lowWater;
  json += "}";

  Serial.println("reportSensorData " + json);
}

void readSerial() {
  if(Serial.available() > 0) {
    message = Serial.read();
  }

  // For each 'w' sent at once, the pump stays on for 2 seconds
  if(message == 'w') {
    waterFlag = true;
    message = ' ';
  }

  if(message == 'p') {
    Serial.println("pingAck");
    message = ' ';
  }
  
  if(waterFlag) {
    digitalWrite(RELAYPIN, LOW);
    Serial.println("plantWatered");
    waterFlag = false;
  } else {
    digitalWrite(RELAYPIN, HIGH);
  }
}
