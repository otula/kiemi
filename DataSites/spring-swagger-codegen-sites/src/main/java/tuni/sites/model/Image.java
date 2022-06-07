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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

/**
 * Image
 */
@Validated
@Entity(name = Definitions.ENTITY_IMAGE) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_IMAGE)
@JsonInclude(Include.NON_NULL)
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
public class Image {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = tuni.sites.model.Definitions.COLUMN_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_URL, length = Definitions.MAX_LENGTH_URL)
	@JsonProperty(Definitions.JSON_PROPERTY_URL)
	private String url = null;
	@Column(name = Definitions.COLUMN_SCALE, length = Definitions.MAX_LENGTH_SCALE)
	@JsonProperty(Definitions.JSON_PROPERTY_SCALE)
	private String scale = null;
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
	 * @param url
	 * @return this
	 */
	public Image url(String url) {
		this.url = url;
		return this;
	}

	/**
	 * URL link to the level picture. Note that even though scale is not strictly
	 * required, it is recommended to prove it if url is given.
	 * 
	 * @return url
	 **/
	@ApiModelProperty(required = true, value = "URL link to the level picture. Note that even though scale is not strictly required, it is recommended to prove it if url is given.")
	@NotNull
	@Size(max=Definitions.MAX_LENGTH_URL)
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @param scale
	 * @return this
	 */
	public Image scale(String scale) {
		this.scale = scale;
		return this;
	}

	/**
	 * Scale of this level, e.g. 1:100.
	 * 
	 * @return scale
	 **/
	@ApiModelProperty(value = "Scale of this level, e.g. 1:100.")
	@Size(max=Definitions.MAX_LENGTH_SCALE)
	public String getScale() {
		return scale;
	}

	/**
	 * 
	 * @param scale
	 */
	public void setScale(String scale) {
		this.scale = scale;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Image image = (Image) o;
		return Objects.equals(this.url, image.url) && Objects.equals(this.scale, image.scale);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, scale);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Image {\n");

		sb.append("    url: ").append(toIndentedString(url)).append("\n");
		sb.append("    scale: ").append(toIndentedString(scale)).append("\n");
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
