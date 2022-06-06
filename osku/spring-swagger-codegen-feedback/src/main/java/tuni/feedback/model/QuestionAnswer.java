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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

/**
 * AnswerQuestionAnswer
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T20:55:51.835399+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_QUESTION_ANSWER) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_QUESTION_ANSWER)
@JsonInclude(Include.NON_NULL)
public class QuestionAnswer {
	@Id
	@Column(name = Definitions.COLUMN_QUESTION_ANSWER_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;
	@Column(name = Definitions.COLUMN_QUESTION_ID)
	@JsonProperty(Definitions.PROPERTY_QUESTION_ID)
	private Long questionId = null;
	@Column(name = Definitions.COLUMN_VALUE)
	@JsonProperty(Definitions.PROPERTY_VALUE)
	private Integer value = null;
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
	 * @param questionId
	 * @return this
	 */
	public QuestionAnswer questionId(Long questionId) {
		this.questionId = questionId;
		return this;
	}

	/**
	 * Get questionId
	 * 
	 * @return questionId
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public Long getQuestionId() {
		return questionId;
	}

	/**
	 * 
	 * @param questionId
	 */
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	/**
	 * 
	 * @param value
	 * @return this
	 */
	public QuestionAnswer value(Integer value) {
		this.value = value;
		return this;
	}

	/**
	 * Get value
	 * 
	 * @return value
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public Integer getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(Integer value) {
		this.value = value;
	}

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public QuestionAnswer id(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@ApiModelProperty(required = true, value = "")
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
		result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		QuestionAnswer other = (QuestionAnswer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class AnswerQuestionAnswer {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    questionId: ").append(toIndentedString(questionId)).append("\n");
		sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
