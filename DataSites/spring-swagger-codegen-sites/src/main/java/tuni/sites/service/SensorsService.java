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
package tuni.sites.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.sites.exception.IdNotFoundException;
import tuni.sites.model.Sensor;
import tuni.sites.model.database.SensorsRepository;
import tuni.sites.relations.database.LayerToSensorRepository;

/**
 * Sensors service
 * 
 */
@Service
public class SensorsService {
	@Autowired
	private SensorsRepository _sensorsRepository = null;
	@Autowired
	private LayerToSensorRepository _layerToSensorRepository = null;

	/**
	 * 
	 * @param sensorId
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public void deleteSensor(Long sensorId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		if(!_sensorsRepository.existsById(sensorId)) {
			throw new IdNotFoundException("Sensor id "+sensorId+" was not found.");
		}
		
		_layerToSensorRepository.deleteBySensorId(sensorId);
		_sensorsRepository.deleteById(sensorId);
	}

	/**
	 * 
	 * @param sensorId
	 * @return sensor
	 * @throws IdNotFoundException 
	 */
	public Sensor getSensor(Long sensorId) throws IdNotFoundException {
		Optional<Sensor> sensor = _sensorsRepository.findById(sensorId);
		if(sensor.isPresent()) {
			return sensor.get();
		}else {
			throw new IdNotFoundException("Sensor id "+sensorId+" was not found.");
		}
	}
	
	/**
	 * 
	 * @param sensorIds
	 * @param sort
	 * @return status and sensor
	 */
	protected List<Sensor> getSensors(List<Long> sensorIds, Sort sort) {
		return _sensorsRepository.findAllByIdIn(sensorIds, sort);
	}

	/**
	 * 
	 * @param sensor
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public void updateSensor(Sensor sensor) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		Long sensorId = sensor.getId();
		if(!_sensorsRepository.existsById(sensorId)) {
			throw new IdNotFoundException("Sensor id "+sensorId+" was not found.");
		}
		
		_sensorsRepository.save(sensor);
	}

	/**
	 * Create a new sensor.
	 * 
	 * @param sensor
	 * @return status and id for the created sensor
	 */
	protected Long createSensor(Sensor sensor) {
		// we could check user permissions here, but there is currently no need
		return _sensorsRepository.save(sensor).getId();
	}
}
