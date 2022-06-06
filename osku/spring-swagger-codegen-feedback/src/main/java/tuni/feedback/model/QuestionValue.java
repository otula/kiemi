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
 * QuestionValue
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_QUESTION_VALUE) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_QUESTION_VALUE)
@JsonInclude(Include.NON_NULL)
public class QuestionValue {
	@Id
	@Column(name = Definitions.COLUMN_QUESTION_VALUE_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;
	@Column(name = Definitions.COLUMN_COLOR_HINT, length = Definitions.MAX_LENGTH_COLOR)
	@JsonProperty(Definitions.PROPERTY_COLOR_HINT)
	private String colorHint = null;
	@Column(name = Definitions.COLUMN_VALUE)
	@JsonProperty(Definitions.PROPERTY_VALUE)
	private Long value = null;
	@Column(name = Definitions.COLUMN_TEXT, length = Definitions.MAX_LENGTH_TEXT)
	@JsonProperty(Definitions.PROPERTY_TEXT)
	private String text = null;
	@Column(name = Definitions.COLUMN_IMAGE_URL, length = Definitions.MAX_LENGTH_URL)
	@JsonProperty(Definitions.PROPERTY_IMAGE_URL)
	private String imageUrl = null;
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
	 * @param value
	 * @return this
	 */
	public QuestionValue value(Long value) {
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
	public Long getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(Long value) {
		this.value = value;
	}

	/**
	 * 
	 * @param colorHint
	 * @return this
	 */
	public QuestionValue colorHint(String colorHint) {
		this.colorHint = colorHint;
		return this;
	}

	/**
	 * HTML color (#000000) hint for this choise to be used when displayed in UI.
	 * Clients may or may not respect this.
	 * 
	 * @return colorHint
	 **/
	@ApiModelProperty(value = "HTML color (#000000) hint for this choise to be used when displayed in UI. Clients may or may not respect this.")
	@Size(max=Definitions.MAX_LENGTH_COLOR)
	public String getColorHint() {
		return colorHint;
	}

	/**
	 * 
	 * @param colorHint
	 */
	public void setColorHint(String colorHint) {
		this.colorHint = colorHint;
	}

	/**
	 * 
	 * @param text
	 * @return this
	 */
	public QuestionValue text(String text) {
		this.text = text;
		return this;
	}

	/**
	 * Get text
	 * 
	 * @return text
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
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
	 * @param imageUrl
	 * @return this
	 */
	public QuestionValue imageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public QuestionValue imageUrl(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get imageUrl
	 * 
	 * @return imageUrl
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_URL)
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * 
	 * @param imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		QuestionValue questionValue = (QuestionValue) o;
		return Objects.equals(this.value, questionValue.value) && Objects.equals(this.text, questionValue.text) && Objects.equals(this.imageUrl, questionValue.imageUrl) && Objects.equals(this.colorHint, questionValue.colorHint);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, text, imageUrl, colorHint);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class QuestionValue {\n");

		sb.append("    value: ").append(toIndentedString(value)).append("\n");
		sb.append("    text: ").append(toIndentedString(text)).append("\n");
		sb.append("    imageUrl: ").append(toIndentedString(imageUrl)).append("\n");
		sb.append("    colorHint: ").append(toIndentedString(colorHint)).append("\n");
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
