/**
 * Copyright 2021 Tampere University
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
package tuni.lorawan.sensors.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Data
 */
@Validated
@Entity(name = Definitions.ENTITY_DATA) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_SENSOR_DATA, indexes = {@Index(name = Definitions.COLUMN_NODE_ID+"_INDEX",  columnList = Definitions.COLUMN_NODE_ID, unique = false)})
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-06-28T16:14:09.575323+03:00[Europe/Helsinki]")
public class Data {
	@JsonProperty(Definitions.JSON_PROPERTY_ID)
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = Definitions.COLUMN_DATA_ID)
	private Long dataId = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(Definitions.JSON_PROPERTY_ECO2)
	@Column(name = Definitions.COLUMN_ECO2)
	private Long eco2 = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(Definitions.JSON_PROPERTY_HUMIDITY)
	@Column(name = Definitions.COLUMN_HUMIDITY)
	private Double humidity = null;
	@Column(name = Definitions.COLUMN_NODE_ID, length = 40)
	private String nodeId = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(Definitions.JSON_PROPERTY_PRESSURE)
	@Column(name = Definitions.COLUMN_PRESSURE)
	private Double pressure = null;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_CREATED)
	private Date rowCreated = null;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_UPDATED)
	private Date rowUpdated = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(Definitions.JSON_PROPERTY_TEMPERATURE)
	@Column(name = Definitions.COLUMN_TEMPERATURE)
	private Double temperature = null;
	@JsonProperty(Definitions.JSON_PROPERTY_TIMESTAMP)
	@Convert(converter = TimestampConverter.class)
	@Column(name = Definitions.COLUMN_TIMESTAMP)
	private Date timestamp = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(Definitions.JSON_PROPERTY_TVOC)
	@Column(name = Definitions.COLUMN_TVOC)
	private Long tvoc = null;

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public Data id(Long id) {
		this.dataId = id;
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
		return dataId;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.dataId = id;
	}

	/**
	 * 
	 * @param timestamp
	 * @return this
	 */
	public Data timestamp(Date timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	/**
	 * Creation timestamp (RFC3339)
	 * 
	 * @return timestamp
	 **/
	@ApiModelProperty(required = true, value = "Creation timestamp (RFC3339)")
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

	/**
	 * 
	 * @param eco2
	 * @return this
	 */
	public Data eco2(Long eco2) {
		this.eco2 = eco2;
		return this;
	}

	/**
	 * eCO2 in ppm
	 * 
	 * @return eco2
	 **/
	@ApiModelProperty(value = "eCO2 in ppm")
	public Long getEco2() {
		return eco2;
	}

	/**
	 * 
	 * @param eco2
	 */
	public void setEco2(Long eco2) {
		this.eco2 = eco2;
	}

	/**
	 * 
	 * @param humidity
	 * @return this
	 */
	public Data humidity(Double humidity) {
		this.humidity = humidity;
		return this;
	}

	/**
	 * relative humidity %
	 * 
	 * @return humidity
	 **/
	@ApiModelProperty(value = "relative humidity %")
	public Double getHumidity() {
		return humidity;
	}

	/**
	 * 
	 * @param humidity
	 */
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}

	/**
	 * 
	 * @param pressure
	 * @return this
	 */
	public Data pressure(Double pressure) {
		this.pressure = pressure;
		return this;
	}

	/**
	 * pressure in hPa
	 * 
	 * @return pressure
	 **/
	@ApiModelProperty(value = "pressure in hPa")
	public Double getPressure() {
		return pressure;
	}

	/**
	 * 
	 * @param pressure
	 */
	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	/**
	 * 
	 * @param temperature
	 * @return this
	 */
	public Data temperature(Double temperature) {
		this.temperature = temperature;
		return this;
	}

	/**
	 * temperature in C
	 * 
	 * @return temperature
	 **/
	@ApiModelProperty(value = "temperature in C")
	public Double getTemperature() {
		return temperature;
	}

	/**
	 * 
	 * @param temperature
	 */
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	/**
	 * 
	 * @param tvoc
	 * @return this
	 */
	public Data tvoc(Long tvoc) {
		this.tvoc = tvoc;
		return this;
	}

	/**
	 * TVOC in ppb
	 * 
	 * @return tvoc
	 **/
	@ApiModelProperty(value = "TVOC in ppb")
	public Long getTvoc() {
		return tvoc;
	}

	/**
	 * 
	 * @param tvoc
	 */
	public void setTvoc(Long tvoc) {
		this.tvoc = tvoc;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Data data = (Data) o;
		return Objects.equals(this.dataId, data.dataId) && Objects.equals(this.timestamp, data.timestamp) && Objects.equals(this.eco2, data.eco2) && Objects.equals(this.humidity, data.humidity) && Objects.equals(this.pressure, data.pressure) && Objects.equals(this.temperature, data.temperature) && Objects.equals(this.tvoc, data.tvoc);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataId, timestamp, eco2, humidity, pressure, temperature, tvoc);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Data {\n");

		sb.append("    id: ").append(toIndentedString(dataId)).append("\n");
		sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
		sb.append("    eco2: ").append(toIndentedString(eco2)).append("\n");
		sb.append("    humidity: ").append(toIndentedString(humidity)).append("\n");
		sb.append("    pressure: ").append(toIndentedString(pressure)).append("\n");
		sb.append("    temperature: ").append(toIndentedString(temperature)).append("\n");
		sb.append("    tvoc: ").append(toIndentedString(tvoc)).append("\n");
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
	
	/**
	 * 
	 * 
	 */
	@Converter
	public static class TimestampConverter implements AttributeConverter <Date, Long> {

	    @Override
	    public Long convertToDatabaseColumn(Date timestamp) {
	        return timestamp.getTime();
	    }

	    @Override
	    public Date convertToEntityAttribute(Long timestamp) {
	        return new Date(timestamp);
	    }
	} // class TimestampConverter
}
