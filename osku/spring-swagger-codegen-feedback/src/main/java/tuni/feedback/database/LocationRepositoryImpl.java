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
package tuni.feedback.database;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import tuni.feedback.model.Definitions;
import tuni.feedback.model.Location;

/**
 * 
 * 
 */
public class LocationRepositoryImpl implements LocationRepositoryExtensions {
	private static final char ALIAS_ENTITY = 'l';
	private static final String JPQL_PARAM = "param";
	private static final String JPQL_FIND_ALL = "select "+ALIAS_ENTITY+" from "+Definitions.ENTITY_LOCATION+" "+ALIAS_ENTITY+" order by "+Definitions.COLUMN_LOCATION_ID+" DESC";
	private static final String JPQL_FIND_ALL_BY_LOCATION_ID = "select "+ALIAS_ENTITY+" from "+Definitions.ENTITY_LOCATION+" "+ALIAS_ENTITY+" where "+Definitions.COLUMN_LOCATION_ID+" IN :"+JPQL_PARAM+" order by "+Definitions.COLUMN_LOCATION_ID+" DESC";
	@PersistenceContext
    private EntityManager _entityManager = null;

	@Override
	public List<Location> findAllByLocationId(List<Long> locationId, Integer maxResults, Integer startPage) {
		Query q = null;
		if(locationId == null || locationId.isEmpty()) {
			q = _entityManager.createQuery(JPQL_FIND_ALL);
		}else {
			q = _entityManager.createQuery(JPQL_FIND_ALL_BY_LOCATION_ID);
			q.setParameter(JPQL_PARAM, locationId);
		}
		if(maxResults != null) {
			q.setMaxResults(maxResults);
		}
		if(startPage != null && startPage != 0) {
			q.setFirstResult(startPage*maxResults);
		}
		return q.getResultList();
	}
}
