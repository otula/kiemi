/**
 * Copyright 2020 Tampere University
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
package tuni.data.adapters.influxdb.aranet.datatypes;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import tuni.data.adapters.datatypes.Data;
import tuni.data.adapters.influxdb.aranet.Definitions;

/**
 * RuuviData extended with Aranet4 data types
 * 
 */
@JsonInclude(Include.NON_NULL)
public class AranetData implements Data {
	@JsonProperty(Definitions.JSON_TIME)
	private Date _time = null;
	@JsonProperty(Definitions.JSON_CO2)
	private Double _co2 = null;
	@JsonProperty(Definitions.JSON_NAME)
	private String _name = null;
	@JsonProperty(Definitions.JSON_HUMIDITY)
	private Double _humidity = null;
	@JsonProperty(Definitions.JSON_PRESSURE)
	private Double _pressure = null;
	@JsonProperty(Definitions.JSON_TEMPERATURE)
	private Double _temperature = null;
	@JsonProperty(Definitions.JSON_RSSI)
	private Double _rssi = null;

	/**
	 * @return the co2
	 */
	public Double getCO2() {
		return _co2;
	}

	/**
	 * @param co2 the co2 to set
	 */
	public void setCO2(Double co2) {
		_co2 = co2;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return _time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		_time = time;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * @return the humidity
	 */
	public Double getHumidity() {
		return _humidity;
	}

	/**
	 * @param humidity the humidity to set
	 */
	public void setHumidity(Double humidity) {
		_humidity = humidity;
	}

	/**
	 * @return the pressure
	 */
	public Double getPressure() {
		return _pressure;
	}

	/**
	 * @param pressure the pressure to set
	 */
	public void setPressure(Double pressure) {
		_pressure = pressure;
	}

	/**
	 * @return the temperature
	 */
	public Double getTemperature() {
		return _temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(Double temperature) {
		_temperature = temperature;
	}

	/**
	 * @return the rssi
	 */
	public Double getRssi() {
		return _rssi;
	}

	/**
	 * @param rssi the rssi to set
	 */
	public void setRssi(Double rssi) {
		_rssi = rssi;
	}

	@Override
	public long getUnixTimestamp() {
		return (_time == null ? -1 : _time.getTime());
	}
}
