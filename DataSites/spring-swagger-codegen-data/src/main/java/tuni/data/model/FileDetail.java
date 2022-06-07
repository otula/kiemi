package tuni.data.model;

import java.util.Date;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * File
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-09-03T13:24:01.043222+03:00[Europe/Helsinki]")
public class FileDetail {
	@JsonProperty(Definitions.JSON_PROPERTY_ID)
	private Long id = null;
	@JsonProperty(Definitions.JSON_PROPERTY_TIMESTAMP)
	private Date timestamp = null;

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public FileDetail id(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull
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
	 * @param timestamp
	 * @return this
	 */
	public FileDetail timestamp(Date timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	/**
	 * File timestamp.
	 * 
	 * @return timestamp
	 **/
	@ApiModelProperty(required = true, value = "File timestamp.")
	@NotNull
	@Valid
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FileDetail file = (FileDetail) o;
		return Objects.equals(this.id, file.id) && Objects.equals(this.timestamp, file.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, timestamp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class File {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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
