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

#include <Wire.h>
#include "Adafruit_SGP30.h"

#define DEBUG_SERIAL SerialUSB

#define SGP30_WARMUP_DELAY 17000

Adafruit_SGP30 sgp30;
int counter = 0;

/**
 * 
 */
void setup() {
  delay(1000);

  while ((!DEBUG_SERIAL) && (millis() < 10000)) {
    // Wait for DEBUG_SERIAL to open
  }

  DEBUG_SERIAL.println("SGP30 start");

  // Start streams
  DEBUG_SERIAL.begin(57600);
  
  setupSGP30();
}

/**
 * 
 */
void loop() {
  uint16_t eco2;
  uint16_t tvoc;
  if(getSGP30IAQMeasure(&eco2, &tvoc)){
    DEBUG_SERIAL.print("SGP30 TVOC "); 
    DEBUG_SERIAL.print(tvoc); 
    DEBUG_SERIAL.print(" ppb");
    DEBUG_SERIAL.print(", eCO2 "); 
    DEBUG_SERIAL.print(eco2); 
    DEBUG_SERIAL.println(" ppm");
  }

  uint16_t rawEthanol;
  uint16_t rawH2;
  if(getSGP30IAQMeasureRaw(&rawEthanol, &rawH2)){
    DEBUG_SERIAL.print("SGP raw H2 "); 
    DEBUG_SERIAL.print(rawH2);
    DEBUG_SERIAL.print(", raw Ethanol "); 
    DEBUG_SERIAL.println(rawEthanol);
  }

  if(++counter % 30 == 0){
    if(sgp30.getIAQBaseline(&eco2, &tvoc)) {
      DEBUG_SERIAL.print("****SGP30 Baseline values: eCO2: 0x"); 
      DEBUG_SERIAL.print(eco2, HEX);
      DEBUG_SERIAL.print(", TVOC: 0x"); 
      DEBUG_SERIAL.println(tvoc, HEX);
    }else{
      DEBUG_SERIAL.println("SGP30 failed to get baseline readings");
    }
  }

  delay(1000);
}

/**
 * 
 */
void setupSGP30() {
  if(sgp30.begin()){
    DEBUG_SERIAL.print("Found SGP30 serial #");
    DEBUG_SERIAL.print(sgp30.serialnumber[0], HEX);
    DEBUG_SERIAL.print(sgp30.serialnumber[1], HEX);
    DEBUG_SERIAL.println(sgp30.serialnumber[2], HEX);

    //TODO sgp30.setIAQBaseline(0x8E68, 0x8F41); // set sensor-specific baseline value, previously retrieved by sgp30.getIAQBaseline(&eCO2_base, &TVOC_base)
    
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

/**
 * @param rawEthanol
 * @param rawH2
 */
boolean getSGP30IAQMeasureRaw(uint16_t* rawEthanol, uint16_t* rawH2){
  if(!sgp30.IAQmeasureRaw()){
    DEBUG_SERIAL.println("SGP30 IAQ raw measurement failed.");
    return false;
  }
  *rawH2 = sgp30.rawH2;
  *rawEthanol = sgp30.rawEthanol;
  return true;
}
