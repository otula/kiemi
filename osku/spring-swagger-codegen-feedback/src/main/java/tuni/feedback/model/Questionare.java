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
package tuni.feedback.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

/**
 * Questionare
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_QUESTIONARE) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_QUESTIONARE)
@JsonInclude(Include.NON_NULL)
public class Questionare {
	@Id
	@Column(name = Definitions.COLUMN_QUESTIONARE_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.PROPERTY_NAME)
	private String name = null;
	@Column(name = Definitions.COLUMN_DESCRIPTION, length = Definitions.MAX_LENGTH_DESCRIPTION)
	@JsonProperty(Definitions.PROPERTY_DESCRIPTION)
	private String description = null;
	@Column(name = Definitions.COLUMN_LOCATION_ID)
	@JsonProperty(Definitions.PROPERTY_LOCATION_ID)
	private Long locationId = null;
	@Column(name = Definitions.COLUMN_USE_SERVICE_USERS)
	@JsonProperty(Definitions.PROPERTY_USE_SERVICE_USERS)
	private Boolean useServiceUsers = null;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_CREATED)
	private Date rowCreated = null;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_UPDATED)
	private Date rowUpdated = null;

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public Questionare id(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@ApiModelProperty(value = "")
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @param useServiceUsers
	 * @return this
	 */
	public Questionare useServiceUsers(Boolean useServiceUsers) {
		this.useServiceUsers = useServiceUsers;
		return this;
	}

	/**
	 * Setting this to false will enable the use and creation of anonymous users and
	 * \"nicknames\" and disables storing of authenticated user's identifier to
	 * answers. Setting this value to true will disable anonymous users and
	 * \"nicknamed\" (user) lists and answers will be linked to the authenticated
	 * user. Note that this value cannot be changed after the questionare has been
	 * created.
	 * 
	 * @return useServiceUsers
	 **/
	@ApiModelProperty(required = true, value = "Setting this to false will enable the use and creation of anonymous users and \"nicknames\" and disables storing of authenticated user's identifier to answers. Setting this value to true will disable anonymous users and \"nicknamed\" (user) lists and answers will be linked to the authenticated user. Note that this value cannot be changed after the questionare has been created.")
	@NotNull
	public Boolean isUseServiceUsers() {
		return useServiceUsers;
	}

	/**
	 * 
	 * @param useServiceUsers
	 */
	public void setUseServiceUsers(Boolean useServiceUsers) {
		this.useServiceUsers = useServiceUsers;
	}

	/**
	 * 
	 * @param name
	 * @return this
	 */
	public Questionare name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Get name
	 * 
	 * @return name
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	@Size(max=Definitions.MAX_LENGTH_NAME)
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Questionare description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Get description
	 * 
	 * @return description
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_DESCRIPTION)
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @param locationId
	 * @return this
	 */
	public Questionare locationId(Long locationId) {
		this.locationId = locationId;
		return this;
	}

	/**
	 * Get locationId
	 * 
	 * @return locationId
	 **/
	@ApiModelProperty(value = "")
	public Long getLocationId() {
		return locationId;
	}

	/**
	 * 
	 * @param locationId
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Questionare questionare = (Questionare) o;
		return Objects.equals(this.id, questionare.id) && Objects.equals(this.name, questionare.name) && Objects.equals(this.description, questionare.description) && Objects.equals(this.locationId, questionare.locationId) && Objects.equals(this.useServiceUsers, questionare.useServiceUsers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description, locationId, useServiceUsers);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Questionare {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    locationId: ").append(toIndentedString(locationId)).append("\n");
		sb.append("    useServiceUsers: ").append(toIndentedString(useServiceUsers)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
