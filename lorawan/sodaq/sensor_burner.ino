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

#define BME680_SCK 12
#define BME680_MISO 11
#define BME680_MOSI 10
#define BME680_CS 9

//Adafruit_BME680 bme680; // I2C
//Adafruit_BME680 bme680(BME680_CS); // hardware SPI
Adafruit_BME680 bme680(BME680_CS, BME680_MOSI, BME680_MISO,  BME680_SCK); // SPI

Adafruit_SGP30 sgp30;

unsigned int counter = 1;

/**
 * 
 */
void setup() {
  delay(1000);

  while ((!DEBUG_SERIAL) && (millis() < 10000)) {
    // Wait for DEBUG_SERIAL to open
  }

  DEBUG_SERIAL.println("Start");

  // Start streams
  DEBUG_SERIAL.begin(57600);
  
  pinMode(LED_BUILTIN, OUTPUT);

  setupSGP30();
  setupBME680();

  digitalWrite(LED_BUILTIN, HIGH);
}

/**
 * 
 */
void loop() {
  printTimestamp();
  
  DEBUG_SERIAL.print("Sodaq temperature "); 
  DEBUG_SERIAL.print(getSodaqTemperature()); 
  DEBUG_SERIAL.println(" C");

  float gasr;
  float temperature;
  float pressure;
  float humidity;
  if(getBME680Measure(&gasr, &humidity, &pressure, &temperature)){
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
  }else{
    flashSOS();
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
  if(++counter % 60 == 0){
    if(sgp30.getIAQBaseline(&eco2, &tvoc)) {
      DEBUG_SERIAL.print("**** SGP Baseline values: eCO2: 0x"); 
      DEBUG_SERIAL.print(eco2, HEX);
      DEBUG_SERIAL.print(", TVOC: 0x"); 
      DEBUG_SERIAL.println(tvoc, HEX);
    }else{
      DEBUG_SERIAL.println("Failed to get baseline readings");
      flashSOS();
    }
  }
  

  DEBUG_SERIAL.println();
  delay(LOOP_DELAY);
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
void setupSGP30() {
  if(sgp30.begin()){
    DEBUG_SERIAL.print("Found SGP30 serial #");
    DEBUG_SERIAL.print(sgp30.serialnumber[0], HEX);
    DEBUG_SERIAL.print(sgp30.serialnumber[1], HEX);
    DEBUG_SERIAL.println(sgp30.serialnumber[2], HEX);

    // sgp30.setIAQBaseline(SGP30_BASELINE_ECO2, SGP30_BASELINE_TVOC); // set sensor-specific baseline value, previously retrieved by sgp30.getIAQBaseline(&eCO2_base, &TVOC_base)
    
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
  const float absoluteHumidity = 216.7f * ((humidity / 100.0f) * 6.112f * exp((17.62f * temperature) / (243.12f + temperature)) / (273.15f + temperature)); // [g/m^3]
  const uint32_t absoluteHumidityScaled = static_cast<uint32_t>(1000.0f * absoluteHumidity); // [mg/m^3]
    
  if(!sgp30.setHumidity(absoluteHumidityScaled)){
    DEBUG_SERIAL.println("SGP30 IAQ measurement failed to set absolute humidity.");
    return false;
  }
  
  return getSGP30IAQMeasure(eCO2, TVOC);
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
    bme680.setIIRFilterSize(BME680_FILTER_SIZE_3);
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
