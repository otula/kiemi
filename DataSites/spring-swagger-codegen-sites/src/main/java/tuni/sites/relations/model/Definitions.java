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
package tuni.sites.relations.model;

/**
 * relation definitions
 * 
 */
public final class Definitions {
	/* columns */
	/** database column */
	public static final String COLUMN_PARENT_ID = "parent_id";
	/** database column */
	public static final String COLUMN_CHILD_ID = "child_id";
	
	/* entities */
	/** entity name */
	public static final String ENTITY_LAYER_TO_LAYER = "layerToLayer";
	/** entity name */
	public static final String ENTITY_LAYER_TO_SENSOR = "layerToSensor";
	/** entity name */
	public static final String ENTITY_SITE_TO_LAYER = "siteToLayer";
	
	/* tables */
	/** database table name */
	public static final String TABLE_LAYER_TO_LAYER = "layer_to_layer";
	/** database table name */
	public static final String TABLE_LAYER_TO_SENSOR = "layer_to_sensor";
	/** database table name */
	public static final String TABLE_SITE_TO_LAYER = "site_to_layer";
	
	
	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
