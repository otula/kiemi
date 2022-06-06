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

import tuni.feedback.model.Definitions;
import tuni.feedback.model.TimeSelection;

/**
 * 
 * 
 */
public interface TimeSelectionRepository extends JpaRepository<TimeSelection, Long> {
	/** default sort */
	public static final Sort DEFAULT_SORT = Sort.by(tuni.feedback.database.Definitions.DEFAULT_SORT_DIRECTION, Definitions.PROPERTY_ID);
	
	/**
	 * 
	 * @param ids
	 * @param sort
	 * @return list of layers
	 */
	List<TimeSelection> findAllByIdIn(Iterable<Long> ids, Sort sort);
}
