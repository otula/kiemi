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
package tuni.feedback.relations.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import tuni.feedback.relations.Definitions;

/**
 * 
 * 
 */
@MappedSuperclass
public abstract class ParentToChild {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = tuni.feedback.model.Definitions.COLUMN_RELATION_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_CHILD_ID)
	private Long childId = null;
	@Column(name = Definitions.COLUMN_PARENT_ID)
	private Long parentId = null;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_CREATED)
	private Date rowCreated = null;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_UPDATED)
	private Date rowUpdated = null;

	/**
	 * @return the childId
	 */
	public Long getChildId() {
		return childId;
	}

	/**
	 * @param childId the childId to set
	 */
	public void setChildId(Long childId) {
		this.childId = childId;
	}

	/**
	 * @return the parentId
	 */
	public Long getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * 
	 */
	public ParentToChild() {
		// nothing needed
	}

	/**
	 * 
	 * @param childId
	 * @param parentId
	 */
	public ParentToChild(Long childId, Long parentId) {
		this.childId = childId;
		this.parentId = parentId;
	}
}
