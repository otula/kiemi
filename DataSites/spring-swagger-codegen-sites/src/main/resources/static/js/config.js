/**
 * Copyright 2021 Tampere University
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

var config = {
  serverUrlData: 'http://'+location.hostname+':44441',
  mapUrl: 'http://www.openstreetmap.org/?zoom=16',
  mapParamLatitude: '&mlat=',
  mapParamLongitude: '&mlon=',
  loadSensorData: 1,
  language: 'en',
  languageFile: 'language.json',
  languageText: {},
  useCache: 0,
  zoomStep: 10,
  alwaysShowId : true, // always show sensor id over floorplan
  defaultSiteId : null, // id of default site id
  defaultLayerId : null, // id of default layer id
  defaultSensorId : null, // array[] of default sensor identifiers
  sensorData: {
    defaultRoundingDigits : 2,
    position: {},
    alert: { alertStates: ['red', 'yellow', 'green'], alertGroup: ['temperature', 'valve'] },
    id: { roundingDigits: 0 },
    name: {},
    externalId: {},
		place: {dataId: 'place'},
    temperature: { dataId: { default: 'temperature', fourdeg: 'current_temperature' }, unit: '°C', alerts: [{ alert: 'green', min: 20, max: 22 }, { alert: 'yellow', min: 19, max: 24 }, { alert: 'red' }], scaleDefaults: { min: 0, max: 30}, roundingDigits: 2 },
    setTemperature: { dataId: 'current_set_point', unit: '°C' },
    communication: { dataId: { default: 'time', fourdeg: 'last_communication' } },
    valve: { dataId: 'current_valve_position', unit: '%', alerts: [{ alert: 'green', min: 10, max: 30 }, { alert: 'yellow', min: 5, max: 60 }, { alert: 'red' }] },
    signal: { dataId: 'current_rssi_signal_strength', unit: 'dBm', alerts: [{ alert: 'green', min: -60 }, { alert: 'yellow', min: -75 }, { alert: 'red' }] },
    battery: { dataId: 'current_battery_remaining', unit: '%', alerts: [{ alert: 'green', min: 60 }, { alert: 'yellow', min: 30 }, { alert: 'red' }] },
    pressure: { dataId: 'pressure', unit: 'hPa', scaleDefaults: { min: 900, max: 1100}, roundingDigits: 0 },
    humidity: { dataId: 'humidity', unit: 'RH%', scaleDefaults: { min: 0, max: 100}, roundingDigits: 0 },
		cloudCover: { dataId: 'cloudCover', unit: '/8', scaleDefaults: { min: 0, max: 8}, roundingDigits: 0 },
		rainIntensity: { dataId: 'rainIntensity', unit: 'mm/h?', roundingDigits: 1 },
		windDirection: { dataId: 'windDirection', unit: '°', roundingDigits: 0 },
		windSpeed: { dataId: 'windSpeed', unit: 'm/s', roundingDigits: 0 },
		rain: { dataId: 'rain', unit: 'mm/h?', roundingDigits: 1 },
    mac: { dataId: 'mac' },
    co2: { dataId: { default: 'co2', ouman: 'sensor-air-room-co2', aranet: 'co2' }, unit: 'ppm', scaleDefaults: { min: 400, max: 1200}, roundingDigits: 0 },
    voc: { dataId: { default: 'voc', ouman: 'sensor-air-room-voc' }, unit: 'ppb', scaleDefaults: { min: 0, max: 1000}, roundingDigits: 0 }
  },
  sensorFilters: {
    default: ['position', 'temperature', 'humidity', 'communication', 'id', 'name'],
    fourdeg: ['position', 'alert', 'id', 'name', 'externalId', 'temperature', 'setTemperature', 'valve', 'signal', 'battery', 'communication'],
    ouman: ['position', 'co2', 'voc', 'communication','id', 'name'],
		porienergia: ['position', 'temperature', 'humidity', 'communication','id', 'name'],
		fmi: ['position', 'cloudCover', 'temperature', 'humidity', 'communication','id', 'name', 'place','pressure','rainIntensity','windDirection','windSpeed'],
    ruuvi: ['position', 'temperature', 'pressure', 'humidity', 'communication', 'alert', 'id', 'name', 'externalId', 'mac'],
		aranet: ['position', 'temperature', 'pressure', 'humidity', 'communication', 'co2', 'id', 'name', 'externalId']
  },
  sensorViewData: {
    default: ['temperature', 'humidity', 'communication'],
    fourdeg: ['temperature', 'setTemperature', 'valve', 'signal', 'battery', 'communication'],
    ouman: ['co2', 'voc', 'communication'],
		porienergia: ['temperature', 'humidity', 'communication'],
		fmi: ['cloudCover', 'temperature', 'humidity', 'communication', 'place','pressure','rainIntensity','windDirection','windSpeed'],
    ruuvi: ['mac','temperature', 'pressure', 'humidity', 'communication'],
    aranet: ['temperature', 'pressure', 'humidity', 'co2', 'communication']
  },
  sensorTooltip: {
    default: ['temperature', 'humidity', 'communication'],
    fourdeg: ['temperature', 'setTemperature', 'valve', 'signal', 'battery', 'communication'],
    ouman: ['co2', 'voc', 'communication'],
		porienergia: ['temperature', 'humidity', 'communication'],
		fmi: ['cloudCover', 'temperature', 'humidity', 'communication', 'place','pressure','rainIntensity','windDirection','windSpeed'],
    ruuvi: ['temperature', 'pressure', 'humidity', 'communication', 'mac'],
    aranet: ['temperature', 'pressure', 'humidity', 'co2', 'communication']
  },
  sensorDataRange : { // default sensor data range for charts
    from : null, // date object
    to : null, // date object
    markerColor : "#000000", // from & to chart marker color
  },
  thermalComfort: {
    borderWidth : 2,
    insideText : "IN",
    outsideText : "OUT",
    limits : [
      {
        color : '#006600',
        polygon : [[17.5,72],[22.1,67],[24.2,33],[19,39]]
      }
    ]
  },
  dataDrop : {
    thresholdMultiplier : 2, // datadrop interval threshold multiplayer, e.g. 2 = if the interval exceed twice of the average interval, there is a data drop
    threshold : 2700, // minimum threshold in seconds
    color : "#ff0000",
    enabled : true // data drop detection is enabled
  }
};
