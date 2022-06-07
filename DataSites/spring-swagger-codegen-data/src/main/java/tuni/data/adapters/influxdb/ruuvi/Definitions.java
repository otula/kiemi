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
package tuni.data.adapters.influxdb.ruuvi;

/**
 * constant definitions
 * 
 */
public final class Definitions {
	/** date-time format */
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	
	/* JSON */
	/** JSON name */
	public static final String JSON_ABSOLUTE_HUMIDITY =  "absoluteHumidity";
	/** JSON name */
	public static final String JSON_ACCELERATION_ANGLE_FROM_X = "accelerationAngleFromX";
	/** JSON name */
	public static final String JSON_ACCELERATION_ANGLE_FROM_Y = "accelerationAngleFromY";
	/** JSON name */
	public static final String JSON_ACCELERATION_ANGLE_FROM_Z = "accelerationAngleFromZ";
	/** JSON name */
	public static final String JSON_ACCELERATION_TOTAL = "accelerationTotal";
	/** JSON name */
	public static final String JSON_ACCELERATION_X = "accelerationX";
	/** JSON name */
	public static final String JSON_ACCELERATION_Y = "accelerationY";
	/** JSON name */
	public static final String JSON_ACCELERATION_Z = "accelerationZ";
	/** JSON name */
	public static final String JSON_AIR_DENSITY = "airDensity";
	/** JSON name */
	public static final String JSON_BATTERY_VOLTAGE = "batteryVoltage";
	/** JSON name */
	public static final String JSON_DEW_POINT = "dewPoint";
	/** JSON name */
	public static final String JSON_EQUILIBRIUM_VAPOR_PRESSURE = "equilibriumVaporPressure";
	/** JSON name */
	public static final String JSON_HUMIDITY = "humidity";
	/** JSON name */
	public static final String JSON_MAC = "mac";
	/** JSON name */
	public static final String JSON_NAME = "name";
	/** JSON prefix for mean values */
	public static final String JSON_PREFIX_MEAN = "mean_";
	/** JSON name */
	public static final String JSON_PRESSURE = "pressure";
	/** JSON name */
	public static final String JSON_RSSI = "rssi";
	/** JSON name */
	public static final String JSON_TEMPERATURE = "temperature";
	/** JSON name */
	public static final String JSON_TIME = "time";
	/** JSON name */
	public static final String JSON_TX_POWER = "txPower";
	/** JSON name */
	public static final String JSON_SUN_AZIMUTH_ANGLE = "sunAzimuthAngle";
	/** JSON name */
	public static final String JSON_SUN_ELEVATION_ANGLE = "sunElevationAngle";
	/** JSON name */
	public static final String JSON_SUN_ZENITH_ANGLE = "sunZenithAngle";
	/** JSON name */
	public static final String JSON_WIND_DIRECTION = "windDirection";
	/** JSON name */
	public static final String JSON_WIND_SPEED = "windSpeed";
	/** JSON name */
	public static final String JSON_RAIN = "rain";
	/** JSON name */
	public static final String JSON_RAIN_INTENSITY = "rainIntensity";
	/** JSON name */
	public static final String JSON_CLOUD_AMOUNT = "cloudAmount";
	
	/* parameters */
	/** parameter retention policy */
	public static final String PARAMETER_RETENTION_POLICY = "policy";

	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
