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
package tuni.lorawan.sensors.database;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import tuni.lorawan.sensors.model.Data;
import tuni.lorawan.sensors.model.Definitions;

/**
 * 
 * 
 */
public class DataRepositoryImpl implements DataRepositoryExtensions {
	private static final String JPQL_AND = " and ";
	private static final String JPQL_DELETE = "delete from "+Definitions.ENTITY_DATA+" d";
	private static final String JPQL_GREATER_OR_EQUAL = ">=?";
	private static final String JPQL_IN = " IN ?";
	private static final String JPQL_LESS_OR_EQUAL = "<=?";
	private static final String JPQL_ORDER_BY = " order by "+Definitions.COLUMN_DATA_ID+" ASC";
	private static final String JPQL_SELECT = "select d from "+Definitions.ENTITY_DATA+" d";
	private static final String JPQL_WHERE = " where ";
	@PersistenceContext
    private EntityManager _entityManager = null;

	@Override
	public void delete(Date from, Date to, List<Long> dataId, List<String> nodeId) {
		StringBuilder sb = new StringBuilder(JPQL_DELETE);
		appendWhere(sb, from, to, dataId, nodeId);
		int paramIndex = 1;
		Query q = _entityManager.createQuery(sb.toString());
		if(from != null) {
			q.setParameter(paramIndex++, from.getTime());
		}
		if(to != null) {
			q.setParameter(paramIndex++, to.getTime());
		}
		if(dataId != null && !dataId.isEmpty()) {
			q.setParameter(paramIndex++, dataId);
		}
		if(nodeId != null && !nodeId.isEmpty()) {
			q.setParameter(paramIndex, nodeId);
		}
		q.executeUpdate();
	}

	@Override
	public List<Data> findAll(Date from, Date to, List<Long> dataId, List<String> nodeId, Integer maxResults) {
		StringBuilder sb = new StringBuilder(JPQL_SELECT);
		appendWhere(sb, from, to, dataId, nodeId);
		
		sb.append(JPQL_ORDER_BY);
		
		int paramIndex = 1;
		Query q = _entityManager.createQuery(sb.toString());
		if(from != null) {
			q.setParameter(paramIndex++, from);
		}
		if(to != null) {
			q.setParameter(paramIndex++, to);
		}
		if(dataId != null && !dataId.isEmpty()) {
			q.setParameter(paramIndex++, dataId);
		}
		if(nodeId != null && !nodeId.isEmpty()) {
			q.setParameter(paramIndex, nodeId);
		}
		if(maxResults != null) {
			q.setMaxResults(maxResults);
		}
		
		return q.getResultList();
	}

	/**
	 * A helper function for creating the a where clause. 
	 * 
	 * The parameters are added in numerical order (1-5), skipping null or empty function arguments.
	 * 
	 * @param sb
	 * @param from
	 * @param to
	 * @param dataId
	 * @param nodeId
	 */
	private void appendWhere(StringBuilder sb, Date from, Date to, List<Long> dataId, List<String> nodeId) {
		sb.append(JPQL_WHERE);
		int paramIndex = 1;
		if(from != null) {
			sb.append(Definitions.COLUMN_TIMESTAMP);
			sb.append(JPQL_GREATER_OR_EQUAL);
			sb.append(paramIndex++);
		}
		if(to != null) {
			if(paramIndex > 1) {
				sb.append(JPQL_AND);
			}
			sb.append(Definitions.COLUMN_TIMESTAMP);
			sb.append(JPQL_LESS_OR_EQUAL);
			sb.append(paramIndex++);
		}
		if(dataId != null && !dataId.isEmpty()) {
			if(paramIndex > 1) {
				sb.append(JPQL_AND);
			}
			sb.append(Definitions.COLUMN_DATA_ID);
			sb.append(JPQL_IN);
			sb.append(paramIndex++);
		}
		if(nodeId != null && !nodeId.isEmpty()) {
			if(paramIndex > 1) {
				sb.append(JPQL_AND);
			}
			sb.append(Definitions.COLUMN_NODE_ID);
			sb.append(JPQL_IN);
			sb.append(paramIndex);
		}
	}
}
