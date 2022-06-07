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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.sites.exception.IdNotFoundException;
import tuni.sites.exception.InvalidParameterException;
import tuni.sites.model.Definitions;
import tuni.sites.model.Layer;
import tuni.sites.model.Sensor;
import tuni.sites.model.database.LayersRepository;
import tuni.sites.relations.database.LayerToLayerRepository;
import tuni.sites.relations.database.LayerToSensorRepository;
import tuni.sites.relations.database.SiteToLayerRepository;
import tuni.sites.relations.model.LayerToLayer;
import tuni.sites.relations.model.LayerToSensor;

/**
 * Layers service
 * 
 */
@Service
public class LayersService {
	private static final Sort SORT_DEFAULT = Sort.by(Sort.Direction.DESC, Definitions.COLUMN_ID);
	private static final Sort SORT_PARENT_TO_CHILD_RELATION = Sort.by(Sort.Direction.DESC, "childId"); // default sort order for site-to-layer relations
	@Autowired
	private LayersRepository _layersRepository = null;
	@Autowired
	private LayerToLayerRepository _layerToLayerRepository = null;
	@Autowired
	private LayerToSensorRepository _layerToSensorRepositry = null;
	@Autowired
	private SensorsService _sensorsService = null;
	@Autowired
	private SiteToLayerRepository _siteToLayerRepository = null;
	
	/**
	 * Delete layer and all associated child layers and sensors.
	 * 
	 * @param layerId
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public void deleteLayer(Long layerId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		if(!_layersRepository.existsById(layerId)) {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
		
		for(Long childId : _layerToLayerRepository.findAllChildIdbyParentId(layerId)) {
			deleteLayer(childId);
		}
		
		for(Long sensorId : _layerToSensorRepositry.findAllSensorIdbyLayerId(layerId)) {
			_sensorsService.deleteSensor(sensorId);
		}

		_siteToLayerRepository.deleteByLayerId(layerId);
		_layerToLayerRepository.deleteAllByLayerId(layerId);
		_layersRepository.deleteById(layerId);
	}

	/**
	 * 
	 * @param layerId
	 * @return layer
	 * @throws IdNotFoundException 
	 */
	public Layer getLayer(Long layerId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		Optional<Layer> layer = _layersRepository.findById(layerId);
		if(layer.isPresent()) {
			return layer.get();
		}else {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
	}
	
	/**
	 * 
	 * @param layerIds
	 * @param sort
	 * @return list of layers
	 */
	protected List<Layer> getLayers(List<Long> layerIds, Sort sort) {
		return _layersRepository.findAllByIdIn(layerIds, sort);
	}

	/**
	 * Get all child layers for the layer.
	 * 
	 * @param layerId
	 * @param maxResults
	 * @param startPage if given, maxResults must also be given.
	 * @return list of layers
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public List<Layer> getChildLayers(Long layerId, Integer maxResults, Integer startPage) throws IdNotFoundException, InvalidParameterException {
		// we could check user permissions here, but there is currently no need
		if(!_layersRepository.existsById(layerId)) {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
		
		List<Long> layerIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				layerIds = _layerToLayerRepository.findAllChildIdbyParentId(layerId, SORT_PARENT_TO_CHILD_RELATION);
			}else {
				layerIds = _layerToLayerRepository.findAllChildIdbyParentId(layerId, PageRequest.of(0, maxResults, SORT_PARENT_TO_CHILD_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			layerIds = _layerToLayerRepository.findAllChildIdbyParentId(layerId, PageRequest.of(startPage, maxResults, SORT_PARENT_TO_CHILD_RELATION)).getContent();
		}
		
		return (layerIds.isEmpty() ? new ArrayList<>() : getLayers(layerIds, SORT_DEFAULT));
	}

	/**
	 * Get all sensors for the layer.
	 * 
	 * @param layerId
	 * @param maxResults
	 * @param startPage if given, max results must also be given
	 * @return list of sensors
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public List<Sensor> getSensors(Long layerId, Integer maxResults, Integer startPage) throws IdNotFoundException, InvalidParameterException {
		// we could check user permissions here, but there is currently no need
		if(!_layersRepository.existsById(layerId)) {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
		
		List<Long> sensorIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				sensorIds = _layerToSensorRepositry.findAllSensorIdbyLayerId(layerId, SORT_PARENT_TO_CHILD_RELATION);
			}else {
				sensorIds = _layerToSensorRepositry.findAllSensorIdbyLayerId(layerId, PageRequest.of(0, maxResults, SORT_PARENT_TO_CHILD_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			sensorIds = _layerToSensorRepositry.findAllSensorIdbyLayerId(layerId, PageRequest.of(startPage, maxResults, SORT_PARENT_TO_CHILD_RELATION)).getContent();
		}
		
		return (sensorIds.isEmpty() ? new ArrayList<>() : _sensorsService.getSensors(sensorIds, SORT_DEFAULT));
	}

	/**
	 * Create and add new layer for the layer (id).
	 * 
	 * @param layer
	 * @param layerId
	 * @return id for the created layer
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public Long createLayer(Layer layer, Long layerId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		if(!_layersRepository.existsById(layerId)) {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
		
		Long id = createLayer(layer);
		_layerToLayerRepository.save(new LayerToLayer(id, layerId));
				
		return id;
	}
	
	/**
	 * Create new layer.
	 * 
	 * @param layer
	 * @return id for the created layer
	 */
	protected Long createLayer(Layer layer) {
		// we could check user permissions here, but there is currently no need
		return _layersRepository.save(layer).getId();
	}

	/**
	 * Create sensor for the given layer (id).
	 * 
	 * @param sensor
	 * @param layerId
	 * @return id for the created sensor
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public Long createSensor(Sensor sensor, Long layerId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		if(!_layersRepository.existsById(layerId)) {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
		
		Long id = _sensorsService.createSensor(sensor);
		_layerToSensorRepositry.save(new LayerToSensor(id, layerId));
		
		return id;
	}

	/**
	 * Update the layer.
	 * 
	 * @param layer
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public void updateLayer(Layer layer) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		Long layerId = layer.getId();
		if(!_layersRepository.existsById(layerId)) {
			throw new IdNotFoundException("Layer id "+layerId+" was not found.");
		}
		
		_layersRepository.save(layer);
	}
}
