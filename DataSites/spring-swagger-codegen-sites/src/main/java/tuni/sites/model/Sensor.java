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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

/**
 * Sensor
 */
@Validated
@Entity(name = Definitions.ENTITY_SENSOR) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_SENSORS)
@JsonInclude(Include.NON_NULL)
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
public class Sensor {
	private static final String TYPE_ARANET = "aranet";
	private static final String TYPE_FOURDEG = "fourdeg";
	private static final String TYPE_ILMATIETEEN_LAITOS = "fmi";
	private static final String TYPE_OUMAN = "ouman";
	private static final String TYPE_PORI_ENERGIA = "porienergia";
	private static final String TYPE_RUUVI = "ruuvi";
	@Column(name = Definitions.COLUMN_DESCRIPTION, length = Definitions.MAX_LENGTH_DESCRIPTION)
	@JsonProperty(Definitions.JSON_PROPERTY_DESCRIPTION)
	private String description = null;
	@Column(name = Definitions.COLUMN_EXTERNAL_ID, length = Definitions.MAX_LENGTH_EXTERNAL_ID)
	@JsonProperty(Definitions.JSON_PROPERTY_EXTERNAL_ID)
	private String externalId = null;
	@Column(name = Definitions.COLUMN_SERVICE_TYPE)
	@JsonProperty(Definitions.JSON_PROPERTY_SERVICE_TYPE)
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType = null;
	@Column(name = Definitions.COLUMN_X)
	@JsonProperty(Definitions.JSON_PROPERTY_X)
	private Double x = null;
	@Column(name = Definitions.COLUMN_Y)
	@JsonProperty(Definitions.JSON_PROPERTY_Y)
	private Double y = null;
	@Column(name = Definitions.COLUMN_Z)
	@JsonProperty(Definitions.JSON_PROPERTY_Z)
	private Double z = null;
	@Id
	@Column(name = Definitions.COLUMN_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.JSON_PROPERTY_ID)
	private Long id = null;
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.JSON_PROPERTY_NAME)
	private String name = null;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_CREATED)
	private Date rowCreated = null;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_UPDATED)
	private Date rowUpdated = null;

	/**
	 * Service identifier for this sensor.
	 */
	public enum ServiceType {
		/** Fourdeg data service */
		fourdeg(TYPE_FOURDEG),
		/** Ruuvi/InfluxDB data service */
		ruuvi(TYPE_RUUVI),
		/** Ouman data service */
		ouman(TYPE_OUMAN),
		/** Pori Energia data service */
		porienergia(TYPE_PORI_ENERGIA),
		/** Aranet data service */
		aranet(TYPE_ARANET),
		/** Ilmatieteen laitos data service */
		fmi(TYPE_ILMATIETEEN_LAITOS);

		private String value;

		ServiceType(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		/**
		 * 
		 * @param text
		 * @return type
		 */
		@JsonCreator
		public static ServiceType fromValue(String text) {
			for (ServiceType b : ServiceType.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	} // enum ServiceType

	/**
	 * 
	 * @param externalId
	 * @return this
	 */
	public Sensor externalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	/**
	 * External service id for the sensor.
	 * 
	 * @return externalId
	 **/
	@ApiModelProperty(required = true, value = "External service id for the sensor.")
	@NotNull
	@Size(max=Definitions.MAX_LENGTH_EXTERNAL_ID)
	public String getExternalId() {
		return externalId;
	}

	/**
	 * 
	 * @param externalId
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Sensor description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Get description
	 * 
	 * @return description
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_DESCRIPTION)
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @param serviceType
	 * @return this
	 */
	public Sensor serviceType(ServiceType serviceType) {
		this.serviceType = serviceType;
		return this;
	}

	/**
	 * Service identifier for this sensor.
	 * 
	 * @return serviceType
	 **/
	@ApiModelProperty(required = true, value = "Service identifier for this sensor.")
	@NotNull
	public ServiceType getServiceType() {
		return serviceType;
	}

	/**
	 * 
	 * @param serviceType
	 */
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * 
	 * @param x
	 * @return this
	 */
	public Sensor x(Double x) {
		this.x = x;
		return this;
	}

	/**
	 * X location for this sensor on the associated level.
	 * 
	 * @return x
	 **/
	@ApiModelProperty(value = "X location for this sensor on the associated level.")
	public Double getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 */
	public void setX(Double x) {
		this.x = x;
	}

	/**
	 * 
	 * @param y
	 * @return this
	 */
	public Sensor y(Double y) {
		this.y = y;
		return this;
	}

	/**
	 * Y location for this sensor on the associated level.
	 * 
	 * @return y
	 **/
	@ApiModelProperty(value = "Y location for this sensor on the associated level.")
	public Double getY() {
		return y;
	}

	/**
	 * 
	 * @param y
	 */
	public void setY(Double y) {
		this.y = y;
	}

	/**
	 * 
	 * @param z
	 * @return this
	 */
	public Sensor z(Double z) {
		this.z = z;
		return this;
	}

	/**
	 * Z location for this sensor on the associated level.
	 * 
	 * @return z
	 **/
	@ApiModelProperty(value = "Z location for this sensor on the associated level.")
	public Double getZ() {
		return z;
	}

	/**
	 * 
	 * @param z
	 */
	public void setZ(Double z) {
		this.z = z;
	}

	/**
	 * 
	 * @param id
	 * @return this
	 */
	public Sensor id(Long id) {
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
	public Sensor name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Get name
	 * 
	 * @return name
	 **/
	@ApiModelProperty(value = "")
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

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Sensor sensor = (Sensor) o;
		return Objects.equals(this.externalId, sensor.externalId) && Objects.equals(this.serviceType, sensor.serviceType) && Objects.equals(this.x, sensor.x) && Objects.equals(this.y, sensor.y) && Objects.equals(this.z, sensor.z) && Objects.equals(this.id, sensor.id) && Objects.equals(this.description, sensor.description)
				&& Objects.equals(this.name, sensor.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(externalId, serviceType, x, y, z, id, description, name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Sensor {\n");

		sb.append("    externalId: ").append(toIndentedString(externalId)).append("\n");
		sb.append("    serviceType: ").append(toIndentedString(serviceType)).append("\n");
		sb.append("    x: ").append(toIndentedString(x)).append("\n");
		sb.append("    y: ").append(toIndentedString(y)).append("\n");
		sb.append("    z: ").append(toIndentedString(z)).append("\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
