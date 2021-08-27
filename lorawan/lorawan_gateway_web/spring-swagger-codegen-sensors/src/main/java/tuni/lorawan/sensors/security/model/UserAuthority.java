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
package tuni.lorawan.sensors.security.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * 
 */
public class UserAuthority implements GrantedAuthority {
	/** serial UID */
	private static final long serialVersionUID = 8806228217032055228L;
	private String authority = null;
	
	@Override
	public String getAuthority() {
		return authority;
	}
	
	/**
	 * 
	 * @param role
	 * @return new user authority with the given role
	 * @throws IllegalArgumentException 
	 */
	public static UserAuthority getUserAuthority(String role) throws IllegalArgumentException {
		if(!Definitions.ROLE_USER.equals(role)) {
			throw new IllegalArgumentException("Invalid role: "+role);
		}
		UserAuthority ua = new UserAuthority();
		ua.authority = role;
		return ua;
	}
}
