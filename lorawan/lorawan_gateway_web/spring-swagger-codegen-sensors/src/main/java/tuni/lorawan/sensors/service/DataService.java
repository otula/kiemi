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
package tuni.lorawan.sensors.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.lorawan.sensors.database.DataRepository;
import tuni.lorawan.sensors.model.Data;

/**
 * 
 * 
 */
@Service
public class DataService {
	private static final Logger LOG = LoggerFactory.getLogger(DataService.class);
	@Autowired
	private DataRepository _repository = null;

	/**
	 * 
	 * @param from
	 * @param to
	 * @param dataId
	 * @param nodeId
	 * @return true on success, false on failure
	 */
	@Transactional
	public boolean deleteData(Date from, Date to, List<Long> dataId, List<String> nodeId) {
		if(from != null || to != null || (dataId != null && !dataId.isEmpty()) || (nodeId != null && !nodeId.isEmpty())) {
			_repository.delete(from, to, dataId, nodeId);
			return true;
		}else {
			LOG.warn("Attempted delete without parameters, this would cause the entire table to be cleared.");
			return false;
		}
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param dataId
	 * @param nodeId
	 * @param maxResults 
	 * @return list of data or an empty list if none was found
	 */
	public List<Data> getData(Date from, Date to, List<Long> dataId, List<String> nodeId, Integer maxResults) {
		if(from != null || to != null || (dataId != null && !dataId.isEmpty()) || (nodeId != null && !nodeId.isEmpty())){
			return _repository.findAll(from, to, dataId, nodeId, maxResults);
		}else if(maxResults != null){
			return _repository.findAll(PageRequest.of(0, maxResults));
		}else {
			return _repository.findAll();
		}
	}
}
