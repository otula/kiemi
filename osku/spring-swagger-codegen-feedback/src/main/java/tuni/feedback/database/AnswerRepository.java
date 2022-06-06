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
package tuni.feedback.database;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tuni.feedback.model.Answer;
import tuni.feedback.model.Definitions;

/**
 * 
 * 
 */
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	/** default sort */
	public static final Sort DEFAULT_SORT = Sort.by(tuni.feedback.database.Definitions.DEFAULT_SORT_DIRECTION, Definitions.PROPERTY_ID);
	
	/**
	 * 
	 * @param ids
	 * @param sort
	 * @return list of answers
	 */
	List<Answer> findAllByIdIn(Iterable<Long> ids, Sort sort);
	
	/**
	 * 
	 * @param ids
	 * @param from 
	 * @param sort
	 * @return list of answers
	 */
	@Query("select a from "+Definitions.ENTITY_ANSWER+" a where "+Definitions.COLUMN_ANSWER_ID+" IN (?1) and "+Definitions.COLUMN_SUBMIT_TIMESTAMP+">=?2")
	List<Answer> findAllBySubmitTimestampFromAndIdIn(Iterable<Long> ids, long from, Sort sort);
	
	/**
	 * 
	 * @param ids
	 * @param from 
	 * @param to 
	 * @param sort
	 * @return list of answers
	 */
	@Query("select a from "+Definitions.ENTITY_ANSWER+" a where "+Definitions.COLUMN_ANSWER_ID+" IN (?1) and "+Definitions.COLUMN_SUBMIT_TIMESTAMP+">=?2 and "+Definitions.COLUMN_SUBMIT_TIMESTAMP+"<=?3")
	List<Answer> findAllBySubmitTimestampFromToAndIdIn(Iterable<Long> ids, long from, long to, Sort sort);
	
	/**
	 * 
	 * @param ids
	 * @param to 
	 * @param sort
	 * @return list of answers
	 */
	@Query("select a from "+Definitions.ENTITY_ANSWER+" a where "+Definitions.COLUMN_ANSWER_ID+" IN (?1) and "+Definitions.COLUMN_SUBMIT_TIMESTAMP+"<=?2")
	List<Answer> findAllBySubmitTimestampToAndIdIn(Iterable<Long> ids, long to, Sort sort);
	
	/**
	 * 
	 * @param ids
	 * @param from 
	 * @param to 
	 * @param sort
	 * @return list of answers
	 */
	@Query("select a from "+Definitions.ENTITY_ANSWER+" a where "+Definitions.COLUMN_SUBMIT_TIMESTAMP+">=?1 and "+Definitions.COLUMN_SUBMIT_TIMESTAMP+"<=?2")
	List<Answer> findAllBySubmitTimestampFromTo(long from, long to);
}
