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
package tuni.feedback.model;

/**
 * 
 * definitions
 */
public final class Definitions {
	/* columns */
	/** database column */
	public static final String COLUMN_ANSWER_ID = "answer_id";
	/** database column */
	public static final String COLUMN_AREA_ID = "area_id";
	/** database column */
	public static final String COLUMN_AREA_IMAGE_URL = "area_image_url";
	/** database column */
	public static final String COLUMN_COLOR_HINT = "color_hint";
	/** database column */
	public static final String COLUMN_CONTINUOUS = "continuous";
	/** database column */
	public static final String COLUMN_COORDINATE_ID = "coordinate_id";
	/** database column */
	public static final String COLUMN_COUNT = "count";
	/** database column */
	public static final String COLUMN_DESCRIPTION = "description";
	/** database column */
	public static final String COLUMN_DEFAULT_VALUE = "default_value";
	/** database column */
	public static final String COLUMN_END = "end";
	/** database column */
	public static final String COLUMN_END_UTC_SECONDS = "end_utc_seconds";
	/** database column */
	public static final String COLUMN_INDEX_NRO = "index_nro";
	/** database column */
	public static final String COLUMN_IMAGE_URL = "image_url";
	/** database column */
	public static final String COLUMN_LOCATION_ID = "location_id";
	/** database column */
	public static final String COLUMN_NAME = "name";
	/** database column */
	public static final String COLUMN_QUESTIONARE_ID = "questionare_id";
	/** database column */
	public static final String COLUMN_QUESTIONARE_USER_ID = "questionare_user_id";
	/** database column */
	public static final String COLUMN_QUESTION_ID = "question_id";
	/** database column */
	public static final String COLUMN_QUESTION_ANSWER_ID = "question_answer_id";
	/** database column */
	public static final String COLUMN_QUESTION_VALUE_ID = "question_value_id";
	/** database column */
	public static final String COLUMN_MAX_SELECTIONS = "max_selections";
	/** database column */
	public static final String COLUMN_RELATION_ID = "relation_id";
	/** database column */
	public static final String COLUMN_REPORT_TIMESTAMP_ID = "report_timestamp_id";
	/** database column */
	public static final String COLUMN_ROW_CREATED = "row_created";
	/** database column */
	public static final String COLUMN_ROW_UPDATED = "row_updated";
	/** database column */
	public static final String COLUMN_START = "start";
	/** database column */
	public static final String COLUMN_START_UTC_SECONDS = "start_utc_seconds";
	/** database column */
	public static final String COLUMN_STATISTICS_ID = "statistics_id";
	/** database column */
	public static final String COLUMN_SUBMIT_TIMESTAMP = "submit_timestamp";
	/** database column */
	public static final String COLUMN_TIME_SELECTION_ID = "time_selection_id";
	/** database column */
	public static final String COLUMN_USERNAME = "username";
	/** database column */
	public static final String COLUMN_TEXT = "text";
	/** database column */
	public static final String COLUMN_TOKEN = "token";
	/** database column */
	public static final String COLUMN_TOKEN_ID = "token_id";
	/** database column */
	public static final String COLUMN_USE_SERVICE_USERS = "use_service_users";
	/** database column */
	public static final String COLUMN_USER_ID = "user_id";
	/** database column */
	public static final String COLUMN_VALUE = "value";
	/** database column */
	public static final String COLUMN_X = "x";
	/** database column */
	public static final String COLUMN_Y = "y";
	
	/* entities */
	/** entity name */
	public static final String ENTITY_ANSWER = "answer";
	/** entity name */
	public static final String ENTITY_AREA = "area";
	/** entity name */
	public static final String ENTITY_COORDINATE = "coordinate";
	/** entity name */
	public static final String ENTITY_LOCATION = "location";
	/** entity name */
	public static final String ENTITY_QUESTIONARE = "questionare";
	/** entity name */
	public static final String ENTITY_QUESTIONARE_USER = "questionareUser";
	/** entity name */
	public static final String ENTITY_QUESTION = "question";
	/** entity name */
	public static final String ENTITY_QUESTION_ANSWER = "questionAnswer";
	/** entity name */
	public static final String ENTITY_QUESTION_VALUE = "questionValue";
	/** entity name */
	public static final String ENTITY_REPORT_TIMESTAMP = "reportTimestamp";
	/** entity name */
	public static final String ENTITY_STATISTICS = "statistics";
	/** entity name */
	public static final String ENTITY_TIME_SELECTION = "timeSelection";
	/** entity name */
	public static final String ENTITY_USER_TOKEN = "userToken";
	
	/* properties */
	/** property name */
	public static final String PROPERTY_AREA_ID = "areaId";
	/** JSON property name */
	public static final String PROPERTY_AREA_IMAGE_URL = "areaImageUrl";
	/** JSON property name */
	public static final String PROPERTY_COLOR_HINT = "colorHint";
	/** JSON property name */
	public static final String PROPERTY_CONTINUOUS = "continuous";
	/** JSON property name */
	public static final String PROPERTY_COUNT = "count";
	/** JSON property name */
	public static final String PROPERTY_DEFAULT_VALUE = "defaultValue";
	/** JSON property name */
	public static final String PROPERTY_DESCRIPTION = "description";
	/** JSON property name */
	public static final String PROPERTY_END = "end";
	/** JSON property name */
	public static final String PROPERTY_ID = "id";
	/** JSON property name */
	public static final String PROPERTY_INDEX_NRO = "indexNro";
	/** JSON property name */
	public static final String PROPERTY_IMAGE_URL = "imageUrl";
	/** JSON property name */
	public static final String PROPERTY_LINK = "link";
	/** JSON property name */
	public static final String PROPERTY_LOCATION_ID = "locationId";
	/** JSON property name */
	public static final String PROPERTY_MAX_SELECTIONS = "maxSelections";
	/** JSON property name */
	public static final String PROPERTY_NAME = "name";
	/** JSON property name */
	public static final String PROPERTY_PASSWORD = "password";
	/** JSON property name */
	public static final String PROPERTY_POLYGON = "polygon";
	/** JSON property name */
	public static final String PROPERTY_REPORT_TIMESTAMP = "reportTimestamp";
	/** JSON property name */
	public static final String PROPERTY_QUESTION_ANSWER = ENTITY_QUESTION_ANSWER;
	/** JSON property name */
	public static final String PROPERTY_QUESTION_ID = "questionId";
	/** JSON property name */
	public static final String PROPERTY_QUESTIONARE_ID = "questionareId";
	/** JSON property name */
	public static final String PROPERTY_USERNAME = "username";
	/** JSON property name */
	public static final String PROPERTY_START = "start";
	/** JSON property name */
	public static final String PROPERTY_SUBMIT_TIMESTAMP = "submitTimestamp";
	/** JSON property name */
	public static final String PROPERTY_TEXT = "text";
	/** JSON property name */
	public static final String PROPERTY_TOKEN = "token";
	/** JSON property name */
	public static final String PROPERTY_USE_SERVICE_USERS = "useServiceUsers";
	/** JSON property name */
	public static final String PROPERTY_USER_ID = "userId";
	/** JSON property name */
	public static final String PROPERTY_VALUE = "value";
	/** JSON property name */
	public static final String PROPERTY_X = "x";
	/** JSON property name */
	public static final String PROPERTY_Y = "y";
	
	/* tables */
	/** database table name */
	public static final String TABLE_ANSWER = "answer";
	/** database table name */
	public static final String TABLE_AREA = "area";
	/** database table name */
	public static final String TABLE_COORDINATE = "coordinate";
	/** database table name */
	public static final String TABLE_LOCATION = "location";
	/** database table name */
	public static final String TABLE_REPORT_TIMESTAMP = "report_timestamp";
	/** database table name */
	public static final String TABLE_QUESTIONARE = "questionare";
	/** database table name */
	public static final String TABLE_QUESTIONARE_USER = "questionare_user";
	/** database table name */
	public static final String TABLE_QUESTION = "question";
	/** database table name */
	public static final String TABLE_QUESTION_ANSWER = "question_answer";
	/** database table name */
	public static final String TABLE_QUESTION_VALUE = "question_value";
	/** database table name */
	public static final String TABLE_STATISTICS = "statistics";
	/** database table name */
	public static final String TABLE_TIME_SELECTION = "time_selection";
	/** database table name */
	public static final String TABLE_USER_TOKEN = "user_token";
	
	/* common */
	/** maximum length for color */
	public static final int MAX_LENGTH_COLOR = 7;
	/** maximum length for description */
	public static final int MAX_LENGTH_DESCRIPTION = 1024;
	/** maximum length for name */
	public static final int MAX_LENGTH_NAME = 256;
	/** maximum length for text */
	public static final int MAX_LENGTH_TEXT = MAX_LENGTH_DESCRIPTION;
	/** maximum length for url */
	public static final int MAX_LENGTH_URL = 1024;
	
	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
