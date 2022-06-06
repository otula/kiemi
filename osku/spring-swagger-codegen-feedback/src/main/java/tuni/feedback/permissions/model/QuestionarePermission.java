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
package tuni.feedback.permissions.model;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import tuni.feedback.permissions.Definitions;

/**
 * Questionare user permission
 * 
 */
@Entity(name = Definitions.ENTITY_QUESTIONARE_PERMISSION) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_QUESTIONARE_PERMISSION, indexes = @Index(name = Definitions.COLUMN_USER_ID + "_" + Definitions.COLUMN_PERMISSION_ID + "_unique", columnList = Definitions.COLUMN_USER_ID + "," + Definitions.COLUMN_PERMISSION_ID, unique = true))
public class QuestionarePermission extends UserPermission {
	/**
	 * 
	 */
	public QuestionarePermission() {
		super();
	}

	/**
	 * 
	 * @param questionareId
	 * @param userId
	 */
	public QuestionarePermission(Long questionareId, Long userId) {
		super(questionareId, userId);
	}
}
