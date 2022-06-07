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
package tuni.sites.api;

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
import tuni.sites.model.Layer;
import tuni.sites.model.Sensor;
import tuni.sites.service.LayersService;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:28:26.053990+03:00[Europe/Helsinki]")
@Controller
@Api(tags = { "layers" })
public class LayersApiController implements LayersApi {
	@Autowired
	private LayersService _layersService = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public LayersApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<Void> deleteLayerById(@ApiParam(value = "Delete the layer, which has the given id.", required = true) @PathVariable("layerId") Long layerId) {
		_layersService.deleteLayer(layerId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Layer>> getLayerById(@ApiParam(value = "return the site, which has the given id.", required = true) @PathVariable("layerId") Long layerId) {
		Layer layer = _layersService.getLayer(layerId);
		List<Layer> layers = new ArrayList<>(1);
		if(layer != null) {
			layers.add(layer);
		}
		return new ResponseEntity<>(layers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Layer>> getLayers(@ApiParam(value = "return the layer, which has the given id.", required = true) @PathVariable("layerId") Long layerId, @ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_layersService.getChildLayers(layerId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Sensor>> getSensors(@ApiParam(value = "return the layer, which has the given id.", required = true) @PathVariable("layerId") Long layerId, @ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_layersService.getSensors(layerId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Layer>> postLayer(@ApiParam(value = "The layer to create as an HTTP body.", required = true) @Valid @RequestBody Layer body, @ApiParam(value = "Update the layer, which has the given id.", required = true) @PathVariable("layerId") Long layerId) {
		body.setId(null);
		Long id = _layersService.createLayer(body, layerId);
		List<Layer> layers = new ArrayList<>(1);
		if(id != null) {
			body.setId(id);
			layers.add(body);
		}
		return new ResponseEntity<>(layers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Sensor>> postSensor(@ApiParam(value = "The sensor to create as an HTTP body.", required = true) @Valid @RequestBody Sensor body, @ApiParam(value = "Update the layer, which has the given id.", required = true) @PathVariable("layerId") Long layerId) {
		body.setId(null);
		Long id = _layersService.createSensor(body, layerId);
		List<Sensor> sensors = new ArrayList<>(1);
		if(id != null) {
			body.setId(id);
			sensors.add(body);
		}
		return new ResponseEntity<>(sensors, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putLayerById(@ApiParam(value = "The updated layer data as an HTTP body.", required = true) @Valid @RequestBody Layer body, @ApiParam(value = "Update layer, which has the given id.", required = true) @PathVariable("layerId") Long layerId) {
		body.setId(layerId);
		_layersService.updateLayer(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
