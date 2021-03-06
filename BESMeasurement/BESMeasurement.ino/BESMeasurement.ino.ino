#include <Adafruit_BMP085.h>
#include <Wire.h>
#include <avr/pgmspace.h>

//-Definations----------------------------------------------------------//
//Analog Devices
int engTempPin = A0;
int exhTempPin = A1;

//Digital Devices
int rearRPMPin = 3;
int engRPMPin = 2;
int statusPinF = 4;
int statusPinW = 5;
int statusPinS = 6;
int workingPin1 = 7;
int workingPin2 = 8;

//Version
float progvers = 1.0;

//-Declarations----------------------------------------------------------//
void serialEvent();

//Devices
Adafruit_BMP085 bmp;

//Environment
float envTemp;
float envPress;
float envAlt;

//Thermos
float engTemp;
float exhTemp;
float u_eng;
float u_exh;
uint8_t measCount;

//RPM
unsigned long dEngTime;
unsigned long dEngHigh;
unsigned long dEngLow;
unsigned long lastEngTime;
unsigned long dWheelTime;
unsigned long dWheelHigh;
unsigned long dWheelLow;
unsigned long lastWheelTime;

unsigned long refMicros;

//Measurement
boolean continous;
boolean temp;

//-Functions------------------------------------------------------------//

//CRC Table
PROGMEM const uint32_t crc_table[16] = {
  0x00000000, 0x1db71064, 0x3b6e20c8, 0x26d930ac,
  0x76dc4190, 0x6b6b51f4, 0x4db26158, 0x5005713c,
  0xedb88320, 0xf00f9344, 0xd6d6a3e8, 0xcb61b38c,
  0x9b64c2b0, 0x86d3d2d4, 0xa00ae278, 0xbdbdf21c
};

//CRC Functions
unsigned long crc_update(unsigned long crc, byte data)
{
  byte tbl_idx;
  tbl_idx = crc ^ (data >> (0 * 4));
  crc = pgm_read_dword_near(crc_table + (tbl_idx & 0x0f)) ^ (crc >> 4);
  tbl_idx = crc ^ (data >> (1 * 4));
  crc = pgm_read_dword_near(crc_table + (tbl_idx & 0x0f)) ^ (crc >> 4);
  return crc;
}

unsigned long crc_string(char *s)
{
  unsigned long crc = ~0L;
  while (*s)
    crc = crc_update(crc, *s++);
  crc = ~crc;

  return crc;
}

char* string2char(String command) {
  if (command.length() != 0) {
    char *p = const_cast<char*>(command.c_str());
    return p;
  }
}

String createCRC(String msg) {
  return String(crc_string(string2char(msg)));
}

String createTelegram(String msg) {
  return ':' + msg + '>' + createCRC(msg) + ';';
}

//Measurement Functions
boolean readEnvironment () {
  if (!bmp.begin()) {
    setStatusSevere();
    return false;
  } else {
    envTemp = bmp.readTemperature();
    envPress = bmp.readPressure();
    envAlt = bmp.readAltitude();
    if (envPress == 0) {
      setStatusWarning();
    }
    return true;
  }
}

void readThermos() {
  //ENGINE
  u_eng = (analogRead(A0) * 4.5) / 1024;
  engTemp = (u_eng - 1.248) / 0.005;

  //EXHAUST
  u_exh = (analogRead(A1) * 4.5) / 1024;
  exhTemp = (u_exh - 1.248) / 0.005;

  if (engTemp <= 0 || exhTemp <= 0) {
    setStatusWarning();
  }
}

//Status LED Functions
void setStatusFine() {
  digitalWrite(statusPinF, LOW);
  digitalWrite(statusPinW, HIGH);
  digitalWrite(statusPinS, HIGH);
}

void setStatusWarning() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, LOW);
  digitalWrite(statusPinS, HIGH);
}

void setStatusSevere() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, HIGH);
  digitalWrite(statusPinS, LOW);
}

void setStatusMaxProblems() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, LOW);
  digitalWrite(statusPinS, LOW);
}

void setNoStatus() {
  digitalWrite(statusPinF, LOW);
  digitalWrite(statusPinW, LOW);
  digitalWrite(statusPinS, LOW);
}

void visualizeInitialization() {
  setStatusFine();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusSevere();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusFine();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusSevere();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusFine();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusSevere();
  delay(50);
  setStatusWarning();
}

void visualizeInitComplete() {
  setStatusFine();
  delay(200);
  setStatusWarning();
  delay(200);
  setStatusSevere();
  delay(200);
  setStatusWarning();
  delay(200);
  setStatusFine();
  delay(200);
  setStatusWarning();
}

void toggleWorkingLed1() {
  digitalWrite(workingPin1, !digitalRead(workingPin1));
}

void toggleWorkingLed2() {
  digitalWrite(workingPin2, !digitalRead(workingPin2));
}

//Reset Variables
void resetMeasurement() {
  refMicros = micros();
}

//-Setup-once called----------------------------------------------------------//
void setup() {
  Serial.begin(57600, SERIAL_8N1);

  pinMode(workingPin1, OUTPUT);
  pinMode(workingPin2, OUTPUT);
  pinMode(statusPinF, OUTPUT);
  pinMode(statusPinW, OUTPUT);
  pinMode(statusPinS, OUTPUT);

  pinMode(engRPMPin, INPUT_PULLUP);
  pinMode(rearRPMPin, INPUT);

  digitalWrite(workingPin1, LOW);
  digitalWrite(workingPin2, LOW);

  visualizeInitialization();
  setNoStatus();

  analogReference(EXTERNAL);

  resetMeasurement();
  
  dEngTime = 0;
  dWheelTime = 0;

  lastEngTime = 0;
  lastWheelTime = 0;
  
  dEngLow = 0;
  dEngHigh = 0;
  dWheelHigh = 0;
  dWheelLow = 0;
  
  measCount = 9;

  continous = false;
  temp = false;

  attachInterrupt(digitalPinToInterrupt(engRPMPin), engISR, RISING);
  attachInterrupt(digitalPinToInterrupt(rearRPMPin), rearISR, RISING);
}


//-Main---------------------------------------------------------------------//
void loop() {
  dEngLow = pulseInLong(engRPMPin, LOW, 10000);
  dEngHigh = pulseInLong(engRPMPin, HIGH, 10000);
  dEngTime = dEngLow + dEngHigh;

  
  
  dWheelLow = pulseInLong(rearRPMPin, LOW, 10000);
  dWheelHigh = pulseInLong(rearRPMPin, HIGH, 10000);
  dWheelTime = dWheelLow + dWheelHigh;
  
  if (continous) {
    if (temp) {
      if (measCount == 9) {
        readThermos();
        measCount = 0;
      }
      measCount++;
      String meas = String(dEngTime) + '#' + String(dWheelTime) + '#' + String(engTemp) + '#' + String(exhTemp) + '#' + String(micros()-refMicros);
      Serial.println(createTelegram(meas));
      Serial.flush();
    } else {
      String meas = String(dEngTime) + '#' + String(dWheelTime) + '#' + String(micros()-refMicros);
      Serial.println(createTelegram(meas));
      Serial.flush();
    }
  }
}


//-ISR-------------------------------------------------------------------//
//ISR for Engine
void engISR() {
  toggleWorkingLed1();
}

//ISR for Rear-Wheel
void rearISR() {
  toggleWorkingLed2();
}

//ISR for Communication

//INIT:        :BESDyno>crc;
//START:       :envTemp#envPress#envAlt>crc;
//ENGINE:      :engTemp#exhTemp>crc;
//ALL:         :engTime#rearTime#engTemp#exhTemp#Time>crc;
//MEASURE:     :engTime#rearTime#Time>crc;
//MEASURENO:   :rearTime#Time>crc;
//FINE:        :FINE>crc;
//WARNING:     :WARNING>crc;
//SEVERE:      :SEVERE>crc;
//MAXPROBLEMS: :MAXPROBLEMS>crc;
//KILL:        :KILL>CRC;
//VERSION:     :version>crc;
//DEBUG:       readable message in the terminal

//CRC is calculated without ':' and ';'
//Every Response: :Message>Checksum;

void serialEvent() {
  while (Serial.available()) {

    char req = (char)Serial.read();

    if (req == 'i') {
      visualizeInitialization();
      String init = "BESDyno";
      Serial.println(createTelegram(init));
      Serial.flush();
      delay(50);
      visualizeInitComplete();

    } else if (req == 's') {
      setStatusFine();
      if (readEnvironment()) {
        String environment = String(envTemp) + '#' + String(envPress) + '#' + String(envAlt);
        Serial.println(createTelegram(environment));
      } else {
        Serial.println(createTelegram("BMP-ERROR"));
      }
      Serial.flush();
      resetMeasurement();

    } else if (req == 'e') {
      setStatusFine();
      readThermos();
      String thermos = String(engTemp) + '#' + String(exhTemp);
      Serial.println(createTelegram(thermos));
      Serial.flush();

    } else if (req == 'a') {
      measCount++;
      if (dEngTime > 0 && dWheelTime > 0) {
        setStatusFine();
      } else {
        setStatusWarning();
      }

      if (measCount == 9) {
        readThermos();
        measCount = 0;
      }

      String all = String(dEngTime) + '#' + String(dWheelTime) + '#' + String(engTemp) + '#' + String(exhTemp) + '#' + String(micros()-refMicros);
      Serial.println(createTelegram(all));
      Serial.flush();
      
    } else if (req == 'm') {
      if (dEngTime > 0 && dWheelTime > 0) {
        setStatusFine();
      } else {
        setStatusWarning();
      }
      
      String measure = String(dEngTime) + '#' + String(dWheelTime) + '#' + String(micros()-refMicros);
      Serial.println(createTelegram(measure));
      Serial.flush();

    } else if (req == 'n') {
      if (dWheelTime > 0) {
        setStatusFine();
      } else {
        setStatusWarning();
      }
      String measureno = String(dWheelTime) + '#' + String(micros()-refMicros);
      Serial.println(createTelegram(measureno));
      Serial.flush();

    } else if (req == 'f') {
      setStatusFine();
      Serial.println(createTelegram("FINE"));
      Serial.flush();

    } else if (req == 'w') {
      setStatusWarning();
      Serial.println(createTelegram("WARNING"));
      Serial.flush();

    } else if (req == 'v') {
      setStatusSevere();
      Serial.println(createTelegram("SEVERE"));
      Serial.flush();

    } else if (req == 'x') {
      setStatusMaxProblems();
      Serial.println(createTelegram("MAXPROBLEMS"));
      Serial.flush();

    } else if (req == 'k') {
      setStatusFine();
      Serial.println(createTelegram("KILL"));
      Serial.flush();
      resetMeasurement();
      
    } else if (req == 'p') {
      setStatusFine();
      Serial.println(createTelegram(String(progvers)));
      Serial.flush();
    } else if (req == 'd') {
      setNoStatus();
      readEnvironment();
      readThermos();
      Serial.println("Temperatur: " + String(envTemp));
      Serial.println("Luftdruck:  " + String(envPress));
      Serial.println("Höhenmeter: " + String(envAlt));
      Serial.println("A0 Motor:   " + String(engTemp));
      Serial.println("A0 U_Eng:   " + String(u_eng));
      Serial.println("A1 Abgas:   " + String(exhTemp));
      Serial.println("A1 U_Exh:   " + String(u_exh));
      Serial.println("D2 Motor:   " + String(dEngTime));
      Serial.println("D3 Walze:   " + String(dWheelTime));
      Serial.println("Zeit (µs):  " + String(micros()-refMicros) + "\n");
      Serial.flush();
    } else if (req == 'c') {
      continous = true;
      Serial.println(createTelegram("CS"));
      Serial.flush();
    } else if (req == 'z') {
      continous = false;
      Serial.println(createTelegram("CT"));
      Serial.flush();
    } else if (req == 't') {
      temp = true;
      Serial.println(createTelegram("TE"));
      Serial.flush();
    } else if (req == 'u') {
      temp = false;
      Serial.println(createTelegram("TD"));
      Serial.flush();
    }
  }
}

