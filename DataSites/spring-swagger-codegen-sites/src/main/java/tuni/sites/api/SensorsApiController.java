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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.sites.model.Sensor;
import tuni.sites.service.SensorsService;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
@Controller
@Api(tags = { "sensors" })
public class SensorsApiController implements SensorsApi {
	@Autowired
	private SensorsService _sensorsService = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public SensorsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<Void> deleteSensorById(@ApiParam(value = "Delete the sensor, which has the given id.", required = true) @PathVariable("sensorId") Long sensorId) {
		_sensorsService.deleteSensor(sensorId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Sensor>> getSensorById(@ApiParam(value = "Return sensor, which has the given id.", required = true) @PathVariable("sensorId") Long sensorId) {
		Sensor sensor = _sensorsService.getSensor(sensorId);
		List<Sensor> sensors = new ArrayList<>(1);
		if(sensor != null) {
			sensors.add(sensor);
		}
		return new ResponseEntity<>(sensors, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putSensorById(@ApiParam(value = "The updated sensor data as an HTTP body.", required = true) @Valid @RequestBody Sensor body, @ApiParam(value = "Update sensor, which has the given id.", required = true) @PathVariable("sensorId") Long sensorId) {
		body.setId(sensorId);
		_sensorsService.updateSensor(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
