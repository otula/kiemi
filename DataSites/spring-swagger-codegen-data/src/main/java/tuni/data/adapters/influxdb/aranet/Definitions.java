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
package tuni.data.adapters.influxdb.aranet;

/**
 * constant definitions
 * 
 */
public final class Definitions {
	
	/** date-time format */
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	
	/* JSON */
	/** JSON name */
	public static final String JSON_CO2 =  "co2";
	/** JSON name */
	public static final String JSON_HUMIDITY = "humidity";
	/** JSON name */
	public static final String JSON_NAME = "name";
	/** JSON name */
	public static final String JSON_PRESSURE = "pressure";
	/** JSON name */
	public static final String JSON_RSSI = "rssi";
	/** JSON name */
	public static final String JSON_TEMPERATURE = "temperature";
	/** JSON name */
	public static final String JSON_TIME = "time";
	
	/** JSON prefix for mean values */
	public static final String JSON_PREFIX_MEAN = "mean_";

	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
