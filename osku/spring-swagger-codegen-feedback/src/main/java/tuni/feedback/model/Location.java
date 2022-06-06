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
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

/**
 * Location
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_LOCATION) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_LOCATION)
@JsonInclude(Include.NON_NULL)
public class Location {
	@Id
	@Column(name = Definitions.COLUMN_LOCATION_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_AREA_IMAGE_URL, length = Definitions.MAX_LENGTH_URL)
	@JsonProperty(Definitions.PROPERTY_AREA_IMAGE_URL)
	private String areaImageUrl = null;
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.PROPERTY_NAME)
	private String name = null;
	@Column(name = Definitions.COLUMN_DESCRIPTION, length = Definitions.MAX_LENGTH_DESCRIPTION)
	@JsonProperty(Definitions.PROPERTY_DESCRIPTION)
	private String description = null;
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
	 * @param name
	 * @return this
	 */
	public Location name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Get name
	 * 
	 * @return name
	 **/
	@ApiModelProperty(value = "")
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
	 * @param id
	 * @return this
	 */
	public Location id(Long id) {
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
	 * @param areaImageUrl
	 * @return this
	 */
	public Location areaImageUrl(String areaImageUrl) {
		this.areaImageUrl = areaImageUrl;
		return this;
	}

	/**
	 * Get areaImageUrl
	 * 
	 * @return areaImageUrl
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_URL)
	public String getAreaImageUrl() {
		return areaImageUrl;
	}

	/**
	 * 
	 * @param areaImageUrl
	 */
	public void setAreaImageUrl(String areaImageUrl) {
		this.areaImageUrl = areaImageUrl;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Location description(String description) {
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

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Location location = (Location) o;
		return Objects.equals(this.id, location.id) && Objects.equals(this.areaImageUrl, location.areaImageUrl) && Objects.equals(this.name, location.name) && Objects.equals(this.description, location.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, areaImageUrl, name, description);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Location {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    areaImageUrl: ").append(toIndentedString(areaImageUrl)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
