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
package tuni.feedback.relations.database;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import tuni.feedback.relations.Definitions;
import tuni.feedback.relations.model.QuestionareToTimeSelection;

/**
 * 
 * 
 */
public interface QuestionareToTimeSelectionRepository extends PagingAndSortingRepository<QuestionareToTimeSelection, Long> {
	/**
	 * 
	 * @param questionareId
	 * @return list of time selection ids
	 */
	@Query("select s.childId from "+Definitions.ENTITY_QUESTIONARE_TO_TIME_SELECTION+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	List<Long> findAllTimeSelectionIdbyQuestionareId(Long questionareId);
	
	/**
	 * 
	 * @param questionareId
	 * @param pageable 
	 * @return page of time selection ids
	 */
	@Query("select s.childId from "+Definitions.ENTITY_QUESTIONARE_TO_TIME_SELECTION+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	Page<Long> findAllTimeSelectionIdbyQuestionareId(Long questionareId, Pageable pageable);
	
	/**
	 * 
	 * @param questionareId
	 * @param sort 
	 * @return list of time selection ids
	 */
	@Query("select s.childId from "+Definitions.ENTITY_QUESTIONARE_TO_TIME_SELECTION+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	List<Long> findAllTimeSelectionIdbyQuestionareId(Long questionareId, Sort sort);
	
	/**
	 * 
	 * @param timeSelectionId
	 */
	@Modifying
	@Query("delete from "+Definitions.ENTITY_QUESTIONARE_TO_TIME_SELECTION+" s where "+Definitions.COLUMN_CHILD_ID+"=?1")
	void deleteByTimeSelectionId(Long timeSelectionId);
	
	/**
	 * 
	 * @param questionareId
	 */
	@Modifying
	@Query("delete from "+Definitions.ENTITY_QUESTIONARE_TO_TIME_SELECTION+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	void deleteByQuestionareId(Long questionareId);
}
