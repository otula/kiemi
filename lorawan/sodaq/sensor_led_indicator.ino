/**
 * Copyright 2019 Tampere University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Can be used to "burn in" new SGP30 and BME680 sensors. 
 * 
 * New sensors should be kept running for several hours before use to get reliable results.
 * 
 * By default, the code will print baseline values for SGP30 every 60 iterations, you can copy these to your code.
 * 
 * NOTE: remember to modify SODAQ_SERIAL to match the serial of your sodaq!
 * 
 */

#include <Sodaq_RN2483.h>
#include <Sodaq_wdt.h>
#include <RN487x_BLE.h>

//#include <FlashAsEEPROM.h>

#include <Wire.h>
#include <SPI.h>
#include "Adafruit_BME680.h"
#include "Adafruit_SGP30.h"

#define SODAQ_SERIAL "000000000AAAAAAA"

#define MORSE_DOT 500 // in ms (note: DOT:DASH:BREAK = 1:3:7)
#define MORSE_DASH 1500 // in ms (note: DOT:DASH:BREAK = 1:3:7)
#define MORSE_WORD_BREAK 3500 // in ms (note: DOT:DASH:BREAK = 1:3:7)

#define DEBUG_SERIAL SerialUSB

#define SGP30_WARMUP_DELAY 5000 // in ms

#define LOOP_DELAY 1000 // in ms

#define SGP30_BASELINE_ECO2 0x8262 // for ???/SGP30#0FDDF4D   BEST 8949/9085 MAYBE 879D
#define SGP30_BASELINE_TVOC 0x9029 // ???/SGP30#0FDDF4D       BEST 9261      MAYBE 8D58

#define BME680_SCK 12
#define BME680_MISO 11
#define BME680_MOSI 10
#define BME680_CS 9

//Adafruit_BME680 bme680; // I2C
//Adafruit_BME680 bme680(BME680_CS); // hardware SPI
Adafruit_BME680 bme680(BME680_CS, BME680_MOSI, BME680_MISO,  BME680_SCK); // SPI

Adafruit_SGP30 sgp30;

unsigned int counter = 1;
bool buttonToggle = false;
bool readBaseline = false;

struct SGP30config {
  uint16_t  serialnumber[3];
  uint16_t  eco2_base;
  uint16_t  tvoc_base;
};
SGP30config config;	//Variable to store custom object read from EEPROM.

/**
 * 
 */
void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(1000);

  while ((!DEBUG_SERIAL) && (millis() < 5000)) {
    // Wait for DEBUG_SERIAL to open
  }
  digitalWrite(LED_BUILTIN, LOW);

  DEBUG_SERIAL.println("Start");

  // Start streams
  DEBUG_SERIAL.begin(57600);

  // Configure the button as an input and enable the internal pull-up resistor
  pinMode(BUTTON, INPUT_PULLUP);
  bool forceResetEEPROM = digitalRead(BUTTON) == LOW;	// read button state for indicating of force eeprom reset
  if(forceResetEEPROM){
    for(int i=0; i<20; ++i){
      digitalWrite(LED_BUILTIN, i%2==0 ? HIGH : LOW);
      delay(25);
    }
  }

  digitalWrite(LED_BUILTIN, HIGH);	// indicate initial setup process
  
  setupLowpower();
  setupSGP30(forceResetEEPROM);
  setupBME680();

  digitalWrite(LED_BUILTIN, LOW);	// initial setup process finished, ready to go
}

/**
 * 
 */
void loop() {
  if(!buttonToggle){
    if(counter % 2 == 0){
      analogWrite(LED_BUILTIN, 0);
    }
    else{
      analogWrite(LED_BUILTIN, 1);
    }
  }

  // the Button is pushed
  if (digitalRead(BUTTON) == LOW) {
	  buttonToggle = !buttonToggle;
    readBaseline = true;
  }

  printTimestamp();
  
  if(counter % 5 == 1){
    DEBUG_SERIAL.print("Sodaq temperature "); 
    DEBUG_SERIAL.print(getSodaqTemperature()); 
    DEBUG_SERIAL.println(" C");
  }

  float gasr = 0.0;
  float temperature = 0.0;
  float pressure = 0.0;
  float humidity = 0.0;

  if(counter % 10 == 1 && getBME680Measure(&gasr, &humidity, &pressure, &temperature)){
    DEBUG_SERIAL.print("BME680 Temperature ");
    DEBUG_SERIAL.print(temperature);
    DEBUG_SERIAL.print(" C");
    DEBUG_SERIAL.print(", pressure ");
    DEBUG_SERIAL.print(pressure);
    DEBUG_SERIAL.print(" hPa");
    DEBUG_SERIAL.print(", humidity ");
    DEBUG_SERIAL.print(humidity);
    DEBUG_SERIAL.print(" %");
    DEBUG_SERIAL.print(", gas resistance ");
    DEBUG_SERIAL.print(gasr);
    DEBUG_SERIAL.println(" ohm");

    const float absoluteHumidity = 216000.7f * ((humidity / 100.0f) * 6.112f * exp((17.62f * temperature) / (243.12f + temperature)) / (273.15f + temperature)); // [g/m^3]
    //const uint32_t absoluteHumidityScaled = static_cast<uint32_t>(1000.0f * absoluteHumidity); // [mg/m^3]
      
    if(!sgp30.setHumidity(absoluteHumidity)){
      DEBUG_SERIAL.println("SGP30 IAQ measurement failed to set absolute humidity.");
    }
  
    DEBUG_SERIAL.print("absolute humidity for temp: ");
    DEBUG_SERIAL.print(absoluteHumidity);
    DEBUG_SERIAL.print(" (");
    DEBUG_SERIAL.print(temperature);
    DEBUG_SERIAL.print(",");
    DEBUG_SERIAL.print(humidity);
    DEBUG_SERIAL.println(")");

    
  }else{
    //flashSOS();
  }
  
  uint16_t tvoc;
  uint16_t eco2;
  if(getSGP30IAQMeasure(&eco2, humidity, temperature, &tvoc)){
    DEBUG_SERIAL.print("SGP30 TVOC "); 
    DEBUG_SERIAL.print(tvoc); 
    DEBUG_SERIAL.print(" ppb");
    DEBUG_SERIAL.print(", eCO2 "); 
    DEBUG_SERIAL.print(eco2); 
    DEBUG_SERIAL.println(" ppm");
  }else{
    flashSOS();
  }
  if(buttonToggle){
	  analogWrite(LED_BUILTIN, 16);
    pinMode(LED_RED, INPUT);

	/* https://www.idt.com/us/en/document/whp/overview-tvoc-and-indoor-air-quality
	Conversion from mg/m3 to ppm for most common TVOC is by the factor ca. 0.5; 
		for example, 10mg/m3 equals ca. 5ppm->5000ppb; 		1mg/m3->0.5ppm->500ppb
	Conversion from ppm to ppb is by the factor 1000;		for example, 0.1ppm equals 100ppb.
	*/
    if(tvoc <= 150){
		// LEVEL 1 Clean Hygienic Air (Target value) No action required. 
		// < 0.3 mg/m3 (150 ppb) Very Good
      pinMode(LED_GREEN, OUTPUT);
      pinMode(LED_BLUE, INPUT);
      analogWrite(LED_GREEN, (tvoc/150.0)*48.0+207);
    }else if(tvoc <= 500){
		// LEVEL 2 Good Air Quality Ventilation recommended. 
		// 0.3 – 1.0 mg/m3 (150-500 ppb) Good
      pinMode(LED_GREEN, INPUT);
      pinMode(LED_BLUE, OUTPUT);
      analogWrite(LED_BLUE, ((tvoc-150)/350.0)*48.0+207);
    }else if(tvoc <= 1500){
		// LEVEL 3 Noticeable Comfort Concerns Ventilation required. Identify sources 
		// 1.0 – 3.0 mg/m3 (500-1500 ppb) Medium
      pinMode(LED_GREEN, OUTPUT);
      pinMode(LED_BLUE, INPUT);
      pinMode(LED_RED, OUTPUT);
      analogWrite(LED_GREEN, ((tvoc-500)/1000.0)*48.0+191);
      analogWrite(LED_RED, 223);
    }else if(tvoc <= 3000){
		// LEVEL 4.1 MODIFIED LEVEL
		// 3.0 – 6.0 mg/m3 (1500-3000 ppb) Poor
      pinMode(LED_GREEN, INPUT);
      pinMode(LED_BLUE, INPUT);
      pinMode(LED_RED, OUTPUT);
      analogWrite(LED_RED, ((tvoc-1500)/1500)*64.0+191);
    }else if(tvoc <= 5000){
		// LEVEL 4.2 MODIFIED LEVEL
		// 6.0 – 10.0 mg/m3 (3000-5000 ppb) Poor
      pinMode(LED_GREEN, OUTPUT);
      pinMode(LED_BLUE, OUTPUT);
      pinMode(LED_RED, OUTPUT);
    }else{
		// Level 5 
		// > 10.0 mg/m3 (>5000 ppb) Unacceptable Conditions
      pinMode(LED_GREEN, INPUT);
      pinMode(LED_BLUE, INPUT);
    }
    
    if(readBaseline){
      if(sgp30.getIAQBaseline(&config.eco2_base, &config.tvoc_base)) {
        DEBUG_SERIAL.println("Baseline retrieved");
        readBaseline = false;
      }else{
        DEBUG_SERIAL.println("Failed to get baseline readings");
        flashSOS();
      }
    }
    printBaseline(config);
  } else {
    pinMode(LED_GREEN, INPUT);
    pinMode(LED_BLUE, INPUT);
    pinMode(LED_RED, INPUT);
  }
  
  DEBUG_SERIAL.println();
  delay(LOOP_DELAY);
  ++counter;
}

/**
 * print timestamp to serial
 */
void printTimestamp() {
  int s = millis()/1000;
  int m = s/60;
  if(m==0){
    DEBUG_SERIAL.print(s);
    DEBUG_SERIAL.print("s #");
  }else{
    int h = m/60;
    if(h==0){
      DEBUG_SERIAL.print(m);
      DEBUG_SERIAL.print("min #");
    }else{
      DEBUG_SERIAL.print(h);
      DEBUG_SERIAL.print("h #");
    }
  }
  
  DEBUG_SERIAL.print(counter);
  DEBUG_SERIAL.println(" " SODAQ_SERIAL);
}

/**
 * flash SOS
 */
void flashSOS() {
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_WORD_BREAK);
  
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DASH);

  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DASH);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DASH);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DASH);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DASH);

  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, LOW);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(MORSE_DOT);
  digitalWrite(LED_BUILTIN, LOW);

  delay(MORSE_WORD_BREAK);
  digitalWrite(LED_BUILTIN, HIGH);
}


/********************** SGP30 => *************************/


/**
 * 
 */
void setupSGP30(bool resetEEPROM) {
  if(sgp30.begin()){
    DEBUG_SERIAL.print("Found SGP30 serial #");
    DEBUG_SERIAL.print(sgp30.serialnumber[0], HEX);
    DEBUG_SERIAL.print(sgp30.serialnumber[1], HEX);
    DEBUG_SERIAL.println(sgp30.serialnumber[2], HEX);

  	readConfig(config);
  	// verify the serial number matches and whether the configuration is sensible (or not)
  	if(!resetEEPROM && 
  		config.serialnumber[0] == sgp30.serialnumber[0] && 
  		config.serialnumber[1] == sgp30.serialnumber[1] &&
  		config.serialnumber[2] == sgp30.serialnumber[2] && 
  		config.eco2_base != 0x0000 && config.tvoc_base != 0x0000){
  		// config is plausible, continue to set sensor-specific baseline value
      sgp30.setIAQBaseline(config.eco2_base, config.tvoc_base); // previously retrieved by sgp30.getIAQBaseline(&eCO2_base, &TVOC_base)
  		DEBUG_SERIAL.println("Configuration read from EEPROM");
  	}else{
  		// config is not sensible, e.g., SGP30 sensor has been changed, or baseline is zeroed
  		// or forced reset of EEPROM requested
  		config.serialnumber[0] = sgp30.serialnumber[0];
  		config.serialnumber[1] = sgp30.serialnumber[1];
  		config.serialnumber[2] = sgp30.serialnumber[2];
  		config.eco2_base = 0x0000; // nullify any previously set value
  		config.tvoc_base = 0x0000; // nullify any previously set value
  		// writeConfig(config); // just write the configuration to EEPROM the next time it is due
      DEBUG_SERIAL.println("EEPROM configuration reset requested");
  	}

    printBaseline(config);
        
    DEBUG_SERIAL.print("Waiting for SGP30 warmup for ");
    DEBUG_SERIAL.print(SGP30_WARMUP_DELAY);
    DEBUG_SERIAL.println(" ms.");
    delay(SGP30_WARMUP_DELAY);
  }else{
    DEBUG_SERIAL.println("SGP30 sensor not found.");
  }
}

/**
 * @param eCO2
 * @param TVOC
 * @return true if the values were successfully set to the given parameters
 */
boolean getSGP30IAQMeasure(uint16_t* eCO2, uint16_t* TVOC){
  if(!sgp30.IAQmeasure()) {
    DEBUG_SERIAL.println("SGP30 IAQ measurement failed.");
    return false;
  }
  *TVOC = sgp30.TVOC;
  *eCO2 = sgp30.eCO2;
  return true;
}

/**
 * Retrieve TVOC and eCO2 vaues using the given temperature and humidity for absolute humidity calculation (to increase accuracy).
 * 
 * @param eCO2 in ppm
 * @param humidity in %
 * @param temperature in C
 * @param TVOC in ppb
 * @return true if the values were successfully set to the given parameters
 */
boolean getSGP30IAQMeasure(uint16_t* eCO2, float humidity, float temperature, uint16_t* TVOC){
  return getSGP30IAQMeasure(eCO2, TVOC);
}

void printBaseline(const SGP30config& config){
  DEBUG_SERIAL.print("**** SGP Baseline values: eCO2: 0x"); 
  DEBUG_SERIAL.print(config.eco2_base, HEX);
  DEBUG_SERIAL.print(", TVOC: 0x"); 
  DEBUG_SERIAL.println(config.tvoc_base, HEX);
}

/********************** <= SGP30 *************************/




/********************** BME680 => *************************/

/**
 * 
 */
void setupBME680() {
  if(bme680.begin()) {
    DEBUG_SERIAL.println("Found BME680 sensor.");
    // Set up oversampling and filter initialization
    bme680.setTemperatureOversampling(BME680_OS_8X);
    bme680.setHumidityOversampling(BME680_OS_2X);
    bme680.setPressureOversampling(BME680_OS_4X);
    bme680.setIIRFilterSize(BME680_FILTER_SIZE_0);
    bme680.setGasHeater(320, 150); // 320*C for 150 ms
  }else{
    DEBUG_SERIAL.println("BME680 sensor not found.");
  }
}

/**
 * @param gasr gas resistance in ohm, gas heater must be enabled for this value
 * @param humidity in %
 * @param pressure in hPa
 * @param temperature in C
 * @return true on success
 */
boolean getBME680Measure(float* gasr, float* humidity, float* pressure, float* temperature) {
  if (!bme680.performReading()) {
    DEBUG_SERIAL.println("Failed to read BME680 measurements.");
    return false;
  }
  *gasr = bme680.gas_resistance;
  *humidity = bme680.humidity;
  *pressure = bme680.pressure / 100.0;
  *temperature = bme680.temperature;
  return true;
}

/********************** <= BME680 *************************/



/******************************** sodaq built-in sensors => ********************************/

/**
 * @return temperature temperature in C
 */
float getSodaqTemperature()
{
  //10mV per C, 0C is 500mV
  float volts = (float)analogRead(TEMP_SENSOR) * 3300.0 / 1023.0;
  return (volts - 500.0) / 10.0;
}


/******************************** <= sodaq built-in sensors ********************************/



/******************************** sodaq helper functions => ********************************/
#define bleSerial       Serial1
#define loraSerial      Serial2

void setupLowpower(){
    // LoRa module connection and sleep
    loraSerial.begin(LoRaBee.getDefaultBaudRate());
    LoRaBee.init(loraSerial, LORA_RESET);
    LoRaBee.sleep();

    // BLE module sleep
    rn487xBle.hwInit();
    bleSerial.begin(rn487xBle.getDefaultBaudRate());
    rn487xBle.initBleStream(&bleSerial);
    rn487xBle.enterCommandMode();
    rn487xBle.dormantMode();
    bleSerial.end();
}

void writeConfig(const SGP30config& config){
	DEBUG_SERIAL.println("Writing configuration to EEPROM");
//	EEPROM.put(0, config);	// write SGP30 configuration to EEPROM
}

void readConfig(SGP30config& config){
	DEBUG_SERIAL.println("Reading configuration from EEPROM");
  config.serialnumber[0] = 0x0;
  config.serialnumber[1] = 0xFD;
  config.serialnumber[2] = 0xDF4D;
  config.eco2_base = SGP30_BASELINE_ECO2;
  config.tvoc_base = SGP30_BASELINE_TVOC;
//	EEPROM.get(0, config);	// read SGP30 configuration from EEPROM
}

/******************************** <= sodaq helper functions ********************************/
