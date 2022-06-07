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
package tuni.sites.relations.database;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import tuni.sites.relations.model.Definitions;
import tuni.sites.relations.model.LayerToLayer;

/**
 * layer-to-layer repository
 * 
 */
public interface LayerToLayerRepository extends PagingAndSortingRepository<LayerToLayer, Long> {
	/**
	 * Delete all associations where the given layerId is either child or parent id.
	 * 
	 * @param layerId
	 */
	@Modifying
	@Query("delete from "+Definitions.ENTITY_LAYER_TO_LAYER+" l where "+Definitions.COLUMN_CHILD_ID+"=?1 or "+Definitions.COLUMN_PARENT_ID+"=?1")
	void deleteAllByLayerId(Long layerId);
	
	/**
	 * 
	 * @param parentLayerId
	 * @return list of layer ids
	 */
	@Query("select l.childId from "+Definitions.ENTITY_LAYER_TO_LAYER+" l where "+Definitions.COLUMN_PARENT_ID+"=?1")
	List<Long> findAllChildIdbyParentId(Long parentLayerId);
	
	/**
	 * 
	 * @param parentLayerId
	 * @param pageable 
	 * @return page of layer ids
	 */
	@Query("select l.childId from "+Definitions.ENTITY_LAYER_TO_LAYER+" l where "+Definitions.COLUMN_PARENT_ID+"=?1")
	Page<Long> findAllChildIdbyParentId(Long parentLayerId, Pageable pageable);
	
	/**
	 * 
	 * @param parentLayerId
	 * @param sort 
	 * @return list of layer ids
	 */
	@Query("select l.childId from "+Definitions.ENTITY_LAYER_TO_LAYER+" l where "+Definitions.COLUMN_PARENT_ID+"=?1")
	List<Long> findAllChildIdbyParentId(Long parentLayerId, Sort sort);
}
