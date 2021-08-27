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

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;

import tuni.lorawan.sensors.model.Data;

/**
 * Query extensions for data repository
 * 
 */
public interface DataRepositoryExtensions {
	
	/**
	 * Delete data based on the given parameters.
	 * 
	 * At least one of the parameters (from, to, dataId, type) must be given.
	 * 
	 * @param from
	 * @param to
	 * @param dataId
	 * @param nodeId
	 */
	@Modifying
	void delete(Date from, Date to, List<Long> dataId, List<String> nodeId);
	
	/**
	 * 
	 * Search data based on the given parameters. 
	 * 
	 * At least one of the parameters (from, to, dataId, type) must be given.
	 * 
	 * @param from
	 * @param to
	 * @param dataId
	 * @param nodeId
	 * @param maxResults
	 * @return list of data
	 */
	List<Data> findAll(Date from, Date to, List<Long> dataId, List<String> nodeId, Integer maxResults);
}
