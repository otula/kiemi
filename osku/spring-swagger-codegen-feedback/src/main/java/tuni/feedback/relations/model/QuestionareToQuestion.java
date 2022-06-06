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
package tuni.feedback.relations.model;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import tuni.feedback.relations.Definitions;

/**
 * 
 * 
 */
@Entity(name = Definitions.ENTITY_QUESTIONARE_TO_QUESTION) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_QUESTIONARE_TO_QUESTION, indexes = @Index(name = Definitions.COLUMN_PARENT_ID + "_" + Definitions.COLUMN_CHILD_ID + "_unique", columnList = Definitions.COLUMN_PARENT_ID + "," + Definitions.COLUMN_CHILD_ID, unique = true))
public class QuestionareToQuestion extends ParentToChild {

	/**
	 * 
	 */
	public QuestionareToQuestion() {
		super();
	}

	/**
	 * 
	 * @param questionID
	 * @param questionareId
	 */
	public QuestionareToQuestion(Long questionID, Long questionareId) {
		super(questionID, questionareId);
	}
}
