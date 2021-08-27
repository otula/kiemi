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
package tuni.lorawan.sensors.api;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.lorawan.sensors.model.Data;
import tuni.lorawan.sensors.service.DataService;
import tuni.lorawan.sensors.utils.Utils;

/**
 * 
 *
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-06-28T16:14:09.575323+03:00[Europe/Helsinki]")
@Controller
@Api(tags = {"data"})
public class DataApiController implements DataApi {
	private static final FastDateFormat DATETIME_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssX");
	private static final Logger LOG = LoggerFactory.getLogger(DataApiController.class);
    @Autowired
    private DataService _service = null;

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
    public ResponseEntity<Void> deleteData(@NotNull @ApiParam(value = "RFC3339 start date (inclusive) for the data to delete", required = false) @Valid @RequestParam(value = "from", required = false) String from,@NotNull @ApiParam(value = "RFC3339 end date  (inclusive) for the data to delete", required = false) @Valid @RequestParam(value = "to", required = false) String to,@ApiParam(value = "Delete data, which have the given data id(s)") @Valid @RequestParam(value = "data_id", required = false) List<Long> dataId,@ApiParam(value = "Delete data produced by specific node(s)") @Valid @RequestParam(value = "node_id", required = false) List<String> nodeId) {
    	Date start = null;
		Date end = null;
    	if(!StringUtils.isBlank(from)) {
    		try {
    			start = DATETIME_FORMATTER.parse(from);
    		} catch (ParseException ex) {
    			LOG.warn("Failed to parse from date.", ex);
    			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    		}
    	}
    	if(!StringUtils.isBlank(to)) {
    		try {
    			end = DATETIME_FORMATTER.parse(to);
    		} catch (ParseException ex) {
    			LOG.warn("Failed to parse to date.", ex);
    			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    		}
    	}
    	if(_service.deleteData(start, end, dataId, nodeId)) {
    		return new ResponseEntity<>(HttpStatus.OK);
    	}else {
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    }

    @Override
    public ResponseEntity<List<Data>> getData(@ApiParam(value = "Return data, which have the given data id(s)") @Valid @RequestParam(value = "data_id", required = false) List<Long> dataId,@ApiParam(value = "RFC3339 start date (inclusive) for the data to retrieve") @Valid @RequestParam(value = "from", required = false) String from,@ApiParam(value = "RFC3339 end date  (inclusive) for the data to retrieve") @Valid @RequestParam(value = "to", required = false) String to,@ApiParam(value = "Return specified maximum number of results") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults,@ApiParam(value = "Return data produced by specific node(s)") @Valid @RequestParam(value = "node_id", required = false) List<UUID> nodeId) {
    	Date start = null;
		Date end = null;
    	if(!StringUtils.isBlank(from)) {
    		try {
    			start = DATETIME_FORMATTER.parse(from);
    		} catch (ParseException ex) {
    			LOG.warn("Failed to parse from date.", ex);
    			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    		}
    	}
    	if(!StringUtils.isBlank(to)) {
    		try {
    			end = DATETIME_FORMATTER.parse(to);
    		} catch (ParseException ex) {
    			LOG.warn("Failed to parse to date.", ex);
    			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    		}
    	}
    	return new ResponseEntity<>(_service.getData(start, end, dataId, Utils.toStringList(nodeId), maxResults), HttpStatus.OK);
    }
}
