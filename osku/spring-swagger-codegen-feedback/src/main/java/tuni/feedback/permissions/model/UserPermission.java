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
package tuni.feedback.permissions.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.EnumType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import tuni.feedback.permissions.Definitions;

/**
 * 
 * 
 */
@MappedSuperclass
public abstract class UserPermission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = Definitions.COLUMN_RELATION_ID)
	private Long id = null;
	@Enumerated(EnumType.STRING)
	@Column(name = Definitions.COLUMN_PERMISSION)
	private Permission permission = null;
	@Column(name = Definitions.COLUMN_PERMISSION_ID)
	private Long permissionId = null;
	@Column(name = Definitions.COLUMN_USER_ID)
	private Long userId = null;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_CREATED)
	private Date rowCreated = null;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_UPDATED)
	private Date rowUpdated = null;
	
	/**
	 * permission level
	 * 
	 */
	public enum Permission {
		/** no permissions */
		NONE(0),
		/** read */
		READ_ONLY(1),
		/** read + submit new answers */
		SUBMIT(2),
		/** read & submit + modify&read everything */
		FULL(3);
		
		private int _value;
		
		/**
		 * 
		 * @param value
		 */
		private Permission(int value) {
			_value = value;
		}
		
		/**
		 * 
		 * @return value
		 */
		public int getValue() {
			return _value;
		}
		
		/**
		 * 
		 * @param required
		 * @param given
		 * @return true if the given permission includes the required permission
		 */
		public static boolean permissionIncludes(Permission required, Permission given) {
			return (required.getValue() <= given.getValue());
		}
	} // enum Permission

	/**
	 * @return the permissionId
	 */
	public Long getPermissionId() {
		return permissionId;
	}

	/**
	 * @param permissionId the permissionId to set
	 */
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the permission
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	/**
	 * 
	 */
	public UserPermission() {
		super();
	}

	/**
	 * 
	 * @param permissionId
	 * @param userId
	 */
	public UserPermission(Long permissionId, Long userId) {
		this.permissionId = permissionId;
		this.userId = userId;
	}
}
