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
package tuni.feedback.relations;

import org.springframework.data.domain.Sort;

/**
 * 
 * definitions
 */
public final class Definitions {
	/* columns */
	/** database column */
	public static final String COLUMN_CHILD_ID = "child_id";
	/** database column */
	public static final String COLUMN_PARENT_ID = "parent_id";
	/** database column */
	public static final String COLUMN_ROW_CREATED = "row_created";
	/** database column */
	public static final String COLUMN_ROW_UPDATED = "row_updated";
	
	/* entities */
	/** entity name */
	public static final String ENTITY_LOCATION_TO_AREA = "locationToArea";
	/** entity name */
	public static final String ENTITY_QUESTIONARE_TO_ANSWER = "questionareToAnswer";
	/** entity name */
	public static final String ENTITY_QUESTIONARE_TO_QUESTION = "questionareToQuestion";
	/** entity name */
	public static final String ENTITY_QUESTIONARE_TO_TIME_SELECTION = "questionareToTimeSelection";
	/** entity name */
	public static final String ENTITY_QUESTIONARE_TO_USER = "questionareToUser";
	
	/* tables */
	/** database table name */
	public static final String TABLE_LOCATION_TO_AREA = "location_to_area";
	/** database table name */
	public static final String TABLE_QUESTIONARE_TO_ANSWER = "questionare_to_answer";
	/** database table name */
	public static final String TABLE_QUESTIONARE_TO_QUESTION = "questionare_to_question";
	/** database table name */
	public static final String TABLE_QUESTIONARE_TO_TIME_SELECTION = "questionare_to_time_selection";
	/** database table name */
	public static final String TABLE_QUESTIONARE_TO_USER = "questionare_to_user";
	
	/* common */
	/** default sort */
	public static final Sort SORT_PARENT_TO_CHILD_RELATION = Sort.by(tuni.feedback.database.Definitions.DEFAULT_SORT_DIRECTION, "childId"); // default sort order for parent-tochild relations

	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
