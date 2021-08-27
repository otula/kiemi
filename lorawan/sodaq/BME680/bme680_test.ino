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
#include <SPI.h>
#include "Adafruit_BME680.h"

#define DEBUG_SERIAL SerialUSB

#define BME680_SCK 12 // note: on Sodaq the LED_BUILTIN in hardcoded to 13, so do not connect anything to D13!
#define BME680_MISO 11
#define BME680_MOSI 10
#define BME680_CS 9

//Adafruit_BME680 bme680; // I2C
//Adafruit_BME680 bme680(BME680_CS); // hardware SPI
Adafruit_BME680 bme680(BME680_CS, BME680_MOSI, BME680_MISO,  BME680_SCK); // SPI

/**
 * 
 */
void setup() {
  delay(1000);

  while ((!DEBUG_SERIAL) && (millis() < 10000)) {
    // Wait for DEBUG_SERIAL to open
  }

  DEBUG_SERIAL.begin(57600);
  DEBUG_SERIAL.println("BME680  start");

  setupBME680();  
}

/**
 * 
 */
void loop() {
  float temperature;
  float pressure;
  float humidity;
  if(!getBME680Measure(&humidity, &pressure, &temperature)){
    return;
  }
  
  DEBUG_SERIAL.print("BME680 Temperature ");
  DEBUG_SERIAL.print(temperature);
  DEBUG_SERIAL.print(" C");
  DEBUG_SERIAL.print(", pressure ");
  DEBUG_SERIAL.print(pressure);
  DEBUG_SERIAL.print(" hPa");
  DEBUG_SERIAL.print(", humidity ");
  DEBUG_SERIAL.print(humidity);
  DEBUG_SERIAL.println(" %");

  DEBUG_SERIAL.println();
  delay(2000);
}

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
    //bme680.setGasHeater(320, 150); // 320*C for 150 ms
    bme680.setGasHeater(0, 0); // disable gas sensor
  }else{
    DEBUG_SERIAL.println("BME680 sensor not found.");
  }
}

/**
 * @param humidity in %
 * @param pressure in hPa
 * @param temperature in C
 * @return true on success
 */
boolean getBME680Measure(float* humidity, float* pressure, float* temperature) {
  if (!bme680.performReading()) {
    DEBUG_SERIAL.println("Failed to read BME680 measurements.");
    return false;
  }
  *humidity = bme680.humidity;
  *pressure = bme680.pressure / 100.0;
  *temperature = bme680.temperature;
  return true;
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
