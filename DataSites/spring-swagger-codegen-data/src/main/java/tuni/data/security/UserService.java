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
package tuni.data.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tuni.data.security.database.UserRepository;
import tuni.data.security.model.User;

/**
 * 
 * 
 */
@Service
public class UserService implements UserDetailsService {
	@Autowired
	private UserRepository _repository = null;
	
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		User u = _repository.findByUsername(username);
		if(u == null) {
			throw new UsernameNotFoundException("Invalid username: "+username);
		}else {
			return u;
		}
	}
}
