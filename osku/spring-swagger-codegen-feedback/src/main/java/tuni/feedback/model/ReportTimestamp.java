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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;
import tuni.feedback.model.converters.DateConverter;

/**
 * ReportTimestamp
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_REPORT_TIMESTAMP) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_REPORT_TIMESTAMP)
@JsonInclude(Include.NON_NULL)
public class ReportTimestamp {
	@Id
	@Column(name = Definitions.COLUMN_REPORT_TIMESTAMP_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;
	@Convert(converter = DateConverter.class)
	@Column(name = Definitions.COLUMN_START)
	@JsonProperty(Definitions.PROPERTY_START)
	private Date start = null;
	@Column(name = Definitions.COLUMN_START_UTC_SECONDS)
	private Long startUtcSeconds = null;
	@Convert(converter = DateConverter.class)
	@Column(name = Definitions.COLUMN_END)
	@JsonProperty(Definitions.PROPERTY_END)
	private Date end = null;
	@Column(name = Definitions.COLUMN_END_UTC_SECONDS)
	private Long endUtcSeconds = null;
	@Column(name = Definitions.COLUMN_CONTINUOUS)
	@JsonProperty(Definitions.PROPERTY_CONTINUOUS)
	private Boolean continuous = null;
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
	 * @param start
	 * @return this
	 */
	public ReportTimestamp start(Date start) {
		this.start = start;
		return this;
	}

	/**
	 * Get start
	 * 
	 * @return start
	 **/
	@ApiModelProperty(required = true, value = "Start time for the report in ISO8601 format")
	@Valid
	@NotNull
	public Date getStart() {
		return start;
	}

	/**
	 * 
	 * @param start
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * 
	 * @param end
	 * @return this
	 */
	public ReportTimestamp end(Date end) {
		this.end = end;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@Valid
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
	 * @param id
	 * @return this
	 */
	public ReportTimestamp end(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get end
	 * 
	 * @return end
	 **/
	@ApiModelProperty(required = true, value = "End time for the report in ISO8601 format")
	@Valid
	@NotNull
	public Date getEnd() {
		return end;
	}

	/**
	 * 
	 * @param end
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * 
	 * @param continuous
	 * @return this
	 */
	public ReportTimestamp continuous(Boolean continuous) {
		this.continuous = continuous;
		return this;
	}

	/**
	 * Get continuous
	 * 
	 * @return continuous
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public Boolean isContinuous() {
		return continuous;
	}

	/**
	 * 
	 * @param continuous
	 */
	public void setContinuous(Boolean continuous) {
		this.continuous = continuous;
	}

	/**
	 * @return the startUtcSeconds
	 */
	@JsonIgnore
	public Long getStartUtcSeconds() {
		return startUtcSeconds;
	}

	/**
	 * @param startUtcSeconds the startUtcSeconds to set
	 */
	public void setStartUtcSeconds(Long startUtcSeconds) {
		this.startUtcSeconds = startUtcSeconds;
	}

	/**
	 * @return the endUtcSeconds
	 */
	@JsonIgnore
	public Long getEndUtcSeconds() {
		return endUtcSeconds;
	}

	/**
	 * @param endUtcSeconds the endUtcSeconds to set
	 */
	public void setEndUtcSeconds(Long endUtcSeconds) {
		this.endUtcSeconds = endUtcSeconds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((continuous == null) ? 0 : continuous.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		ReportTimestamp other = (ReportTimestamp) obj;
		if (continuous == null) {
			if (other.continuous != null)
				return false;
		} else if (!continuous.equals(other.continuous))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class AnswerReportTimestamp {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    start: ").append(toIndentedString(start)).append("\n");
		sb.append("    end: ").append(toIndentedString(end)).append("\n");
		sb.append("    continuous: ").append(toIndentedString(continuous)).append("\n");
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
