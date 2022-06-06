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
package tuni.feedback.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.feedback.database.AreaRepository;
import tuni.feedback.database.LocationRepository;
import tuni.feedback.database.QuestionareRepository;
import tuni.feedback.exception.ForbiddenException;
import tuni.feedback.exception.IdNotFoundException;
import tuni.feedback.exception.InvalidParameterException;
import tuni.feedback.model.Area;
import tuni.feedback.model.Location;
import tuni.feedback.permissions.model.UserPermission.Permission;
import tuni.feedback.permissions.service.PermissionService;
import tuni.feedback.relations.database.LocationToAreaRepository;
import tuni.feedback.relations.model.LocationToArea;

/**
 * 
 * 
 */
@Service
public class LocationsService {
	private static final Logger LOGGER = LogManager.getLogger(LocationsService.class);
	@Autowired
	private PermissionService _permissionService = null;
	@Autowired
	private LocationRepository _locationRepository = null;
	@Autowired
	private QuestionareRepository _questionareRepository = null;
	@Autowired
	private AreaRepository _areaRepository = null;
	@Autowired
	private LocationToAreaRepository _locationToAreaRepository = null;


	/**
	 * 
	 * @param locationId
	 * @param areaId
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public void deleteArea(Long locationId, Long areaId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.isAdmin()) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_locationRepository.existsById(locationId)) {
			throw new IdNotFoundException("Location id: "+locationId+" was not found.");
		}
		if(!_areaRepository.existsById(areaId)) {
			throw new IdNotFoundException("Area id: "+areaId+" was not found.");
		}
		_locationToAreaRepository.deleteByAreaId(areaId);
		_areaRepository.deleteById(areaId);  //TODO the area may be referred by a questionare answer
	}

	/**
	 * 
	 * @param locationId
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public void deleteLocation(Long locationId) throws IdNotFoundException, InvalidParameterException, ForbiddenException {
		if(!_permissionService.isAdmin()) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_locationRepository.existsById(locationId)) {
			throw new IdNotFoundException("Location id: "+locationId+" was not found.");
		}
		
		if(_questionareRepository.countByLocationId(locationId) > 0) {
			throw new InvalidParameterException("Cannot remove location, id: "+locationId+": references by questionare(s).");
		}
		
		List<Long> ids = _locationToAreaRepository.findAllAreaIdbyLocationId(locationId);
		if(ids != null && !ids.isEmpty()) {
			for(Long id : ids) {
				_areaRepository.deleteById(id); //TODO the area may be referred by a questionare answer
			}
			_locationToAreaRepository.deleteByLocationId(locationId);
		}
		
		_locationRepository.deleteById(locationId);
	}

	/**
	 * 
	 * @param locationId
	 * @param maxResults
	 * @param startPage
	 * @return areas
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 * @throws ForbiddenException 
	 */
	public List<Area> getAreas(Long locationId, @Valid Integer maxResults, @Valid Integer startPage) throws IdNotFoundException, InvalidParameterException, ForbiddenException {
		if(!_permissionService.hasLocationPermission(locationId, Permission.READ_ONLY)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		if(!_locationRepository.existsById(locationId)) {
			throw new IdNotFoundException("Location id: "+locationId+" was not found.");
		}
		
		List<Long> areaIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				areaIds = _locationToAreaRepository.findAllAreaIdbyLocationId(locationId, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION);
			}else {
				areaIds = _locationToAreaRepository.findAllAreaIdbyLocationId(locationId, PageRequest.of(0, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			areaIds = _locationToAreaRepository.findAllAreaIdbyLocationId(locationId, PageRequest.of(startPage, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();
		}
		
		return (areaIds.isEmpty() ? new ArrayList<>() : _areaRepository.findAllByIdIn(areaIds, AreaRepository.DEFAULT_SORT));
	}

	/**
	 * 
	 * @param locationId
	 * @return location
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	public Location getLocation(Long locationId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasLocationPermission(locationId, Permission.READ_ONLY)) {
			throw new ForbiddenException("Permission was denied.");
		}
		Optional<Location> q = _locationRepository.findById(locationId);
		if(q.isPresent()) {
			return q.get();
		}else {
			throw new IdNotFoundException("Location id: "+locationId+" was not found.");
		}
	}

	/**
	 * 
	 * @param maxResults
	 * @param startPage
	 * @return locations
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public List<Location> getLocations(@Valid Integer maxResults, @Valid Integer startPage) throws InvalidParameterException {
		if(startPage != null && startPage != 0 && maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}
		List<Long> locationIds = null;
		if(!_permissionService.isAdmin()) {
			List<Long> questionareIds = _permissionService.getPermittedQuestionareIds();
			if(questionareIds == null || questionareIds.isEmpty()) {
				return new ArrayList<>(0);
			}
			locationIds = _questionareRepository.findAllLocationIdByQuestionareId(questionareIds);
			if(locationIds == null) {
				LOGGER.debug("The user's questionares do not use any locations.");
				return new ArrayList<>(0);
			}
		}
		
		return _locationRepository.findAllByLocationId(locationIds, maxResults, startPage);
	}

	/**
	 * 
	 * @param locationId
	 * @param area
	 * @return id for the created area
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public Long createArea(Long locationId, @Valid Area area) throws IdNotFoundException, ForbiddenException, InvalidParameterException {
		if(!_permissionService.isAdmin()) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		Optional<Location> location = _locationRepository.findById(locationId);
		if(location.isEmpty()) {
			throw new IdNotFoundException("Location id: "+locationId+" was not found.");
		}
		
		if(StringUtils.isBlank(location.get().getAreaImageUrl()) && area.getPolygon() != null) {
			throw new InvalidParameterException("Cannot set polygon for location, id: "+locationId+": the location does not have an area image.");
		}
		
		Long id = _areaRepository.save(area).getId();
		_locationToAreaRepository.save(new LocationToArea(id, locationId));
		return id;
	}

	/**
	 * 
	 * @param location
	 * @return id for the created location
	 * @throws ForbiddenException 
	 */
	public Long createLocation(@Valid Location location) throws ForbiddenException {
		if(!_permissionService.isAdmin()) {
			throw new ForbiddenException("Permission was denied.");
		}
		return _locationRepository.save(location).getId();
	}
}
