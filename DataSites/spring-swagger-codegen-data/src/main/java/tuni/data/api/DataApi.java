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
/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package tuni.data.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import tuni.data.adapters.datatypes.Data;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-29T23:09:47.580174+03:00[Europe/Helsinki]")
@Api(value = "data")
public interface DataApi {

	/**
	 * 
	 * @param sensorId
	 * @param from
	 * @param to
	 * @param params
	 * @return response
	 */
	@ApiOperation(value = "Return data.", nickname = "getData", notes = "", response = Object.class, responseContainer = "List", authorizations = {
	        @Authorization(value = "BasicAuth")    }, tags={ "data", })
	    @ApiResponses(value = { 
	        @ApiResponse(code = 200, message = "successful operation", response = Object.class, responseContainer = "List") })
	    @RequestMapping(value = "/data/sensors/{sensorId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    ResponseEntity<List<? extends Data>> getData(@ApiParam(value = "Retrieve the sensor, which has the given id.",required=true) @PathVariable("sensorId") Long sensorId
	,@ApiParam(value = "Select data since the given value (inclusive), in ISO8601 format.") @Valid @RequestParam(value = "from", required = false) String from
	,@ApiParam(value = "Select data until the given value (inclusive), in ISO8601 format.") @Valid @RequestParam(value = "to", required = false) String to
	,@ApiParam(value = "List of parameters for back-end adapter, separated by comma.") @Valid @RequestParam(value = "params", required = false) List<String> params
	);
}
