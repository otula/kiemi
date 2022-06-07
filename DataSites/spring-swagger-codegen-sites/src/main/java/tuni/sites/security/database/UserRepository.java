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
package tuni.sites.security.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import tuni.sites.security.model.Definitions;
import tuni.sites.security.model.User;

/**
 * 
 * 
 */
public interface UserRepository extends CrudRepository<User, Long> {
	/**
	 * 
	 * @param username
	 * @return user or null
	 */
	@Query("select u from "+Definitions.ENTITY_USER+" u where "+Definitions.COLUMN_USERNAME+"=?1")
	User findByUsername(String username);
}
