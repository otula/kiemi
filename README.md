# kiemi
Source code repository for the KIEMI Project.

[https://www.avoinsatakunta.fi/kiemi](https://www.avoinsatakunta.fi/kiemi)

Contents of this repository
---------------------------
csv-charts
 - HTML/Javascript example for generating pie and bar graphs from .csv files using Chart.js


csv-json-graphs
- HTML/JavaScript example for generating (line) charts from .csv and .json files using JQuery and flotcharts

csv-json-xy-graphs
- HTML/JavaScript example for generation dot charts (e.g. comfort temperature charts) from .csv and .json files using JQuery and flotcharts
- Example files from csv-json-graphs work with csv-json-xy-graphs, just remember to change the temperature,humidity field names to match the fields you want to plot, and change the csv separator (, or ;)


DS18x20-uart-opencpu
 - DS18x20 1-wire over uart implementation for Quectel bc66/OpenCPU.


LockTaskApp
- Android application (wrapper) for running an arbitrary HTML page in "kiosk mode", limiting user access only to the given page.


lorawan
- LoraWAN -> MySQL gateway example and BME680 (temperature, relative humidity, atmospheric pressure) & SGP30 (eCO2, TVOC) sensor code examples for Sodaq/Arduido


NB-IoT-Temperature-Prototype
- NB-IoT-based temperature sensor prototype running on Quectel bc66/OpenCPU.
- Can be used to send CIMI, temperature measurements, voltage and signal strength to a remote server using HTTP
- Uses DS18x20-uart-opencpu 1-wire protocol implementation for sensor communications

License
-------

The contents of this repository are licensed under the Apache License, Version 2.0, unless otherwise stated.

The 3rd party components included are licensed under their respective licenses. Check the individual application directories for further details.
