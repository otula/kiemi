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
import tuni.feedback.relations.model.LocationToArea;

/**
 * 
 * 
 */
public interface LocationToAreaRepository extends PagingAndSortingRepository<LocationToArea, Long> {
	/**
	 * 
	 * @param locationId
	 * @return list of area ids
	 */
	@Query("select s.childId from "+Definitions.ENTITY_LOCATION_TO_AREA+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	List<Long> findAllAreaIdbyLocationId(Long locationId);
	
	/**
	 * 
	 * @param locationId
	 * @param pageable 
	 * @return page of area ids
	 */
	@Query("select s.childId from "+Definitions.ENTITY_LOCATION_TO_AREA+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	Page<Long> findAllAreaIdbyLocationId(Long locationId, Pageable pageable);
	
	/**
	 * 
	 * @param locationId
	 * @param sort 
	 * @return list of area ids
	 */
	@Query("select s.childId from "+Definitions.ENTITY_LOCATION_TO_AREA+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	List<Long> findAllAreaIdbyLocationId(Long locationId, Sort sort);
	
	/**
	 * 
	 * @param areaId
	 */
	@Modifying
	@Query("delete from "+Definitions.ENTITY_LOCATION_TO_AREA+" s where "+Definitions.COLUMN_CHILD_ID+"=?1")
	void deleteByAreaId(Long areaId);
	
	/**
	 * 
	 * @param locationId
	 */
	@Modifying
	@Query("delete from "+Definitions.ENTITY_LOCATION_TO_AREA+" s where "+Definitions.COLUMN_PARENT_ID+"=?1")
	void deleteByLocationId(Long locationId);
	
	/**
	 * 
	 * @param parentId
	 * @param childId
	 * @return true if the association exists
	 */
	boolean existsByParentIdAndChildId(Long parentId, Long childId);
}
