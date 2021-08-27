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
package tuni.lorawan.sensors.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.lorawan.sensors.database.DataRepository;
import tuni.lorawan.sensors.database.NodeRepository;
import tuni.lorawan.sensors.model.Node;

/**
 * 
 * 
 */
@Service
public class NodeService {
	private static final Logger LOG = LoggerFactory.getLogger(NodeService.class);
	@Autowired
	private DataRepository _dataRepository = null;
	@Autowired
	private NodeRepository _nodeRepository = null;

	/**
	 * 
	 * @param nodeId
	 * @return true on success
	 */
	@Transactional
	public boolean deleteNodes(List<String> nodeId) {
		_nodeRepository.deleteById(nodeId);
		_dataRepository.deleteByNodeId(nodeId);
		return true;
	}

	/**
	 * 
	 * @param nodeIds optional list of ids to use as a filter
	 * @return list of nodes or an empty list
	 */
	public List<Node> getNodes(List<String> nodeIds) {
		if(nodeIds == null || nodeIds.isEmpty()) {
			return _nodeRepository.findAll();
		}else {
			return _nodeRepository.findAllById(nodeIds);
		}
	}

	/**
	 * 
	 * @param node if the node contains an id, an existing node will be updated
	 * @return the node with a populated uuid
	 */
	public Node saveNode(Node node) {
		if(StringUtils.isBlank(node.getId())) {
			LOG.debug("No node id given, generating a new one.");
			node.setId(UUID.randomUUID().toString());
		}
		return _nodeRepository.save(node);
	}
}
