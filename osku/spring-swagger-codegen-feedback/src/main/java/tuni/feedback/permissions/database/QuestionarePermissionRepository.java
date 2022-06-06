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


import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tuni.feedback.permissions.Definitions;
import tuni.feedback.permissions.model.QuestionarePermission;

/**
 * 
 * 
 */
public interface QuestionarePermissionRepository extends JpaRepository<QuestionarePermission, Long> {
	/**
	 * 
	 * @param userId
	 * @param questionareId
	 * @return number of matching user id - question id pairs
	 */
	@Query("select p from "+Definitions.ENTITY_QUESTIONARE_PERMISSION+" p where "+Definitions.COLUMN_USER_ID+"=?1 and "+Definitions.COLUMN_PERMISSION_ID+" IN(?2)")
	List<QuestionarePermission> getPermissions(Long userId, Collection<Long> questionareId);
	
	/**
	 * 
	 * @param userId
	 * @param questionareId
	 * @return 1 if the permission exists, 0 if not
	 */
	@Query("select p.permissionId from "+Definitions.ENTITY_QUESTIONARE_PERMISSION+" p where "+Definitions.COLUMN_USER_ID+"=?1")
	List<Long> findAllQuestionareIdbyUserId(Long userId);
}
