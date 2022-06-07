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
package tuni.sites.model;

/**
 * Constant definitions for models
 * 
 */
public final class Definitions {
	/* columns */
	/** database column */
	public static final String COLUMN_ADDRESS = "address";
	/** database column */
	public static final String COLUMN_CITY = "city";
	/** database column */
	public static final String COLUMN_COUNTRY = "country";
	/** database column */
	public static final String COLUMN_DESCRIPTION = "description";
	/** database column */
	public static final String COLUMN_EXTERNAL_ID = "external_id";
	/** database column */
	public static final String COLUMN_EXTERNAL_URL = "external_url";
	/** database column */
	public static final String COLUMN_ID = "id";
	/** database column */
	public static final String COLUMN_INDEX = "index_nro";
	/** database column */
	public static final String COLUMN_LATITUDE = "latitude";
	/** database column */
	public static final String COLUMN_LAYER_ID = "layer_id";
	/** database column */
	public static final String COLUMN_LONGITUDE = "longitude";
	/** database column */
	public static final String COLUMN_NAME = "name";
	/** database column */
	public static final String COLUMN_ORGANIZATION_NAME = "organization_name";
	/** database column */
	public static final String COLUMN_POSTAL_CODE = "postal_code";
	/** database column */
	public static final String COLUMN_ROW_CREATED = "row_created";
	/** database column */
	public static final String COLUMN_ROW_UPDATED = "row_updated";
	/** database column */
	public static final String COLUMN_SCALE = "scale";
	/** database column */
	public static final String COLUMN_SERVICE_TYPE = "service_type";
	/** database column */
	public static final String COLUMN_TYPE = "type";
	/** database column */
	public static final String COLUMN_URL = "url";
	/** database column */
	public static final String COLUMN_X = "x";
	/** database column */
	public static final String COLUMN_Y = "y";
	/** database column */
	public static final String COLUMN_Z = "z";
	
	/* entities */
	/** entity name */
	public static final String ENTITY_IMAGE = "image";
	/** entity name */
	public static final String ENTITY_LAYER = "layer";
	/** entity name */
	public static final String ENTITY_SENSOR = "sensor";
	/** entity name */
	public static final String ENTITY_SITE = "site";
	
	/* json properties */
	/** JSON property name */
	public static final String JSON_PROPERTY_ADDRESS = "address";
	/** JSON property name */
	public static final String JSON_PROPERTY_CITY = "city";
	/** JSON property name */
	public static final String JSON_PROPERTY_COUNTRY = "country";
	/** JSON property name */
	public static final String JSON_PROPERTY_DESCRIPTION = "description";
	/** JSON property name */
	public static final String JSON_PROPERTY_EXTERNAL_ID = "externalId";
	/** JSON property name */
	public static final String JSON_PROPERTY_EXTERNAL_URL = "externalUrl";
	/** JSON property name */
	public static final String JSON_PROPERTY_ID = "id";
	/** JSON property name */
	public static final String JSON_PROPERTY_IMAGE = "image";
	/** JSON property name */
	public static final String JSON_PROPERTY_INDEX = "index";
	/** JSON property name */
	public static final String JSON_PROPERTY_LATITUDE = "latitude";
	/** JSON property name */
	public static final String JSON_PROPERTY_LONGITUDE = "longitude";
	/** JSON property name */
	public static final String JSON_PROPERTY_NAME = "name";
	/** JSON property name */
	public static final String JSON_PROPERTY_ORGANIZATION_NAME = "organizationName";
	/** JSON property name */
	public static final String JSON_PROPERTY_POSTAL_CODE = "postalCode";
	/** JSON property name */
	public static final String JSON_PROPERTY_SCALE = "scale";
	/** JSON property name */
	public static final String JSON_PROPERTY_SERVICE_TYPE = "serviceType";
	/** JSON property name */
	public static final String JSON_PROPERTY_TYPE = "type";
	/** JSON property name */
	public static final String JSON_PROPERTY_URL = "url";
	/** JSON property name */
	public static final String JSON_PROPERTY_X = "x";
	/** JSON property name */
	public static final String JSON_PROPERTY_Y = "y";
	/** JSON property name */
	public static final String JSON_PROPERTY_Z = "z";
	
	/* tables */
	/** database table name */
	public static final String TABLE_IMAGE = "images";
	/** database table name */
	public static final String TABLE_LAYERS = "layers";
	/** database table name */
	public static final String TABLE_SENSORS = "sensors";
	/** database table name */
	public static final String TABLE_SITES = "sites";
	
	/* common */
	/** maximum length for address */
	public static final int MAX_LENGTH_ADDRESS = 256;
	/** maximum length for country */
	public static final int MAX_LENGTH_CITY = 256;
	/** maximum length for country */
	public static final int MAX_LENGTH_COUNTRY = 256;
	/** maximum length for description */
	public static final int MAX_LENGTH_DESCRIPTION = 1024;
	/** maximum length for external id */
	public static final int MAX_LENGTH_EXTERNAL_ID = 1024;
	/** maximum length for name */
	public static final int MAX_LENGTH_NAME = 256;
	/** maximum length for postal code */
	public static final int MAX_LENGTH_POSTAL_CODE = 20;
	/** maximum length for url */
	public static final int MAX_LENGTH_URL = 1024;
	/** maximum length for scale */
	public static final int MAX_LENGTH_SCALE = 50;
	
	
	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
