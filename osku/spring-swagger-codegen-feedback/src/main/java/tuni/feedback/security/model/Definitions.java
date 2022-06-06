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
package tuni.feedback.security.model;

/**
 * 
 * 
 */
public final class Definitions {
	/* entity names */
	/** entity */
	public static final String ENTITY_USER = "user";
	/** entity */
	public static final String ENTITY_USER_AUTHORITY = "userAuthority";
	/* tables */
	/** database table */
	public static final String TABLE_USERS = "users";
	/** database table */
	public static final String TABLE_USER_AUTHORITIES = "user_authorities";
	
	/* columns */
	/** database column */
	public static final String COLUMN_AUTHORITY = "authority";
	/** database column */
	public static final String COLUMN_CREATED = "created";
	/** database column */
	public static final String COLUMN_ACCOUNT_NON_EXPIRED = "account_non_expired";
	/** database column */
	public static final String COLUMN_ACCOUNT_NON_LOCKED = "account_non_locked";
	/** database column */
	public static final String COLUMN_CREDENTIALS_NON_EXPIRED = "credentials_non_expired";
	/** database column */
	public static final String COLUMN_ENABLED = "enabled";
	/** database column */
	public static final String COLUMN_USER_AUTHORITY_ID = "user_authority_id";
	/** database column */
	public static final String COLUMN_USER_ID = "user_id";
	/** database column */
	public static final String COLUMN_PASSWORD = "password";
	/** database column */
	public static final String COLUMN_UPDATED = "updated";
	/** database column */
	public static final String COLUMN_USERNAME = "username";
	
	/* authority roles */
	/** admin role string */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	/** user role string */
	public static final String ROLE_USER = "ROLE_USER";
	
	/* common */
	/** maximum length for url column */
	public static final int MAX_LENGTH_PASSWORD = 256;
	/** maximum length for username */
	public static final int MAX_LENGTH_USERNAME = 256;

	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
