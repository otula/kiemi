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
package tuni.lorawan.sensors.database;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import tuni.lorawan.sensors.model.Data;
import tuni.lorawan.sensors.model.Definitions;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called taskRepository
 * CRUD refers Create, Read, Update, Delete
 */
public interface DataRepository extends CrudRepository<Data, Long>, DataRepositoryExtensions {
	
	/**
	 * 
	 * @param nodeId
	 */
	@Modifying
	@Query("delete from "+Definitions.ENTITY_DATA+" where "+Definitions.COLUMN_NODE_ID+" in ?1")
	void deleteByNodeId(List<String> nodeId);
	
	@Override
	@Query("select d from "+Definitions.ENTITY_DATA+" d order by "+Definitions.COLUMN_DATA_ID+" ASC")
	List<Data> findAll();
	
	/**
	 * 
	 * @param pageable
	 * @return all data based on the given paging options
	 */
	@Query("select d from "+Definitions.ENTITY_DATA+" d order by "+Definitions.COLUMN_DATA_ID+" ASC")
	List<Data> findAll(Pageable pageable);
}
