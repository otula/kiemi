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
package tuni.feedback.database;

import java.util.List;

import tuni.feedback.model.Questionare;

/**
 * 
 * query extensions for questionare repository
 *
 */
public interface QuestionareRepositoryExtensions {
	/**
	 * 
	 * @param questionareId optional list of filters
	 * @param maxResults optional max result count
	 * @param startPage optional start page, if start page is given, max results must also be given
	 * @return list of questionares
	 */
	public List<Questionare> findAllByQuestionareId(List<Long> questionareId, Integer maxResults, Integer startPage);
	
	/**
	 * 
	 * @param locationId
	 * @return number of questionares that refer the given location id
	 */
	public long countByLocationId(Long locationId);
	
	/**
	 * 
	 * @param locationId
	 * @return list of questionares (ids) that use the given location
	 */
	public List<Long> findAllQuestionareIdByLocationId(Long locationId);
	
	/**
	 * 
	 * @param questionareId
	 * @return list of locations used by the given questionares
	 */
	public List<Long> findAllLocationIdByQuestionareId(List<Long> questionareId);
	
	/**
	 * 
	 * @param questionareId
	 * @return true if all of the given ids exist in the database
	 */
	public boolean existById(List<Long> questionareId);
}
