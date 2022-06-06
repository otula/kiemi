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
package tuni.feedback.security;

import java.util.Arrays;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.feedback.security.database.UserRepository;
import tuni.feedback.security.model.Definitions;
import tuni.feedback.security.model.User;
import tuni.feedback.security.model.UserAuthority;

/**
 * 
 * 
 */
@Service
public class UserService implements UserDetailsService {
	private static final Logger LOGGER = LogManager.getLogger(UserService.class);
	@Autowired
	private UserRepository _repository = null;
	private BCryptPasswordEncoder _encoder = new BCryptPasswordEncoder();
	
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		User u = _repository.findByUsername(username);
		if(u == null) {
			throw new UsernameNotFoundException("Invalid username: "+username);
		}else {
			return u;
		}
	}
	
	/**
	 * Create a new user using the given credentials
	 * 
	 * @param username
	 * @param password
	 * @return the created user or null if user could not be created (i.e. username was in use)
	 */
	@Transactional
	public User createUser(String username, String password) {
		User u = _repository.findByUsername(username);
		if(u != null) {
			LOGGER.debug("Username already exists: "+username);
			return null;
		}
		
		u = new User();
		u.setUsername(username);
		u.setPassword(_encoder.encode(password));
		u.setAuthorities(Arrays.asList(UserAuthority.getUserAuthority(Definitions.ROLE_USER)));
		u.setAccountNonExpired(true);
		u.setAccountNonLocked(true);
		u.setCredentialsNonExpired(true);
		u.setEnabled(true);
		u = _repository.save(u);
		return u;
	}
	
	/**
	 * 
	 * @param userId
	 */
	public void deleteUser(Long userId) {
		_repository.deleteById(userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return user or null if not found
	 */
	public User getUser(Long userId) {
		Optional<User> user = _repository.findById(userId);
		return (user.isEmpty() ? null : user.get());
	}
	
	/**
	 * @return the encoder
	 */
	protected BCryptPasswordEncoder getEncoder() {
		return _encoder;
	}
}
