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
package tuni.feedback.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * 
 */
@Entity(name = Definitions.ENTITY_USER_AUTHORITY)
@Table(name = Definitions.TABLE_USER_AUTHORITIES)
public class UserAuthority implements GrantedAuthority {
	/** serial UID */
	private static final long serialVersionUID = 8806228217032055228L;
	@Column(name = Definitions.COLUMN_AUTHORITY)
	private String authority = null;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = Definitions.COLUMN_USER_AUTHORITY_ID)
	private Long id = null;
	
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
		if(!Definitions.ROLE_USER.equals(role) && !Definitions.ROLE_ADMIN.equals(role)) {
			throw new IllegalArgumentException("Invalid role: "+role);
		}
		UserAuthority ua = new UserAuthority();
		ua.authority = role;
		return ua;
	}
}
