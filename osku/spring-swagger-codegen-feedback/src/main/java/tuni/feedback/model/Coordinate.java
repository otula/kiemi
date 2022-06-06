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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Coordinate
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_COORDINATE) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_COORDINATE)
public class Coordinate {
	@Id
	@Column(name = Definitions.COLUMN_COORDINATE_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;
	@Column(name = Definitions.COLUMN_X)
	@JsonProperty(Definitions.PROPERTY_X)
	private Integer x = null;
	@Column(name = Definitions.COLUMN_Y)
	@JsonProperty(Definitions.PROPERTY_Y)
	private Integer y = null;
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
	 * @param x
	 * @return this
	 */
	public Coordinate x(Integer x) {
		this.x = x;
		return this;
	}

	/**
	 * Get x
	 * 
	 * @return x
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public Integer getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * 
	 * @param y
	 * @return this
	 */
	public Coordinate y(Integer y) {
		this.y = y;
		return this;
	}

	/**
	 * Get y
	 * 
	 * @return y
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public Integer getY() {
		return y;
	}

	/**
	 * 
	 * @param y
	 */
	public void setY(Integer y) {
		this.y = y;
	}

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public Coordinate id(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Coordinate {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    x: ").append(toIndentedString(x)).append("\n");
		sb.append("    y: ").append(toIndentedString(y)).append("\n");
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
