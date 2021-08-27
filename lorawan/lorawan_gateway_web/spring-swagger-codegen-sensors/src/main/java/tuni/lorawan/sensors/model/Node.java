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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
 * Node
 */
@Validated
@Entity(name = Definitions.ENTITY_NODE) // This tells Hibernate to make a table out of this class
@Table(name = Definitions.TABLE_SENSOR_NODES)
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-06-28T16:14:09.575323+03:00[Europe/Helsinki]")
public class Node {
	@JsonProperty(Definitions.JSON_PROPERTY_APPKEY)
	@Column(name = Definitions.COLUMN_APPKEY)
	private String appkey = null;
	@JsonProperty(Definitions.JSON_PROPERTY_APPSKEY)
	@Column(name = Definitions.COLUMN_APPSKEY)
	private String appskey = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(Definitions.JSON_PROPERTY_DESCRIPTION)
	@Column(name = Definitions.COLUMN_DESCRIPTION)
	private String description = null;
	@JsonProperty(Definitions.JSON_PROPERTY_ID)
	@Id
	@Column(name = Definitions.COLUMN_NODE_ID, length = 40)
	private String nodeId = null;
	@JsonProperty(Definitions.JSON_PROPERTY_NWKSKEY)
	@Column(name = Definitions.COLUMN_NWKSKEY)
	private String nwkskey = null;
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
	public Node id(String id) {
		this.nodeId = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@ApiModelProperty(value = "UUID for this node")
	@Valid
	public String getId() {
		return nodeId;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.nodeId = id;
	}

	/**
	 * 
	 * @param nwkskey
	 * @return this
	 */
	public Node nwkskey(String nwkskey) {
		this.nwkskey = nwkskey;
		return this;
	}

	/**
	 * 16 hex (32 characters) nwkskey, e.g. 00000000000000000000000000000000
	 * 
	 * @return nwkskey
	 **/
	@ApiModelProperty(required = true, value = "16 hex (32 characters) nwkskey, e.g. 00000000000000000000000000000000")
	@NotNull
	public String getNwkskey() {
		return nwkskey;
	}

	/**
	 * 
	 * @param nwkskey
	 */
	public void setNwkskey(String nwkskey) {
		this.nwkskey = nwkskey;
	}

	/**
	 * 
	 * @param appskey
	 * @return this
	 */
	public Node appskey(String appskey) {
		this.appskey = appskey;
		return this;
	}

	/**
	 * 16 hex (32 characters) appskey, e.g. 00000000000000000000000000000000
	 * 
	 * @return appskey
	 **/
	@ApiModelProperty(required = true, value = "16 hex (32 characters) appskey, e.g. 00000000000000000000000000000000")
	@NotNull
	public String getAppskey() {
		return appskey;
	}

	/**
	 * 
	 * @param appskey
	 */
	public void setAppskey(String appskey) {
		this.appskey = appskey;
	}

	/**
	 * 
	 * @param appkey
	 * @return this
	 */
	public Node appkey(String appkey) {
		this.appkey = appkey;
		return this;
	}

	/**
	 * 16 hex (32 characters) appkey, e.g. 00000000000000000000000000000000
	 * 
	 * @return appkey
	 **/
	@ApiModelProperty(required = true, value = "16 hex (32 characters) appkey, e.g. 00000000000000000000000000000000")
	@NotNull
	public String getAppkey() {
		return appkey;
	}

	/**
	 * 
	 * @param appkey
	 */
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	/**
	 * 
	 * @param description
	 * @return this
	 */
	public Node description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * optional description for this node
	 * 
	 * @return description
	 **/
	@ApiModelProperty(value = "optional description for this node")
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

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Node node = (Node) o;
		return Objects.equals(this.appkey, node.appkey) && Objects.equals(this.appskey, node.appskey) && Objects.equals(this.description, node.description) && Objects.equals(this.nodeId, node.nodeId) && Objects.equals(this.nwkskey, node.nwkskey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(appkey, appskey, description, nodeId, nwkskey);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Node {\n");

		sb.append("    appkey: ").append(toIndentedString(appkey)).append("\n");
		sb.append("    appskey: ").append(toIndentedString(appskey)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    id: ").append(toIndentedString(nodeId)).append("\n");
		sb.append("    nwkskey: ").append(toIndentedString(nwkskey)).append("\n");
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
