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
package tuni.lorawan.sensors.model;

/**
 * 
 * 
 */
public final class Definitions {
	/* columns */
	/** database column */
	public static final String COLUMN_APPKEY = "appkey";
	/** database column */
	public static final String COLUMN_APPSKEY = "appskey";
	/** database column */
	public static final String COLUMN_DATA_ID = "data_id";
	/** database column */
	public static final String COLUMN_DESCRIPTION = "description";
	/** database column */
	public static final String COLUMN_ECO2 = "eco2";
	/** database column */
	public static final String COLUMN_HUMIDITY = "humidity";
	/** database column */
	public static final String COLUMN_NODE_ID = "node_id";
	/** database column */
	public static final String COLUMN_NWKSKEY = "nwkskey";
	/** database column */
	public static final String COLUMN_PRESSURE = "pressure";
	/** database column */
	public static final String COLUMN_ROW_CREATED = "row_created";
	/** database column */
	public static final String COLUMN_ROW_UPDATED = "row_updated";
	/** database column */
	public static final String COLUMN_TEMPERATURE = "temperature";
	/** database column */
	public static final String COLUMN_TIMESTAMP = "timestamp";
	/** database column */
	public static final String COLUMN_TVOC = "tvoc";

	/* entity names */
	/** entity */
	public static final String ENTITY_DATA = "Data";
	/** entity */
	public static final String ENTITY_NODE = "Node";
	
	/* JSON properties */
	/** JSON property */
	public static final String JSON_PROPERTY_APPKEY = COLUMN_APPKEY;
	/** JSON property */
	public static final String JSON_PROPERTY_APPSKEY = COLUMN_APPSKEY;
	/** JSON property */
	public static final String JSON_PROPERTY_DESCRIPTION = COLUMN_DESCRIPTION;
	/** JSON property */
	public static final String JSON_PROPERTY_ECO2 = COLUMN_ECO2;
	/** JSON property */
	public static final String JSON_PROPERTY_HUMIDITY = COLUMN_HUMIDITY;
	/** JSON property */
	public static final String JSON_PROPERTY_ID = "id";
	/** JSON property */
	public static final String JSON_PROPERTY_NWKSKEY = COLUMN_NWKSKEY;
	/** JSON property */
	public static final String JSON_PROPERTY_PRESSURE = COLUMN_PRESSURE;
	/** JSON property */
	public static final String JSON_PROPERTY_TEMPERATURE = COLUMN_TEMPERATURE;
	/** JSON property */
	public static final String JSON_PROPERTY_TIMESTAMP = COLUMN_TIMESTAMP;	
	/** JSON property */
	public static final String JSON_PROPERTY_TVOC = COLUMN_TVOC;
	
	/* tables */
	/** database table */
	public static final String TABLE_SENSOR_DATA = "sensor_data";
	/** database table */
	public static final String TABLE_SENSOR_NODES = "sensor_nodes";
	
	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
