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
package tuni.feedback.api;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.feedback.model.Area;
import tuni.feedback.model.Coordinate;
import tuni.feedback.model.Location;
import tuni.feedback.service.LocationsService;

/**
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Controller
@Api(tags = { "locations" })
public class LocationsApiController implements LocationsApi {
	@Autowired
	private LocationsService _service = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public LocationsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<Void> deleteArea(@ApiParam(value = "Delete area from the location, which has the given id.", required = true) @PathVariable("locationId") Long locationId, @ApiParam(value = "Delete area, which has the given id.", required = true) @PathVariable("areaId") Long areaId) {
		_service.deleteArea(locationId, areaId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteLocation(@ApiParam(value = "Delete location, which has the given id.", required = true) @PathVariable("locationId") Long locationId) {
		_service.deleteLocation(locationId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Area>> getAreas(@ApiParam(value = "Return area(s) for the location, which has the given id.", required = true) @PathVariable("locationId") Long locationId, @ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getAreas(locationId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Location>> getLocation(@ApiParam(value = "Get location, which has the given id.", required = true) @PathVariable("locationId") Long locationId) {
		Location location = _service.getLocation(locationId);
		if (location == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			List<Location> list = new ArrayList<>(1);
			list.add(location);
			return new ResponseEntity<>(list, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<List<Location>> getLocations(@ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getLocations(maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Area>> postArea(@ApiParam(value = "Add new area for the location, which has the given id.", required = true) @PathVariable("locationId") Long locationId, @ApiParam(value = "The area as an HTTP body.", required = true) @Valid @RequestBody Area body) {
		// make sure no ids are given
		body.setId(null);
		List<Coordinate> polygon = body.getPolygon();
		if (polygon != null && !polygon.isEmpty()) {
			for (Coordinate c : polygon) {
				c.setId(null);
			}
		}

		Long id = _service.createArea(locationId, body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Area> list = new ArrayList<>(1);
		Area a = new Area();
		a.setId(id);
		list.add(a);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Location>> postLocation(@ApiParam(value = "The location as an HTTP body.", required = true) @Valid @RequestBody Location body) {
		body.setId(null); // make sure no ids are given
		Long id = _service.createLocation(body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Location> list = new ArrayList<>(1);
		Location l = new Location();
		l.setId(id);
		list.add(l);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

}
