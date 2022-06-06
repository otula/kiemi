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
package tuni.feedback.permissions.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.feedback.database.QuestionareRepository;
import tuni.feedback.model.UserToken;
import tuni.feedback.permissions.Definitions;
import tuni.feedback.permissions.database.QuestionarePermissionRepository;
import tuni.feedback.permissions.database.UserTokenRepository;
import tuni.feedback.permissions.model.QuestionarePermission;
import tuni.feedback.permissions.model.UserPermission.Permission;
import tuni.feedback.security.model.User;

/**
 * location and questionare permission service
 * 
 */
@Service
public class PermissionService {
	private static final Logger LOGGER = LogManager.getLogger(PermissionService.class);
	@Value("${"+Definitions.PROPERTY_PERMISSION_BASEURI+"}")
	private String _linkBaseUri = null;
	@Value("${"+Definitions.PROPERTY_PERMISSION_TOKEN_LENGTH+"}")
	private int _tokenLength = tuni.feedback.security.model.Definitions.MAX_LENGTH_PASSWORD;
	@Autowired
	private QuestionarePermissionRepository _questionarePermissionRepository = null;
	@Autowired
	private QuestionareRepository _questionareRepository = null;
	@Autowired
	private UserTokenRepository _userTokenRepository = null;
	
	/**
	 * Note: if the currently authenticated user is an admin, this will return true. This does not guarantee that the questionare exists in the database.
	 * 
	 * @param questionareId
	 * @param permission
	 * @return true if currently logged in user has the given permission
	 */
	public boolean hasQuestionarePermission(Long questionareId, Permission permission) {
		User user = getAuthenticatedUser();
		if(isAdmin(user)) {
			LOGGER.debug("Admin user, not checking permissions.");
			return true;
		}
		return (user == null ? false : Permission.permissionIncludes(permission, getQuestionarePermission(user.getId(), questionareId)));
	}
	
	/**
	 * Note: if the currently authenticated user is an admin, this will return true. This does not guarantee that the location exists in the database.
	 * 
	 * @param locationId
	 * @param permission
	 * @return true if currently logged in user has the given permission
	 */
	public boolean hasLocationPermission(Long locationId, Permission permission) {
		User user = getAuthenticatedUser();
		if(isAdmin(user)) {
			LOGGER.debug("Admin user, not checking permissions.");
			return true;
		}
		return (user == null ? false : Permission.permissionIncludes(permission, getLocationPermission(user.getId(), locationId)));
	}
	
	/**
	 * Note: this will only make a simple table lookup, it will not check if the user is an admin.
	 * 
	 * @param userId
	 * @param locationId
	 * @return permission
	 */
	private Permission getLocationPermission(Long userId, Long locationId) {
		List<Long> ids = _questionareRepository.findAllQuestionareIdByLocationId(locationId);
		if(ids == null || ids.isEmpty()) {
			return Permission.NONE;
		}
		
		List<QuestionarePermission> p = _questionarePermissionRepository.getPermissions(userId, ids); // if at least one match is found, the user has access to the location based on questionare permissions
		if(p.size() < 1) {
			return Permission.NONE;
		}
		Iterator<QuestionarePermission> iter = p.iterator();
		Permission highest = iter.next().getPermission();
		while(iter.hasNext()) {
			Permission qp = iter.next().getPermission();
			if(qp.getValue() > highest.getValue()) {
				highest = qp;
			}
		}
		
		return highest;
	}
	
	/**
	 * 
	 * @return user id for the authenticated user or null if not authenticated
	 */
	private User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null) {
			LOGGER.debug("No active authentication.");
			return null;
		}
		
		Object p = authentication.getPrincipal();
		try {
			return (User) p;
		} catch (ClassCastException ex) {
			LOGGER.warn(ex.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * 
	 * @return id for the authenticated user or null if no authenticated user
	 */
	public Long getAuthenticatedUserId() {
		User u = getAuthenticatedUser();
		return (u == null ? null : u.getId());
	}
	
	/**
	 * Note: this will only make a simple table lookup, it will not check if the user is an admin.
	 * 
	 * @param userId
	 * @param questionareId
	 * @return permission
	 */
	private Permission getQuestionarePermission(Long userId, Long questionareId) {
		List<QuestionarePermission> p = _questionarePermissionRepository.getPermissions(userId, Arrays.asList(questionareId));
		return (p.size() < 1 ? Permission.NONE : p.get(0).getPermission());
	}
	
	/**
	 * 
	 * @return true if the currently authenticated user is an admin user
	 */
	public boolean isAdmin() {
		User user = getAuthenticatedUser();
		if(user == null) {
			return false;
		}
		return isAdmin(user);
	}
	
	/**
	 * @param user
	 * @return true if the currently authenticated user is an admin user
	 */
	private boolean isAdmin(User user) {
		for(GrantedAuthority a : user.getAuthorities()) {
			if(tuni.feedback.security.model.Definitions.ROLE_ADMIN.equals(a.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Note: this will only make a simple table lookup, it will not check if the user is an admin. use {@link #isAdmin()} to check if the user should have access to all questionares.
	 * 
	 * @return list of permitted questionare ids for the currently authenticated user or null if none
	 */
	public List<Long> getPermittedQuestionareIds(){
		User user = getAuthenticatedUser();
		if(user == null) {
			return null;
		}
		List<Long> ids = _questionarePermissionRepository.findAllQuestionareIdbyUserId(user.getId());
		return (ids.isEmpty() ? null : ids);
	}
	
	/**
	 * Note: this will not verify that the questionare ids exist.
	 * 
	 * @param questionareId
	 * @return the created user token
	 */
	@Transactional
	public UserToken createToken(List<Long> questionareId) {
		UserToken t = new UserToken();
		t.setQuestionareId(questionareId);
		String token = RandomStringUtils.randomAlphanumeric(_tokenLength);
		t.setToken(token);
		t = _userTokenRepository.save(t);
		t.link(_linkBaseUri+Definitions.PARAMETER_USER_TOKEN+token);
		return t;
	}
	
	/**
	 * 
	 * @param token
	 * @return token or null if none was found
	 */
	public UserToken getToken(String token) {
		return _userTokenRepository.findTokenByToken(token);
	}
	
	/**
	 * Note: this will not check for existing (duplicate) permissions, an attempt to insert duplicates may result in (database) exception to be thrown
	 * 
	 * Note: this will not check that the user or the questionare id exists.
	 * 
	 * @param userId
	 * @param questionareId
	 */
	@Transactional
	public void addQuestionarePermissions(Long userId, List<Long> questionareId) {
		ArrayList<QuestionarePermission> a = new ArrayList<>(questionareId.size());
		for(Long qid : questionareId) {
			boolean found = false;
			for(QuestionarePermission inserted : a) { // check that the given id list does not contain duplicates
				if(inserted.getPermissionId().equals(qid)) {
					found = true;
					break;
				}
			}
			if(!found) {
				QuestionarePermission qp = new QuestionarePermission();
				qp.setPermissionId(qid);
				qp.setUserId(userId);
				qp.setPermission(Permission.SUBMIT); // add new users with submit permissions
				a.add(qp);
			}
		}
		_questionarePermissionRepository.saveAll(a);
	}
}
