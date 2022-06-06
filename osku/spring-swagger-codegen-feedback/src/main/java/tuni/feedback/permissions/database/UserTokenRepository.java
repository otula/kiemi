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
package tuni.feedback.permissions.database;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tuni.feedback.model.Definitions;
import tuni.feedback.model.UserToken;

/**
 * 
 * 
 */
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
	/**
	 * 
	 * @param token
	 * @return the matching token
	 */
	@Query("select t from "+Definitions.ENTITY_USER_TOKEN+" t where "+Definitions.COLUMN_TOKEN+"=?1")
	UserToken findTokenByToken(String token);
}
