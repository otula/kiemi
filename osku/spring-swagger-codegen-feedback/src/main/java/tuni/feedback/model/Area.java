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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
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
 * Area
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_AREA)
@Table(name = Definitions.TABLE_AREA)
@JsonInclude(Include.NON_NULL)
public class Area {
	@Id
	@Column(name = Definitions.COLUMN_AREA_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.PROPERTY_NAME)
	private String name = null;
	@Column(name = Definitions.COLUMN_DESCRIPTION, length = Definitions.MAX_LENGTH_DESCRIPTION)
	@JsonProperty(Definitions.PROPERTY_DESCRIPTION)
	private String description = null;
	@OrderColumn
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = Definitions.COLUMN_AREA_ID)
	@JsonProperty(Definitions.PROPERTY_POLYGON)
	@Valid
	private List<Coordinate> polygon = null;
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
	public Area id(Long id) {
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
	 * @param name
	 * @return this
	 */
	public Area name(String name) {
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
	 * @param polygon
	 * @return this
	 */
	public Area polygon(List<Coordinate> polygon) {
		this.polygon = polygon;
		return this;
	}

	/**
	 * 
	 * @param polygonItem
	 * @return this
	 */
	public Area addPolygonItem(Coordinate polygonItem) {
		if (this.polygon == null) {
			this.polygon = new ArrayList<>();
		}
		this.polygon.add(polygonItem);
		return this;
	}

	/**
	 * Get polygon
	 * 
	 * @return polygon
	 **/
	@ApiModelProperty(value = "")
	@Valid
	public List<Coordinate> getPolygon() {
		return polygon;
	}

	/**
	 * 
	 * @param polygon
	 */
	public void setPolygon(List<Coordinate> polygon) {
		this.polygon = polygon;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Area description(String description) {
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
		Area area = (Area) o;
		return Objects.equals(this.id, area.id) && Objects.equals(this.name, area.name) && Objects.equals(this.polygon, area.polygon) && Objects.equals(this.description, area.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, polygon, description);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Area {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    polygon: ").append(toIndentedString(polygon)).append("\n");
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
