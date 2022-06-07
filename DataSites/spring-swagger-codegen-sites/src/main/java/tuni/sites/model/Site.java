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
package tuni.sites.model;

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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Site
 */
@Validated
@Entity(name = Definitions.ENTITY_SITE) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_SITES)
@JsonInclude(Include.NON_NULL)
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
public class Site {
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.JSON_PROPERTY_NAME)
	private String name = null;
	@Column(name = Definitions.COLUMN_DESCRIPTION, length = Definitions.MAX_LENGTH_DESCRIPTION)
	@JsonProperty(Definitions.JSON_PROPERTY_DESCRIPTION)
	private String description = null;
	@Column(name = Definitions.COLUMN_EXTERNAL_URL, length = Definitions.MAX_LENGTH_URL)
	@JsonProperty(Definitions.JSON_PROPERTY_EXTERNAL_URL)
	private String externalUrl = null;
	@Id
	@Column(name = Definitions.COLUMN_ID)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonProperty(Definitions.JSON_PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_ORGANIZATION_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.JSON_PROPERTY_ORGANIZATION_NAME)
	private String organizationName = null;
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
	public Site name(String name) {
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
	 * @param externalUrl
	 * @return this
	 */
	public Site externalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
		return this;
	}

	/**
	 * External web link.
	 * 
	 * @return externalUrl
	 **/
	@ApiModelProperty(value = "External web link.")
	@Size(max=Definitions.MAX_LENGTH_URL)
	public String getExternalUrl() {
		return externalUrl;
	}

	/**
	 * 
	 * @param externalUrl
	 */
	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Site description(String description) {
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
	 * @param id
	 * @return this
	 */
	public Site id(Long id) {
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
	 * @param organizationName
	 * @return this
	 */
	public Site organizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	/**
	 * Organization name for this site.
	 * 
	 * @return organizationName
	 **/
	@ApiModelProperty(value = "Organization name for this site.")
	@Size(max=Definitions.MAX_LENGTH_NAME)
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * 
	 * @param organizationName
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Site site = (Site) o;
		return Objects.equals(this.name, site.name) && Objects.equals(this.description, site.description) && Objects.equals(this.id, site.id) && Objects.equals(this.externalUrl, site.externalUrl) && Objects.equals(this.organizationName, site.organizationName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, id, externalUrl, organizationName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Site {\n");

		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    externalUrl: ").append(toIndentedString(externalUrl)).append("\n");
		sb.append("    organizationName: ").append(toIndentedString(organizationName)).append("\n");
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
