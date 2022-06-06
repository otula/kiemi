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
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
import tuni.feedback.model.converters.TimeConverter;

/**
 * TimeSelection
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_TIME_SELECTION) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_TIME_SELECTION)
@JsonInclude(Include.NON_NULL)
public class TimeSelection {
	@Id
	@Column(name = Definitions.COLUMN_TIME_SELECTION_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.PROPERTY_NAME)
	private String name = null;
	@Convert(converter = TimeConverter.class)
	@Column(name = Definitions.COLUMN_START)
	@JsonProperty(Definitions.PROPERTY_START)
	private String start = null;
	@Convert(converter = TimeConverter.class)
	@Column(name = Definitions.COLUMN_END)
	@JsonProperty(Definitions.PROPERTY_END)
	private String end = null;
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
	public TimeSelection id(Long id) {
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
	public TimeSelection name(String name) {
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
	 * @param start
	 * @return this
	 */
	public TimeSelection start(String start) {
		this.start = start;
		return this;
	}

	/**
	 * Get start
	 * 
	 * @return start
	 **/
	@ApiModelProperty(required = true, value = "UTC time in 00:00:00Z (HOURS:MINUTES:SECONDSZ) format.")
	@NotNull
	@Valid
	public String getStart() {
		return start;
	}

	/**
	 * 
	 * @param start
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * 
	 * @param end
	 * @return this
	 */
	public TimeSelection end(String end) {
		this.end = end;
		return this;
	}

	/**
	 * Get end
	 * 
	 * @return end
	 **/
	@ApiModelProperty(required = true, value = "UTC time in 00:00:00Z (HOURS:MINUTES:SECONDSZ) format.")
	@NotNull
	@Valid
	public String getEnd() {
		return end;
	}

	/**
	 * 
	 * @param end
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TimeSelection timeSelection = (TimeSelection) o;
		return Objects.equals(this.id, timeSelection.id) && Objects.equals(this.name, timeSelection.name) && Objects.equals(this.start, timeSelection.start) && Objects.equals(this.end, timeSelection.end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, start, end);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class TimeSelection {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    start: ").append(toIndentedString(start)).append("\n");
		sb.append("    end: ").append(toIndentedString(end)).append("\n");
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
