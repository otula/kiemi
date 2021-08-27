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

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.lorawan.sensors.model.Node;
import tuni.lorawan.sensors.service.NodeService;
import tuni.lorawan.sensors.utils.Utils;

/**
 * 
 *
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-06-28T16:14:09.575323+03:00[Europe/Helsinki]")
@Controller
@Api(tags = {"nodes"})
public class NodesApiController implements NodesApi {
	@Autowired
	private NodeService _service = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public NodesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	/**
	 * 
	 */
	@Override
	public ResponseEntity<Void> deleteNodes(@ApiParam(value = "Delete node(s) matching the give id(s)") @Valid @RequestParam(value = "node_id", required = false) List<UUID> nodeId) {
		if (_service.deleteNodes(Utils.toStringList(nodeId))) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<List<Node>> getNodes(@ApiParam(value = "Return nodes matching the given node id") @Valid @RequestParam(value = "node_id", required = false) List<UUID> nodeId) {
		return new ResponseEntity<>(_service.getNodes(Utils.toStringList(nodeId)), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Node> postNode(@ApiParam(value = "The node to add as an HTTP body", required = true) @Valid @RequestBody Node body) {
		Node node = _service.saveNode(body);
		if (node == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(node, HttpStatus.OK);
		}
	}
}
