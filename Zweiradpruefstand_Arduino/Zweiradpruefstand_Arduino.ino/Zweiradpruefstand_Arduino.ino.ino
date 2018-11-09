#include <Adafruit_BMP085.h>
#include <Wire.h>

//-Definations----------------------------------------------------------//
//Analog Devices
int engTempPin = A0;
int exhTempPin = A1;

//Digital Devices
int rearRPMPin = 2;
int engRPMPin = 1;

//EnvTemp and EnvPress at TWI-Interface (BMP180)

//-Declarations----------------------------------------------------------//
//Devices
Adafruit_BMP085 bmp;

//Environment
double envTemp;
double envPress;

//Thermos


//-Functions------------------------------------------------------------//
void readEnvironment () {
    if (!bmp.begin()) {
        Serial.println("Error 180: Environment-Sensor not found");
        return;
    }
    envTemp = bmp.readTemperature();
    envPress = bmp.readPressure();
}

void readSparkplug() {
                                                                                                     
}

void readRearWheel() {

}

void readThermos() {
    // ENGINE
    Serial.println(analogRead(engTempPin));

    // EXHAUST
    Serial.println(analogRead(exhTempPin));
}

void setup() {
    Serial.begin(57600);
}

void loop() {
    Serial.println((analogRead(A0)*5.0)/1024);
    delay(100);
}