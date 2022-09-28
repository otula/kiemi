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
package tuni.data.adapters.ilmatieteenlaitos;

/**
 * 
 * 
 */
public final class Definitions {
	/* JSON properties */
	/** JSON property name */
	public static final String JSON_PROPERTY_CLOUD_COVER = "cloudCover";
	/** JSON property name */
	public static final String JSON_PROPERTY_LATITUDE = "latitude";
	/** JSON property name */
	public static final String JSON_PROPERTY_LONGITUDE = "longitude";
	/** JSON property name */
	public static final String JSON_PROPERTY_PRESSURE_SEA_LEVEL = "pressure";
	/** JSON property name */
	public static final String JSON_PROPERTY_RAIN = "rain";
	/** JSON property name */
	public static final String JSON_PROPERTY_RAIN_INTENSITY = "rainIntensity";
	/** JSON property name */
	public static final String JSON_PROPERTY_HUMIDITY = "humidity";
	/** JSON property name */
	public static final String JSON_PROPERTY_TEMPERATURE = "temperature";
	/** JSON property name */
	public static final String JSON_PROPERTY_TIMESTAMP = "time";
	/** JSON property name */
	public static final String JSON_PROPERTY_WIND_DIRECTION = "windDirection";
	/** JSON property name */
	public static final String JSON_PROPERTY_WIND_SPEED = "windSpeed";
	/** JSON property name */
	public static final String JSON_PROPERTY_PLACE = "place";
	
	/** Ilmatieteen laitos API's data keys */
	public static final String FMI_CLOUD_COVER = "n_man";
	/** JSON property name */
	public static final String FMI_PRESSURE_SEA_LEVEL = "p_sea";
	/** JSON property name */
	public static final String FMI_RAIN_1H = "r_1h";
	/** JSON property name */
	public static final String FMI_RAIN_INTENSITY_10MIN = "ri_10min";
	/** JSON property name */
	public static final String FMI_RH = "rh";
	/** JSON property name */
	public static final String FMI_TEMPERATURE_2_MIN = "t2m";
	/** JSON property name */
	public static final String FMI_TIMESTAMP = "timestamp";
	/** JSON property name */
	public static final String FMI_WIND_DIRECTION_10MIN = "wd_10min";
	/** JSON property name */
	public static final String FMI_WIND_SPEED_10MIN = "ws_10min";
	
	/* common */
	/** data retrieval range when no start and end times are given, in milliseconds */
	public static final long NO_DATES_RANGE = 7200000; // 2h

	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
