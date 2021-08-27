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
/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.8).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package tuni.lorawan.sensors.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import tuni.lorawan.sensors.model.Data;

/**
 * 
 *
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-03T11:34:38.399859+03:00[Europe/Helsinki]")
@Api(value = "data")
public interface DataApi {

	/**
	 * 
	 * @param from
	 * @param to
	 * @param dataId
	 * @param nodeId
	 * @return response
	 */
	@ApiOperation(value = "Delete data", nickname = "deleteData", notes = "Delete existing data", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "data", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The data was deleted OK"), @ApiResponse(code = 400, message = "Malformed parameter(s) given"), @ApiResponse(code = 404, message = "Data was not found or permission was denied") })
	@RequestMapping(value = "/data", method = RequestMethod.DELETE)
	ResponseEntity<Void> deleteData(@NotNull @ApiParam(value = "RFC3339 start date (inclusive) for the data to delete", required = false) @Valid @RequestParam(value = "from", required = false) String from, @NotNull @ApiParam(value = "RFC3339 end date  (inclusive) for the data to delete", required = false) @Valid @RequestParam(value = "to", required = false) String to,
			@ApiParam(value = "Delete data, which have the given data id(s)") @Valid @RequestParam(value = "data_id", required = false) List<Long> dataId, @ApiParam(value = "Delete data produced by specific node(s)") @Valid @RequestParam(value = "node_id", required = false) List<String> nodeId);

	/**
	 * 
	 * @param dataId
	 * @param from
	 * @param to
	 * @param maxResults
	 * @param nodeId
	 * @return response
	 */
	@ApiOperation(value = "Return an array of data", nickname = "getData", notes = "Returns data based on the given parameters", response = Data.class, responseContainer = "List", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "data", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Data.class, responseContainer = "List") })
	@RequestMapping(value = "/data", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<List<Data>> getData(@ApiParam(value = "Return data, which have the given data id(s)") @Valid @RequestParam(value = "data_id", required = false) List<Long> dataId, @ApiParam(value = "RFC3339 start date (inclusive) for the data to retrieve") @Valid @RequestParam(value = "from", required = false) String from, @ApiParam(value = "RFC3339 end date  (inclusive) for the data to retrieve") @Valid @RequestParam(value = "to", required = false) String to,
			@ApiParam(value = "Return specified maximum number of results") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Return data produced by specific node(s)") @Valid @RequestParam(value = "node_id", required = false) List<UUID> nodeId);
}