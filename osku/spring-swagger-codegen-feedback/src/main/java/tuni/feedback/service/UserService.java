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
package tuni.feedback.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.feedback.database.QuestionareRepository;
import tuni.feedback.exception.ForbiddenException;
import tuni.feedback.exception.IdNotFoundException;
import tuni.feedback.exception.InvalidParameterException;
import tuni.feedback.model.User;
import tuni.feedback.model.UserToken;
import tuni.feedback.permissions.service.PermissionService;

/**
 * user service
 * 
 */
@Service
public class UserService {
	private static final Logger LOGGER = LogManager.getLogger(UserService.class);
	@Autowired
	private PermissionService _permissionService = null;
	@Autowired
	private QuestionareRepository _questionareRepository = null;
	@Autowired
	private tuni.feedback.security.UserService _userService = null;
	
	/**
	 * 
	 * @param userId
	 * @return user
	 * @throws ForbiddenException
	 * @throws IdNotFoundException
	 */
	public User getUser(Long userId) throws ForbiddenException, IdNotFoundException {
		if(!_permissionService.isAdmin() && !_permissionService.getAuthenticatedUserId().equals(userId)) {
			throw new ForbiddenException("Permission denied.");
		}
		tuni.feedback.security.model.User u = _userService.getUser(userId);
		if(u == null) {
			throw new IdNotFoundException("User does not exist, id: "+userId);
		}
		return convert(u);
	}
	
	/**
	 * 
	 * @param user
	 * @return the security user converted to user 
	 */
	private User convert(tuni.feedback.security.model.User user) {
		User u = new User();
		u.setId(user.getId());
		u.setUsername(user.getUsername());
		// do not set password to prevent accidental showing of password hashes
		return u;
	}
	
	/**
	 * 
	 * @return user
	 * @throws ForbiddenException if not authenticated
	 * @throws IllegalArgumentException 
	 */
	public User getAuthenticatedUser() throws ForbiddenException, IllegalArgumentException {
		Long userId = _permissionService.getAuthenticatedUserId();
		if(userId == null) {
			throw new ForbiddenException("Permission denied.");
		}
		tuni.feedback.security.model.User u = _userService.getUser(userId);
		if(u == null) { // this should not happen
			throw new IllegalArgumentException("The currently authenticated user, id: "+userId+", was not found in the database.");
		}
		return convert(u);
	}
	
	/**
	 * 
	 * @param userId
	 * @throws ForbiddenException
	 */
	public void deleteUser(Long userId) throws ForbiddenException {
		if(!_permissionService.isAdmin()) {
			Long authUserId = _permissionService.getAuthenticatedUserId();
			if(authUserId == null || !authUserId.equals(userId)) {
				throw new ForbiddenException("Permission denied.");
			}
		}
		
		_userService.deleteUser(userId);
	}

	/**
	 * 
	 * @param questionareId
	 * @return the created user token
	 * @throws InvalidParameterException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public UserToken createToken(List<Long> questionareId) throws InvalidParameterException, ForbiddenException {
		if(!_permissionService.isAdmin()) {
			throw new ForbiddenException("Permission denied.");
		}
		if(questionareId == null || questionareId.isEmpty() || !_questionareRepository.existById(questionareId)) {
			throw new InvalidParameterException("Invalid questionare id list.");
		}
		
		return _permissionService.createToken(questionareId);
	}

	/**
	 * 
	 * @param token
	 * @param user
	 * @return identifier for the created user or null
	 * @throws ForbiddenException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public Long createUser(String token, User user) throws ForbiddenException, InvalidParameterException {
		UserToken userToken = _permissionService.getToken(token);
		if(userToken == null) {
			LOGGER.debug("Attempted to use no-existing token: "+token);
			throw new ForbiddenException("Permission denied.");
		}
		
		String username = user.getUsername();
		if(StringUtils.isBlank(username) || username.length() != StringUtils.trim(username).length()) {
			throw new InvalidParameterException("Invalid username.");
		}
		String password = user.getPassword();
		if(StringUtils.isBlank(password) || password.length() != StringUtils.trim(password).length()) {
			throw new InvalidParameterException("Invalid password.");
		}
		
		List<Long> qid = userToken.getQuestionareId();
		if(!_questionareRepository.existById(qid)) {
			LOGGER.debug("Attempted to use token ("+token+") with non-existing questionare ids.");
			throw new ForbiddenException("Permission denied.");
		}
		
		tuni.feedback.security.model.User u = _userService.createUser(username, password);
		if(u == null) {
			throw new InvalidParameterException("Could not create new user with given username: "+username);
		}
		
		_permissionService.addQuestionarePermissions(u.getId(), qid); // this is a new user and the user id does not have any existing permissions, so no need to check for existing permissions
		return u.getId();
	}
}
