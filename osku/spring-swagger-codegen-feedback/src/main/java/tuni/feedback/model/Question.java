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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Question
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_QUESTION)
@Table(name = Definitions.TABLE_QUESTION)
@JsonInclude(Include.NON_NULL)
public class Question {
	@Id
	@Column(name = Definitions.COLUMN_QUESTION_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_TEXT, length = Definitions.MAX_LENGTH_TEXT)
	@JsonProperty(Definitions.PROPERTY_TEXT)
	private String text = null;
	@Column(name = Definitions.COLUMN_INDEX_NRO)
	@JsonProperty(Definitions.PROPERTY_INDEX_NRO)
	private Integer indexNro = null;
	@Column(name = Definitions.COLUMN_DEFAULT_VALUE)
	@JsonProperty(Definitions.PROPERTY_DEFAULT_VALUE)
	private Integer defaultValue = null;
	@Column(name = Definitions.COLUMN_MAX_SELECTIONS)
	@JsonProperty(Definitions.PROPERTY_MAX_SELECTIONS)
	private Integer maxSelections = null;
	@OrderColumn
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = Definitions.COLUMN_QUESTION_ID)
	@JsonProperty(Definitions.PROPERTY_VALUE)
	@Valid
	private List<QuestionValue> value = null;
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
	 * @param defaultValue
	 * @return this
	 */
	public Question defaultValue(Integer defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	/**
	 * Get defaultValue
	 * 
	 * @return defaultValue
	 **/
	@ApiModelProperty(value = "")
	public Integer getDefaultValue() {
		return defaultValue;
	}

	/**
	 * 
	 * @param defaultValue
	 */
	public void setDefaultValue(Integer defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public Question id(Long id) {
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
	 * @param text
	 * @return this
	 */
	public Question text(String text) {
		this.text = text;
		return this;
	}

	/**
	 * Get text
	 * 
	 * @return text
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_TEXT)
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 
	 * @param maxSelections
	 * @return this
	 */
	public Question maxSelections(Integer maxSelections) {
		this.maxSelections = maxSelections;
		return this;
	}

	/**
	 * Maximum number of value selections for this question.
	 * 
	 * @return maxSelections
	 **/
	@ApiModelProperty(required = true, value = "Maximum number of value selections for this question.")
	@NotNull
	public Integer getMaxSelections() {
		return maxSelections;
	}

	/**
	 * 
	 * @param maxSelections
	 */
	public void setMaxSelections(Integer maxSelections) {
		this.maxSelections = maxSelections;
	}

	/**
	 * 
	 * @param indexNro
	 * @return this
	 */
	public Question indexNro(Integer indexNro) {
		this.indexNro = indexNro;
		return this;
	}

	/**
	 * Get indexNro
	 * 
	 * @return indexNro
	 **/
	@ApiModelProperty(required = true, value = "")
	public Integer getIndexNro() {
		return indexNro;
	}

	/**
	 * 
	 * @param indexNro
	 */
	public void setIndexNro(Integer indexNro) {
		this.indexNro = indexNro;
	}

	/**
	 * 
	 * @param value
	 * @return this
	 */
	public Question value(List<QuestionValue> value) {
		this.value = value;
		return this;
	}

	/**
	 * 
	 * @param valueItem
	 * @return this
	 */
	public Question addValueItem(QuestionValue valueItem) {
		if (this.value == null) {
			this.value = new ArrayList<>();
		}
		this.value.add(valueItem);
		return this;
	}

	/**
	 * Get values
	 * 
	 * @return values
	 **/
	@ApiModelProperty(required = true, value = "")
	@Valid
	public List<QuestionValue> getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(List<QuestionValue> value) {
		this.value = value;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Question question = (Question) o;
		return Objects.equals(this.id, question.id) && Objects.equals(this.text, question.text) && Objects.equals(this.indexNro, question.indexNro) && Objects.equals(this.maxSelections, question.maxSelections) && Objects.equals(this.defaultValue, question.defaultValue) && Objects.equals(this.value, question.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, text, indexNro, maxSelections, defaultValue, value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Question {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    text: ").append(toIndentedString(text)).append("\n");
		sb.append("    indexNro: ").append(toIndentedString(indexNro)).append("\n");
		sb.append("    maxSelections: ").append(toIndentedString(maxSelections)).append("\n");
		sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
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
