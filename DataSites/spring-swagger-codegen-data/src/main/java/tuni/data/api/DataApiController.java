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
package tuni.data.api;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.data.adapters.datatypes.Data;
import tuni.data.exception.InvalidParameterException;
import tuni.data.service.DataService;

/**
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-29T17:44:36.682034+03:00[Europe/Helsinki]")
@Controller
@Api(tags = { "data" })
public class DataApiController implements DataApi {
	private static final FastDateFormat DATETIME_FORMATTER = FastDateFormat.getInstance(Definitions.DATE_FORMAT);
	private static final Logger LOGGER = LogManager.getLogger(DataApiController.class);
	private static final char SEPARATOR_PARAMETER = '=';
	@Autowired
	private DataService _dataService = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public DataApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<List<? extends Data>> getData(@ApiParam(value = "Retrieve the sensor, which has the given id.", required = true) @PathVariable("sensorId") Long sensorId, @ApiParam(value = "Select data since the given value (inclusive), in ISO8601 format.") @Valid @RequestParam(value = "from", required = false) String from,
			@ApiParam(value = "Select data until the given value (inclusive), in ISO8601 format.") @Valid @RequestParam(value = "to", required = false) String to, @ApiParam(value = "List of parameters for back-end adapter, separated by comma.") @Valid @RequestParam(value = "params", required = false) List<String> params) throws InvalidParameterException {
		Date fromDate = null;
		Date toDate = null;
		if (!StringUtils.isBlank(from)) {
			try {
				fromDate = DATETIME_FORMATTER.parse(from);
			} catch (ParseException ex) {
				LOGGER.debug(ex.getMessage(), ex);
				throw new InvalidParameterException(ex.getMessage());
			}
		}
		if (!StringUtils.isBlank(to)) {
			try {
				toDate = DATETIME_FORMATTER.parse(to);
			} catch (ParseException ex) {
				LOGGER.debug(ex.getMessage(), ex);
				throw new InvalidParameterException(ex.getMessage());
			}
		}
		
		HashMap<String, String> paramMap = null;
		if(params != null && !params.isEmpty()) {
			paramMap = new HashMap<>();
			for(String p : params) {
				String[] v = StringUtils.split(p, SEPARATOR_PARAMETER);
				if(v.length != 2) {
					throw new InvalidParameterException("Invalid parameter: "+p);
				}
				paramMap.put(v[0], v[1]);
			}
		}

		return new ResponseEntity<>(_dataService.getData(sensorId, fromDate, toDate, paramMap), HttpStatus.OK);
	}

}
