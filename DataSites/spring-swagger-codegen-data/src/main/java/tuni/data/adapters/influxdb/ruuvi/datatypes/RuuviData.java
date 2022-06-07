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
package tuni.data.adapters.influxdb.ruuvi.datatypes;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import tuni.data.adapters.datatypes.Data;
import tuni.data.adapters.influxdb.ruuvi.Definitions;

/**
 * ruuvi data
 * 
 */
@JsonInclude(Include.NON_NULL)
public class RuuviData implements Data {
	@JsonProperty(Definitions.JSON_TIME)
	private Date _time = null;
	@JsonProperty(Definitions.JSON_ABSOLUTE_HUMIDITY)
	private Double _absoluteHumidity = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_ANGLE_FROM_X)
	private Double _accelerationAngleFromX = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_ANGLE_FROM_Y)
	private Double _accelerationAngleFromY = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_ANGLE_FROM_Z)
	private Double _accelerationAngleFromZ = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_TOTAL)
	private Double _accelerationTotal = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_ANGLE_FROM_X)
	private Double _accelerationX = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_Y)
	private Double _accelerationY = null;
	@JsonProperty(Definitions.JSON_ACCELERATION_Z)
	private Double _accelerationZ = null;
	@JsonProperty(Definitions.JSON_AIR_DENSITY)
	private Double _airDensity = null;
	@JsonProperty(Definitions.JSON_BATTERY_VOLTAGE)
	private Double _batteryVoltage = null;
	@JsonProperty(Definitions.JSON_DEW_POINT)
	private Double _dewPoint = null;
	@JsonProperty(Definitions.JSON_EQUILIBRIUM_VAPOR_PRESSURE)
	private Double _equilibriumVaporPressure = null;
	@JsonProperty(Definitions.JSON_HUMIDITY)
	private Double _humidity = null;
	@JsonProperty(Definitions.JSON_MAC)
	private String _mac = null;
	@JsonProperty(Definitions.JSON_NAME)
	private String _name = null;
	@JsonProperty(Definitions.JSON_PRESSURE)
	private Double _pressure = null;
	@JsonProperty(Definitions.JSON_RSSI)
	private Double _rssi = null;
	@JsonProperty(Definitions.JSON_TEMPERATURE)
	private Double _temperature = null;
	@JsonProperty(Definitions.JSON_TX_POWER)
	private Double _txPower = null;
	@JsonProperty(Definitions.JSON_SUN_AZIMUTH_ANGLE)
	private Double _sunAzimuthAngle = null;
	@JsonProperty(Definitions.JSON_SUN_ELEVATION_ANGLE)
	private Double _sunElevationAngle = null;
	@JsonProperty(Definitions.JSON_SUN_ZENITH_ANGLE)
	private Double _sunZenithAngle = null;
	@JsonProperty(Definitions.JSON_WIND_DIRECTION)
	private Double _windDirection = null;
	@JsonProperty(Definitions.JSON_WIND_SPEED)
	private Double _windSpeed = null;
	@JsonProperty(Definitions.JSON_RAIN)
	private Double _rain = null;
	@JsonProperty(Definitions.JSON_RAIN_INTENSITY)
	private Double _rainIntensity = null;
	@JsonProperty(Definitions.JSON_CLOUD_AMOUNT)
	private Double _cloudAmount = null;

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
	 * @return the absoluteHumidity
	 */
	public Double getAbsoluteHumidity() {
		return _absoluteHumidity;
	}

	/**
	 * @param absoluteHumidity the absoluteHumidity to set
	 */
	public void setAbsoluteHumidity(Double absoluteHumidity) {
		_absoluteHumidity = absoluteHumidity;
	}

	/**
	 * @return the accelerationAngleFromX
	 */
	public Double getAccelerationAngleFromX() {
		return _accelerationAngleFromX;
	}

	/**
	 * @param accelerationAngleFromX the accelerationAngleFromX to set
	 */
	public void setAccelerationAngleFromX(Double accelerationAngleFromX) {
		_accelerationAngleFromX = accelerationAngleFromX;
	}

	/**
	 * @return the accelerationAngleFromY
	 */
	public Double getAccelerationAngleFromY() {
		return _accelerationAngleFromY;
	}

	/**
	 * @param accelerationAngleFromY the accelerationAngleFromY to set
	 */
	public void setAccelerationAngleFromY(Double accelerationAngleFromY) {
		_accelerationAngleFromY = accelerationAngleFromY;
	}

	/**
	 * @return the accelerationAngleFromZ
	 */
	public Double getAccelerationAngleFromZ() {
		return _accelerationAngleFromZ;
	}

	/**
	 * @param accelerationAngleFromZ the accelerationAngleFromZ to set
	 */
	public void setAccelerationAngleFromZ(Double accelerationAngleFromZ) {
		_accelerationAngleFromZ = accelerationAngleFromZ;
	}

	/**
	 * @return the accelerationTotal
	 */
	public Double getAccelerationTotal() {
		return _accelerationTotal;
	}

	/**
	 * @param accelerationTotal the accelerationTotal to set
	 */
	public void setAccelerationTotal(Double accelerationTotal) {
		_accelerationTotal = accelerationTotal;
	}

	/**
	 * @return the accelerationX
	 */
	public Double getAccelerationX() {
		return _accelerationX;
	}

	/**
	 * @param accelerationX the accelerationX to set
	 */
	public void setAccelerationX(Double accelerationX) {
		_accelerationX = accelerationX;
	}

	/**
	 * @return the accelerationY
	 */
	public Double getAccelerationY() {
		return _accelerationY;
	}

	/**
	 * @param accelerationY the accelerationY to set
	 */
	public void setAccelerationY(Double accelerationY) {
		_accelerationY = accelerationY;
	}

	/**
	 * @return the accelerationZ
	 */
	public Double getAccelerationZ() {
		return _accelerationZ;
	}

	/**
	 * @param accelerationZ the accelerationZ to set
	 */
	public void setAccelerationZ(Double accelerationZ) {
		_accelerationZ = accelerationZ;
	}

	/**
	 * @return the airDensity
	 */
	public Double getAirDensity() {
		return _airDensity;
	}

	/**
	 * @param airDensity the airDensity to set
	 */
	public void setAirDensity(Double airDensity) {
		_airDensity = airDensity;
	}

	/**
	 * @return the batteryVoltage
	 */
	public Double getBatteryVoltage() {
		return _batteryVoltage;
	}

	/**
	 * @param batteryVoltage the batteryVoltage to set
	 */
	public void setBatteryVoltage(Double batteryVoltage) {
		_batteryVoltage = batteryVoltage;
	}

	/**
	 * @return the dewPoint
	 */
	public Double getDewPoint() {
		return _dewPoint;
	}

	/**
	 * @param dewPoint the dewPoint to set
	 */
	public void setDewPoint(Double dewPoint) {
		_dewPoint = dewPoint;
	}

	/**
	 * @return the equilibriumVaporPressure
	 */
	public Double getEquilibriumVaporPressure() {
		return _equilibriumVaporPressure;
	}

	/**
	 * @param equilibriumVaporPressure the equilibriumVaporPressure to set
	 */
	public void setEquilibriumVaporPressure(Double equilibriumVaporPressure) {
		_equilibriumVaporPressure = equilibriumVaporPressure;
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
	 * @return the mac
	 */
	public String getMac() {
		return _mac;
	}

	/**
	 * @param mac the mac to set
	 */
	public void setMac(String mac) {
		_mac = mac;
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
	 * @return the txPower
	 */
	public Double getTxPower() {
		return _txPower;
	}

	/**
	 * @param txPower the txPower to set
	 */
	public void setTxPower(Double txPower) {
		_txPower = txPower;
	}

	/**
	 * @return the sunAzimuthAngle
	 */
	public Double getSunAzimuthAngle() {
		return _sunAzimuthAngle;
	}

	/**
	 * @param sunAzimuthAngle the sunAzimuthAngle to set
	 */
	public void setSunAzimuthAngle(Double sunAzimuthAngle) {
		_sunAzimuthAngle = sunAzimuthAngle;
	}

	/**
	 * @return the sunElevationAngle
	 */
	public Double getSunElevationAngle() {
		return _sunElevationAngle;
	}

	/**
	 * @param sunElevationAngle the sunElevationAngle to set
	 */
	public void setSunElevationAngle(Double sunElevationAngle) {
		_sunElevationAngle = sunElevationAngle;
	}

	/**
	 * @return the sunZenithAngle
	 */
	public Double getSunZenithAngle() {
		return _sunZenithAngle;
	}

	/**
	 * @param sunZenithAngle the sunZenithAngle to set
	 */
	public void setSunZenithAngle(Double sunZenithAngle) {
		_sunZenithAngle = sunZenithAngle;
	}

	/**
	 * @return the windDirection
	 */
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
	public Double getWindSpeed() {
		return _windSpeed;
	}

	/**
	 * @param windSpeed the windSpeed to set
	 */
	public void setWindSpeed(Double windSpeed) {
		_windSpeed = windSpeed;
	}

	/**
	 * @return the rain
	 */
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
	 * @return the rainIntensity
	 */
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
	 * @return the cloudAmount
	 */
	public Double getCloudAmount() {
		return _cloudAmount;
	}

	/**
	 * @param cloudAmount the cloudAmount to set
	 */
	public void setCloudAmount(Double cloudAmount) {
		_cloudAmount = cloudAmount;
	}

	@Override
	public long getUnixTimestamp() {
		return (_time == null ? -1 : _time.getTime());
	}
}
