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
package tuni.feedback.permissions;

/**
 * 
 * definitions
 */
public final class Definitions {
	/* columns */
	/** database column */
	public static final String COLUMN_PERMISSION = "permission";
	/** database column */
	public static final String COLUMN_PERMISSION_ID = "permission_id";
	/** database column */
	public static final String COLUMN_RELATION_ID = "relation_id";
	/** database column */
	public static final String COLUMN_ROW_CREATED = "row_created";
	/** database column */
	public static final String COLUMN_ROW_UPDATED = "row_updated";
	/** database column */
	public static final String COLUMN_USER_ID = tuni.feedback.security.model.Definitions.COLUMN_USER_ID;
	
	/* entities */
	/** entity name */
	public static final String ENTITY_QUESTIONARE_PERMISSION = "questionarePermission";
	
	/* tables */
	/** database table name */
	public static final String TABLE_QUESTIONARE_PERMISSION = "questionare_permission";
	
	/* application properties */
	/** permission property */
	public static final String PROPERTY_PERMISSION_BASEURI = "permissions.link.baseuri";
	/** permission property */
	public static final String PROPERTY_PERMISSION_TOKEN_LENGTH = "permissions.token.length";
	
	/** url parameter for token */
	public static final String PARAMETER_USER_TOKEN = "?token=";
	
	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
