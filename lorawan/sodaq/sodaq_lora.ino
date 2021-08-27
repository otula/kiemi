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
 * NOTE: remember to modify SGP30_BASELINE_ECO2, SGP30_BASELINE_TVOC, devAddr, appSKey and nwkSKey to match your values.
 */
#include <Sodaq_RN2483.h>
#include <stdio.h> // sprintf
#include <string.h> // strlen
#include <avr/dtostrf.h>

#include <Wire.h>
#include <SPI.h>
#include "Adafruit_SGP30.h"
#include "Adafruit_BME680.h"

#define DEBUG_SERIAL SerialUSB
#define LORA_SERIAL Serial2

#define DOUBLE_MIN_WIDTH 3
#define DOUBLE_PRECISION 2

#define SEND_INTERVAL 180000 // in ms, should be at least twice of IDLE_DELAY for reasonable operation
#define IDLE_DELAY 1000 // in ms

#define SPREADING_FACTOR 7

#define LED_TIME 1000 // in ms
#define LED_DELAY 3000 // in ms

#define LED_SENSOR_ERROR_DELAY 300 // in ms

#define VALUE_BUFFER_SIZE 10 // size of value buffer, in characters, e.g. for 10 characters/digits total the value should be smaller than XXXXXX.XX\0 (<1000000), see VALUE_MIN and VALUE_MAX below
#define VALUE_MAX 1000000 // maximum value, exclusive
#define VALUE_MIN -100000 // minimum value, exclusive

#define LORAWAN_MESSAGE_MAX 200 // maximum size for a single lorawan message, in characters

#define SENSOR_ERROR -1

#define SGP30_WARMUP_DELAY 17000
#define SGP30_BASELINE_ECO2 0x920C
#define SGP30_BASELINE_TVOC 0x9216

#define BME680_SCK 12
#define BME680_MISO 11
#define BME680_MOSI 10
#define BME680_CS 9

//Adafruit_BME680 bme680; // I2C
//Adafruit_BME680 bme680(BME680_CS); // hardware SPI
Adafruit_BME680 bme680(BME680_CS, BME680_MOSI, BME680_MISO,  BME680_SCK); // SPI

Adafruit_SGP30 sgp30;

const char* messageFormatBME680 = "|H%s|P%s|T%s"; // BME680
const char* messageFormatSGP30 = "|E%d|V%d"; // SGP30
const char* messageFormatSodaq = "|T%s"; // built-in sensors

// /* ABP sodaq */
const uint8_t devAddr[4] = {0x00, 0x00, 0x00, 0x00};
const uint8_t appSKey[16] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
const uint8_t nwkSKey[16] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};


/**
 * 
 */
void setup()
{
  delay(1000);

  while ((!DEBUG_SERIAL) && (millis() < 10000)) {
    // Wait for DEBUG_SERIAL to open
  }

  // Start streams
  DEBUG_SERIAL.begin(57600);
  DEBUG_SERIAL.println("Start");

  pinMode(LED_BUILTIN, OUTPUT);

  setupLoRa();

  setupBME680();

  setupSGP30();
}

/**
 * 
 */
void loop()
{
  char buffer[LORAWAN_MESSAGE_MAX];
  if(!getSodaqBuiltInData(buffer)){
    return;
  }

  char tbuf[LORAWAN_MESSAGE_MAX];
  if(!getSGP30Data(tbuf)){
    return;
  }
  strcat(buffer, tbuf); // TODO: in the final implementation, we should just print everything in the same buffer, and also check that the string length does not exceed the maximum lorawan package size

  if(!getBME680Data(tbuf)){
    return;
  }
  strcat(buffer, tbuf); // TODO: in the final implementation, we should just print everything in the same buffer, and also check that the string length does not exceed the maximum lorawan package size
  
  DEBUG_SERIAL.println(buffer);
  
  sendLoRa((uint8_t*) buffer, strlen(buffer));
}

/**
 * flash led and wait
 * 
 * @param count number of times to flash the led, < 0 will flash the led on/off for duration of the whole delay. NoError (0) will call idle()
 */
void flashLEDandWait(int count) {
  if(count < 0){
    for(int delay_remaining = SEND_INTERVAL; delay_remaining > 0; delay_remaining -= LED_SENSOR_ERROR_DELAY*2){
      digitalWrite(LED_BUILTIN, HIGH);
      delay(LED_SENSOR_ERROR_DELAY);
      digitalWrite(LED_BUILTIN, LOW);
      delay(LED_SENSOR_ERROR_DELAY);
    }
  }else if(count == NoError){
    idle();
  }else{
    for(int delay_remaining = SEND_INTERVAL; delay_remaining > 0; delay_remaining -= (LED_DELAY+2*LED_TIME*count)) {
      for(int i=0;i<count;++i){
        digitalWrite(LED_BUILTIN, HIGH);
        delay(LED_TIME);
        digitalWrite(LED_BUILTIN, LOW);
        delay(LED_TIME);
      }
      delay(LED_DELAY);
    }
  }
}

/**
 * idle for SEND_INTERVAL, will keep the led on for half of the delay.
 */
void idle() {
  digitalWrite(LED_BUILTIN, HIGH);
  float temperature;
  float pressure;
  float humidity;
  uint16_t tvoc;
  uint16_t eco2;
  boolean isHigh = true;
  for(int d=SEND_INTERVAL,hd=SEND_INTERVAL/2;d>0;d-=IDLE_DELAY){
    if(!getBME680Measure(&humidity, &pressure, &temperature)){
      if(isHigh){
        digitalWrite(LED_BUILTIN, LOW);
      }
      DEBUG_SERIAL.println("Failed to get BME680 data for SGP30 absolute humidity calculation.");
      flashLEDandWait(SENSOR_ERROR);
      return;
    }

    if(!getSGP30IAQMeasure(&eco2, humidity, temperature, &tvoc)){ // SGP30 sensor needs to be read about every second to keep the baseline calculation active, so read the measurements and discard them
      if(isHigh){
        digitalWrite(LED_BUILTIN, LOW);
      }
      DEBUG_SERIAL.println("Failed to get SGP30 data.");
      flashLEDandWait(SENSOR_ERROR);
      return;
    }

    if(isHigh && d<=hd){
      digitalWrite(LED_BUILTIN, LOW);
      isHigh = false;
    }

    delay(IDLE_DELAY);
  }
}

/******************************** loraw => ********************************/

/**
 * 
 */
void setupLoRa() {
  LORA_SERIAL.begin(LoRaBee.getDefaultBaudRate());

  LoRaBee.setDiag(DEBUG_SERIAL); // to use debug remove //DEBUG inside library
  LoRaBee.init(LORA_SERIAL, LORA_RESET);
  
  if (LoRaBee.initABP(LORA_SERIAL, devAddr, appSKey, nwkSKey, true))  {
    DEBUG_SERIAL.println("Communication to LoRaBEE successful.");
  } else {
    DEBUG_SERIAL.println("Communication to LoRaBEE failed.");
  }
  // Uncomment this line to for the RN2903 with the Actility Network
  // For OTAA update the DEFAULT_FSB in the library
  // LoRaBee.setFsbChannels(1);

  LoRaBee.setSpreadingFactor(SPREADING_FACTOR);
}

/**
 * send the package over using lorabee, this will also automatically wait
 * 
 * @param buffer to send, and length
 */
void sendLoRa(uint8_t* buffer, int len) {
  switch (LoRaBee.send(1, buffer, len)) // cases from: https://github.com/SodaqMoja/Sodaq_RN2483/blob/master/src/Sodaq_RN2483.h
  {
    case NoError:
      DEBUG_SERIAL.println("Successful transmission.");
      flashLEDandWait(NoError);
      break;
    case NoResponse:
      DEBUG_SERIAL.println("There was no response from the device.");
      flashLEDandWait(NoResponse);
      break;
    case Timeout:
      DEBUG_SERIAL.println("Connection timed-out..");
      flashLEDandWait(Timeout);
      break;
    case PayloadSizeError:
      DEBUG_SERIAL.println("The size of the payload is greater than allowed. Transmission failed.");
      flashLEDandWait(PayloadSizeError);
      break;
    case InternalError:
      DEBUG_SERIAL.println("Internal error.");
      setupLoRa();
      flashLEDandWait(InternalError);
      break;
    case Busy:
      DEBUG_SERIAL.println("The device is busy.");
      flashLEDandWait(Busy);
      break;
    case NetworkFatalError:
      DEBUG_SERIAL.println("There is a non-recoverable error with the network connection.");
      setupLoRa();
      flashLEDandWait(NetworkFatalError);
      break;
    case NotConnected:
      DEBUG_SERIAL.println("The device is not connected to the network.");
      setupLoRa();
      flashLEDandWait(NotConnected);
      break;
    case NoAcknowledgment:
      DEBUG_SERIAL.println("There was no acknowledgment sent back.");
      flashLEDandWait(NoAcknowledgment);
      break;
    default:
      DEBUG_SERIAL.println("Unhandeled response code from LoraBee.send().");
      flashLEDandWait(-1);
      break;
  }
}

/******************************** <= loraw ********************************/





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

/**
 * Get data from the built-in sensors of the sodaq board
 * 
 * @param buffer to write the data into
 * @return true on success
 */
boolean getSodaqBuiltInData(char* buffer){
  float temperature = getSodaqTemperature();
  if(temperature <= VALUE_MIN || temperature >= VALUE_MAX){
    DEBUG_SERIAL.println("Failed to get sodaq temperature data.");
    flashLEDandWait(SENSOR_ERROR);
    return false;
  }

  char str_temperature[VALUE_BUFFER_SIZE];
  dtostrf(temperature, DOUBLE_MIN_WIDTH, DOUBLE_PRECISION, str_temperature);

  sprintf(buffer, messageFormatSodaq, str_temperature);
  return true;
}

/******************************** <= sodaq built-in sensors => ********************************/





/******************************** sqp30 => ********************************/


/**
 * 
 */
void setupSGP30() {
  if(sgp30.begin()){
    DEBUG_SERIAL.print("Found SGP30 serial #");
    DEBUG_SERIAL.print(sgp30.serialnumber[0], HEX);
    DEBUG_SERIAL.print(sgp30.serialnumber[1], HEX);
    DEBUG_SERIAL.println(sgp30.serialnumber[2], HEX);

    sgp30.setIAQBaseline(SGP30_BASELINE_ECO2, SGP30_BASELINE_TVOC); // set sensor-specific baseline value, previously retrieved by sgp30.getIAQBaseline(&eCO2_base, &TVOC_base)
    
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
 * Get data from SGP30
 * 
 * @param buffer to write the data into
 * @return true on success
 */
boolean getSGP30Data(char* buffer){
  float temperature;
  float pressure;
  float humidity;
  if(!getBME680Measure(&humidity, &pressure, &temperature)){
    DEBUG_SERIAL.println("Failed to get BME680 data for SGP30 absolute humidity calculation.");
    flashLEDandWait(SENSOR_ERROR);
    return false;
  }

  uint16_t tvoc;
  uint16_t eco2;
  if(!getSGP30IAQMeasure(&eco2, humidity, temperature, &tvoc) || tvoc <= VALUE_MIN || tvoc >= VALUE_MAX || eco2 <= VALUE_MIN || eco2 >= VALUE_MAX){
    DEBUG_SERIAL.println("Failed to get SGP30 data.");
    flashLEDandWait(SENSOR_ERROR);
    return false;
  }

  sprintf(buffer, messageFormatSGP30, eco2, tvoc);
  return true;
}

/******************************** <= sqp30 ********************************/



/******************************** bme680 => ********************************/

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
 * Get data from BME680
 * 
 * @param buffer to write the data into
 * @return true on success
 */
boolean getBME680Data(char* buffer){
  float temperature;
  float pressure;
  float humidity;
  if(!getBME680Measure(&humidity, &pressure, &temperature) || humidity <= VALUE_MIN || humidity >= VALUE_MAX || pressure <= VALUE_MIN || pressure >= VALUE_MAX || temperature <= VALUE_MIN || temperature >= VALUE_MAX){
    DEBUG_SERIAL.println("Failed to get BME680 data.");
    flashLEDandWait(SENSOR_ERROR);
    return false;
  }

  char str_humidity[VALUE_BUFFER_SIZE];
  dtostrf(humidity, DOUBLE_MIN_WIDTH, DOUBLE_PRECISION, str_humidity);
  char str_pressure[VALUE_BUFFER_SIZE];
  dtostrf(pressure, DOUBLE_MIN_WIDTH, DOUBLE_PRECISION, str_pressure);
  char str_temperature[VALUE_BUFFER_SIZE];
  dtostrf(temperature, DOUBLE_MIN_WIDTH, DOUBLE_PRECISION, str_temperature);

  sprintf(buffer, messageFormatBME680, str_humidity, str_pressure, str_temperature);
  return true;
}

/******************************** <= bme680 ********************************/
