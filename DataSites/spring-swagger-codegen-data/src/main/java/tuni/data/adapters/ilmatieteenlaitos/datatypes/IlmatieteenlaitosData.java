/**
 * Copyright 2022 Tampere University
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
package tuni.data.adapters.ilmatieteenlaitos.datatypes;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import tuni.data.adapters.ilmatieteenlaitos.Definitions;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Ilmatieteen laitos data
 * 
 */
@JsonInclude(Include.NON_NULL)
public class IlmatieteenlaitosData implements tuni.data.adapters.datatypes.Data {
	@JsonProperty(Definitions.JSON_PROPERTY_CLOUD_COVER)
	private Double _cloudCover = null;
	@JsonProperty(Definitions.JSON_PROPERTY_HUMIDITY)
	private Double _humidity = null;
	@JsonProperty(Definitions.JSON_PROPERTY_PLACE)
	private String _place = null;
	@JsonProperty(Definitions.JSON_PROPERTY_PRESSURE_SEA_LEVEL)
	private Double _pressure = null;
	@JsonProperty(Definitions.JSON_PROPERTY_RAIN)
	private Double _rain = null;
	@JsonProperty(Definitions.JSON_PROPERTY_RAIN_INTENSITY)
	private Double _rainIntensity = null;
	@JsonProperty(Definitions.JSON_PROPERTY_TEMPERATURE)
	private Double _temperature = null;
	@JsonProperty(Definitions.JSON_PROPERTY_TIMESTAMP)
	private Date _time = null;
	@JsonProperty(Definitions.JSON_PROPERTY_WIND_DIRECTION)
	private Double _windDirection = null;
	@JsonProperty(Definitions.JSON_PROPERTY_WIND_SPEED)
	private Double _windSpeed = null;

	/**
	 * @return the humidity
	 */
	@JsonIgnore
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
	@JsonIgnore
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
	 * @return the rainIntensity
	 */
	@JsonIgnore
	public Double getRainIntensity() {
		return _rainIntensity;
	}

	/**
	 * @param rainIntensity the rainIntensity to set
	 */
	public void setRainIntensity(Double rainIntensity) {
		_rainIntensity = rainIntensity;
	}

	/**
	 * @return the temperature
	 */
	@JsonIgnore
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
	 * @return the time
	 */
	@JsonIgnore
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
	 * @return the windDirection
	 */
	@JsonIgnore
	public Double getWindDirection() {
		return _windDirection;
	}

	/**
	 * @param windDirection the windDirection to set
	 */
	public void setWindDirection(Double windDirection) {
		_windDirection = windDirection;
	}

	/**
	 * @return the windSpeed
	 */
	@JsonIgnore
	public Double getWindSpeed() {
		return _windSpeed;
	}

	/**
	 * @param windSpeed the windSpeed to set
	 */
	public void setWindSpeed(Double windSpeed) {
		_windSpeed = windSpeed;
	}

	@Override
	public long getUnixTimestamp() {
		return (_time == null ? -1 : _time.getTime());
	}

	/**
	 * @return the cloudCover
	 */
	@JsonIgnore
	public Double getCloudCover() {
		return _cloudCover;
	}

	/**
	 * @param cloudCover the cloudCover to set
	 */
	public void setCloudCover(Double cloudCover) {
		_cloudCover = cloudCover;
	}

	/**
	 * @return the rain
	 */
	@JsonIgnore
	public Double getRain() {
		return _rain;
	}

	/**
	 * @param rain the rain to set
	 */
	public void setRain(Double rain) {
		_rain = rain;
	}

	/**
	 * @return the place
	 */
	@JsonIgnore
	public String getPlace() {
		return _place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		_place = place;
	}
}
