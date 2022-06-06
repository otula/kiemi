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
import javax.persistence.Convert;
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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import tuni.feedback.model.converters.DateConverter;
import tuni.feedback.model.converters.LongArrayConverter;

/**
 * Answer
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T20:55:51.835399+02:00[Europe/Helsinki]")
@Entity(name = Definitions.ENTITY_ANSWER)
@Table(name = Definitions.TABLE_ANSWER)
@JsonInclude(Include.NON_NULL)
public class Answer {
	@Id
	@Column(name = Definitions.COLUMN_ANSWER_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_USER_ID)
	@JsonProperty(Definitions.PROPERTY_USER_ID)
	private Long userId = null;
	@Convert(converter = LongArrayConverter.class)
	@Column(name = Definitions.COLUMN_AREA_ID)
	@JsonProperty(Definitions.PROPERTY_AREA_ID)
	@Valid
	private List<Long> areaId = null;
	@OrderColumn
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = Definitions.COLUMN_ANSWER_ID)
	@JsonProperty(Definitions.PROPERTY_QUESTION_ANSWER)
	@Valid
	private List<QuestionAnswer> questionAnswer = null;
	@OrderColumn
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = Definitions.COLUMN_ANSWER_ID)
	@JsonProperty(Definitions.PROPERTY_REPORT_TIMESTAMP)
	@Valid
	private List<ReportTimestamp> reportTimestamp = null;
	@Convert(converter = DateConverter.class)
	@Column(name = Definitions.COLUMN_SUBMIT_TIMESTAMP)
	@JsonProperty(Definitions.PROPERTY_SUBMIT_TIMESTAMP)
	private Date submitTimestamp = null;
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
	public Answer id(Long id) {
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
	 * @param userId
	 * @return this
	 */
	public Answer userId(Long userId) {
		this.userId = userId;
		return this;
	}

	/**
	 * Get userId
	 * 
	 * @return userId
	 **/
	@ApiModelProperty(value = "")
	public Long getUserId() {
		return userId;
	}

	/**
	 * 
	 * @param userId
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 
	 * @param areaId
	 * @return this
	 */
	public Answer areaId(List<Long> areaId) {
		this.areaId = areaId;
		return this;
	}

	/**
	 * 
	 * @param areaIdItem
	 * @return this
	 */
	public Answer addAreaIdItem(Long areaIdItem) {
		if (this.areaId == null) {
			this.areaId = new ArrayList<>();
		}
		this.areaId.add(areaIdItem);
		return this;
	}

	/**
	 * Get areaId
	 * 
	 * @return areaId
	 **/
	@ApiModelProperty(value = "")
	public List<Long> getAreaId() {
		return areaId;
	}

	/**
	 * 
	 * @param areaId
	 */
	public void setAreaId(List<Long> areaId) {
		this.areaId = areaId;
	}

	/**
	 * 
	 * @param questionAnswer
	 * @return this
	 */
	public Answer questionAnswer(List<QuestionAnswer> questionAnswer) {
		this.questionAnswer = questionAnswer;
		return this;
	}

	/**
	 * 
	 * @param questionAnswerItem
	 * @return this
	 */
	public Answer addQuestionAnswerItem(QuestionAnswer questionAnswerItem) {
		if (this.questionAnswer == null) {
			this.questionAnswer = new ArrayList<>();
		}
		this.questionAnswer.add(questionAnswerItem);
		return this;
	}

	/**
	 * Get questionAnswer
	 * 
	 * @return questionAnswer
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
	@Valid
	public List<QuestionAnswer> getQuestionAnswer() {
		return questionAnswer;
	}

	/**
	 * 
	 * @param questionAnswer
	 */
	public void setQuestionAnswer(List<QuestionAnswer> questionAnswer) {
		this.questionAnswer = questionAnswer;
	}

	/**
	 * 
	 * @param reportTimestamp
	 * @return this
	 */
	public Answer reportTimestamp(List<ReportTimestamp> reportTimestamp) {
		this.reportTimestamp = reportTimestamp;
		return this;
	}

	/**
	 * 
	 * @param reportTimestampItem
	 * @return this
	 */
	public Answer addReportTimestampItem(ReportTimestamp reportTimestampItem) {
		if (this.reportTimestamp == null) {
			this.reportTimestamp = new ArrayList<>();
		}
		this.reportTimestamp.add(reportTimestampItem);
		return this;
	}

	/**
	 * Get reportTimestamp
	 * 
	 * @return reportTimestamp
	 **/
	@ApiModelProperty(value = "")
	@Valid
	@NotNull
	public List<ReportTimestamp> getReportTimestamp() {
		return reportTimestamp;
	}

	/**
	 * 
	 * @param reportTimestamp
	 */
	public void setReportTimestamp(List<ReportTimestamp> reportTimestamp) {
		this.reportTimestamp = reportTimestamp;
	}

	/**
	 * 
	 * @param submitTimestamp
	 * @return this
	 */
	public Answer submitTimestamp(Date submitTimestamp) {
		this.submitTimestamp = submitTimestamp;
		return this;
	}

	/**
	 * Get submitTimestamp
	 * 
	 * @return submitTimestamp
	 **/
	@ApiModelProperty(value = "Submit timestamp, if not given, the service generate submission timestamp.")
	@Valid
	public Date getSubmitTimestamp() {
		return submitTimestamp;
	}

	/**
	 * 
	 * @param submitTimestamp
	 */
	public void setSubmitTimestamp(Date submitTimestamp) {
		this.submitTimestamp = submitTimestamp;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Answer answer = (Answer) o;
		return Objects.equals(this.id, answer.id) && Objects.equals(this.userId, answer.userId) && Objects.equals(this.areaId, answer.areaId) && Objects.equals(this.questionAnswer, answer.questionAnswer) && Objects.equals(this.reportTimestamp, answer.reportTimestamp) && Objects.equals(this.submitTimestamp, answer.submitTimestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, areaId, questionAnswer, reportTimestamp, submitTimestamp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Answer {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
		sb.append("    areaId: ").append(toIndentedString(areaId)).append("\n");
		sb.append("    questionAnswer: ").append(toIndentedString(questionAnswer)).append("\n");
		sb.append("    reportTimestamp: ").append(toIndentedString(reportTimestamp)).append("\n");
		sb.append("    submitTimestamp: ").append(toIndentedString(submitTimestamp)).append("\n");
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
