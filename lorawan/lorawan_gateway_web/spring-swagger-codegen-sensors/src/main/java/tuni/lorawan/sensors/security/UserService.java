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
package tuni.lorawan.sensors.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import tuni.lorawan.sensors.security.model.Definitions;
import tuni.lorawan.sensors.security.model.User;
import tuni.lorawan.sensors.security.model.UserAuthority;

/**
 * 
 * 
 */
@Service
public class UserService implements UserDetailsService {
	@Autowired // simply autowire a single user defined in the application.properties
	private ConfiguredUser _user = null;

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		if(_user.getUsername().equals(username)) { // use the defined user, we could also load user details from database, if needed
			return _user;
		}else {
			throw new UsernameNotFoundException("Invalid username: "+username);
		}
	}
	
	/**
	 * Simple class that reads username / password from application.properties:
	 * 
	 *  tuni.lorawan.sensors.username
	 *  tuni.lorawan.sensors.password
	 * 
	 */
	@Component
	private class ConfiguredUser extends User {
		/** serial UUID */
		private static final long serialVersionUID = -5608483995331686556L;
		private List<UserAuthority> _authorities = Arrays.asList(UserAuthority.getUserAuthority(Definitions.ROLE_USER));
		@Value("${tuni.lorawan.sensors.password}")
		private String _password = null;
		@Value("${tuni.lorawan.sensors.username}")
		private String _username = null;
		
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return _authorities;
		}
		@Override
		public String getPassword() {
			return _password;
		}
		@Override
		public String getUsername() {
			return _username;
		}
	} // class ConfiguredUser
}
