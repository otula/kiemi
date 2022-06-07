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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
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
 * Layer
 */
@Validated
@Entity(name = Definitions.ENTITY_LAYER) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_LAYERS)
@JsonInclude(Include.NON_NULL)
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
public class Layer {
	@Column(name = Definitions.COLUMN_INDEX)
	@JsonProperty(Definitions.JSON_PROPERTY_INDEX)
	private Integer index = null;
	@Column(name = Definitions.COLUMN_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.JSON_PROPERTY_NAME)
	private String name = null;
	@Column(name = Definitions.COLUMN_ADDRESS, length = Definitions.MAX_LENGTH_ADDRESS)
	@JsonProperty(Definitions.JSON_PROPERTY_ADDRESS)
	private String address = null;
	@Column(name = Definitions.COLUMN_COUNTRY, length = Definitions.MAX_LENGTH_COUNTRY)
	@JsonProperty(Definitions.JSON_PROPERTY_COUNTRY)
	private String country = null;
	@Column(name = Definitions.COLUMN_CITY, length = Definitions.MAX_LENGTH_CITY)
	@JsonProperty(Definitions.JSON_PROPERTY_CITY)
	private String city = null;
	@Column(name = Definitions.COLUMN_DESCRIPTION, length = Definitions.MAX_LENGTH_DESCRIPTION)
	@JsonProperty(Definitions.JSON_PROPERTY_DESCRIPTION)
	private String description = null;
	@Column(name = Definitions.COLUMN_EXTERNAL_URL, length = Definitions.MAX_LENGTH_URL)
	@JsonProperty(Definitions.JSON_PROPERTY_EXTERNAL_URL)
	private String externalUrl = null;
	@Id
	@Column(name = Definitions.COLUMN_ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(Definitions.JSON_PROPERTY_ID)
	private Long id = null;
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = Definitions.COLUMN_LAYER_ID)
	@JsonProperty(Definitions.JSON_PROPERTY_IMAGE)
	private Image image = null;
	@Column(name = Definitions.COLUMN_LATITUDE)
	@JsonProperty(Definitions.JSON_PROPERTY_LATITUDE)
	private Double latitude = null;
	@Column(name = Definitions.COLUMN_LONGITUDE)
	@JsonProperty(Definitions.JSON_PROPERTY_LONGITUDE)
	private Double longitude = null;
	@Column(name = Definitions.COLUMN_ORGANIZATION_NAME, length = Definitions.MAX_LENGTH_NAME)
	@JsonProperty(Definitions.JSON_PROPERTY_ORGANIZATION_NAME)
	private String organizationName = null;
	@Column(name = Definitions.COLUMN_POSTAL_CODE, length = Definitions.MAX_LENGTH_POSTAL_CODE)
	@JsonProperty(Definitions.JSON_PROPERTY_POSTAL_CODE)
	private String postalCode = null;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_CREATED)
	private Date rowCreated = null;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Definitions.COLUMN_ROW_UPDATED)
	private Date rowUpdated = null;
	@Column(name = Definitions.COLUMN_TYPE)
	@JsonProperty(Definitions.JSON_PROPERTY_TYPE)
	@Enumerated(EnumType.STRING)
	private Type type = null;

	/**
	 * Type of the layer.
	 */
	public enum Type {
		/** unknown type */
		unknown("unknown"),
		/** outdoor location */
		outdoor("outdoor"),
		/** building */
		building("building"),
		/** floor */
		floor("floor"),
		/** room */
		room("room");

		private String value;

		Type(String value) {
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
		public static Type fromValue(String text) {
			for (Type b : Type.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	} // enum Type

	/**
	 * 
	 * @param index
	 * @return this
	 */
	public Layer index(Integer index) {
		this.index = index;
		return this;
	}

	/**
	 * Unique index for this layer, for ordering layers in a specific order.
	 * 
	 * @return index
	 **/
	@ApiModelProperty(required = true, value = "Unique index for this layer, for ordering layers in a specific order.")
	@NotNull
	public Integer getIndex() {
		return index;
	}

	/**
	 * 
	 * @param index
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * 
	 * @param name
	 * @return this
	 */
	public Layer name(String name) {
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
	 * @param address
	 * @return this
	 */
	public Layer address(String address) {
		this.address = address;
		return this;
	}

	/**
	 * Street address
	 * 
	 * @return address
	 **/
	@ApiModelProperty(value = "Street address")
	@Size(max=Definitions.MAX_LENGTH_ADDRESS)
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 
	 * @param country
	 * @return this
	 */
	public Layer country(String country) {
		this.country = country;
		return this;
	}

	/**
	 * Get country
	 * 
	 * @return country
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_COUNTRY)
	public String getCountry() {
		return country;
	}

	/**
	 * 
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * 
	 * @param city
	 * @return this
	 */
	public Layer city(String city) {
		this.city = city;
		return this;
	}

	/**
	 * Get city
	 * 
	 * @return city
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_CITY)
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 
	 * @param externalUrl
	 * @return this
	 */
	public Layer externalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
		return this;
	}

	/**
	 * External web link.
	 * 
	 * @return externalUrl
	 **/
	@ApiModelProperty(value = "External web link.")
	@Size(max=Definitions.MAX_LENGTH_URL)
	public String getExternalUrl() {
		return externalUrl;
	}

	/**
	 * 
	 * @param externalUrl
	 */
	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Layer description(String description) {
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
	 * @param id
	 * @return this
	 */
	public Layer id(Long id) {
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
	 * @param image
	 * @return this
	 */
	public Layer image(Image image) {
		this.image = image;
		return this;
	}

	/**
	 * Get image
	 * 
	 * @return image
	 **/
	@ApiModelProperty(value = "")
	@Valid
	public Image getImage() {
		return image;
	}

	/**
	 * 
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * 
	 * @param latitude
	 * @return this
	 */
	public Layer latitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	/**
	 * Latitude for the location coordinate. If latitude is given, longitude must
	 * also be provided.
	 * 
	 * @return latitude
	 **/
	@ApiModelProperty(value = "Latitude for the location coordinate. If latitude is given, longitude must also be provided.")
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @param latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @param longitude
	 * @return this
	 */
	public Layer longitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

	/**
	 * Longitude for the location coordinate. If longitude is given, latitude must
	 * also be provided.
	 * 
	 * @return longitude
	 **/
	@ApiModelProperty(value = "Longitude for the location coordinate. If longitude is given, latitude must also be provided.")
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @param longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * 
	 * @param organizationName
	 * @return this
	 */
	public Layer organizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	/**
	 * Organization name for this site.
	 * 
	 * @return organizationName
	 **/
	@ApiModelProperty(value = "Organization name for this site.")
	@Size(max=Definitions.MAX_LENGTH_NAME)
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * 
	 * @param organizationName
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * 
	 * @param postalCode
	 * @return this
	 */
	public Layer postalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	/**
	 * Get postalCode
	 * 
	 * @return postalCode
	 **/
	@ApiModelProperty(value = "")
	@Size(max=Definitions.MAX_LENGTH_POSTAL_CODE)
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * 
	 * @param postalCode
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * 
	 * @param type
	 * @return this
	 */
	public Layer type(Type type) {
		this.type = type;
		return this;
	}

	/**
	 * Type of the layer.
	 * 
	 * @return type
	 **/
	@ApiModelProperty(required = true, value = "Type of the layer.")
	@NotNull
	public Type getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((externalUrl == null) ? 0 : externalUrl.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((organizationName == null) ? 0 : organizationName.hashCode());
		result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
		result = prime * result + ((rowCreated == null) ? 0 : rowCreated.hashCode());
		result = prime * result + ((rowUpdated == null) ? 0 : rowUpdated.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Layer other = (Layer) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (externalUrl == null) {
			if (other.externalUrl != null)
				return false;
		} else if (!externalUrl.equals(other.externalUrl))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organizationName == null) {
			if (other.organizationName != null)
				return false;
		} else if (!organizationName.equals(other.organizationName))
			return false;
		if (postalCode == null) {
			if (other.postalCode != null)
				return false;
		} else if (!postalCode.equals(other.postalCode))
			return false;
		if (rowCreated == null) {
			if (other.rowCreated != null)
				return false;
		} else if (!rowCreated.equals(other.rowCreated))
			return false;
		if (rowUpdated == null) {
			if (other.rowUpdated != null)
				return false;
		} else if (!rowUpdated.equals(other.rowUpdated))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Layer {\n");

		sb.append("    index: ").append(toIndentedString(index)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    address: ").append(toIndentedString(address)).append("\n");
		sb.append("    country: ").append(toIndentedString(country)).append("\n");
		sb.append("    city: ").append(toIndentedString(city)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    image: ").append(toIndentedString(image)).append("\n");
		sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
		sb.append("    externalUrl: ").append(toIndentedString(externalUrl)).append("\n");
		sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
		sb.append("    organizationName: ").append(toIndentedString(organizationName)).append("\n");
		sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
