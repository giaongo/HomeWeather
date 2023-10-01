// ArduinoBLE - Version: Latest 
#include <ArduinoBLE.h>

// DHT sensor library - Version: Latest 
#include <DHT.h>
#include <DHT_U.h>

#define DHT22_PIN 7
#define DHTTYPE DHT22
DHT dht(DHT22_PIN, DHTTYPE);
BLEService newService("180A"); 
BLEFloatCharacteristic temperatureReading("2A57", BLERead | BLENotify);
BLEFloatCharacteristic humidityReading("2A58", BLERead | BLENotify);

// Variable
float humidity; // store humidity value
float temperature; // store temperature value
long previousMillis = 0;

void setup() {
    Serial.begin(9600);   
    pinMode(LED_BUILTIN, OUTPUT);
    if(!BLE.begin()) {
      Serial.println("Starting Bluetooth Low Energy fails");
      while(1);
    }
    dht.begin();
    BLE.setLocalName("MKR Temp&Humi");// setting a name that will appear while scanning
    BLE.setAdvertisedService(newService); 
    newService.addCharacteristic(temperatureReading); // add characteristic to the service
    newService.addCharacteristic(humidityReading);
    BLE.addService(newService); // adding the service
    temperatureReading.writeValue(0); // setting the intial value
    humidityReading.writeValue(0);
    
    BLE.advertise(); // start advertising the service
    Serial.println("Bluetooth device active, waiting for connection...");
    
}

void loop() {
    BLEDevice central = BLE.central();
    if(central) {
      Serial.print("Connected to central: ");
      Serial.println(central.address());
      digitalWrite(LED_BUILTIN,HIGH);
      while(central.connected()) {
        long currentMillis = millis();
        if(currentMillis - previousMillis >= 60000) {
          previousMillis = currentMillis;
          humidity = dht.readHumidity();
          temperature = dht.readTemperature();
          humidityReading.writeValue(humidity);
          temperatureReading.writeValue(-25.5);
          
          // Print temp and humidity value to serial monitor
          Serial.print("Humidity; ");
          Serial.print(humidity);
          Serial.print(" %, Temp: ");
          Serial.print(temperature);
          Serial.println(" Celsius");
        }
      }
      digitalWrite(LED_BUILTIN, LOW); // when the central disconnects -> turn off the LED
      Serial.print("Disconnected from central: ");
      Serial.println(central.address());
    } 
}
