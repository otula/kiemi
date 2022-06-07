/**
 * Copyright 2022 Tampere University
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
package tuni.data.adapters.influxdb.aranet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Aranet properties
 * 
 */
@Configuration
@PropertySource("classpath:adapter-aranet.properties")
public class AranetProperties {
	@Value("${aranet.influxdb.database}")
	private String _database = null;
	@Value("${aranet.influxdb.measurement}")
	private String _measurement = null;
	@Value("${aranet.influxdb.password}")
	private String _password = null;
	@Value("${aranet.influxdb.retentionpolicy}")
	private String _retentionPolicy = null;
	@Value("${aranet.influxdb.tag}")
	private String _tag = null;
	@Value("${aranet.influxdb.uri}")
	private String _uri = null;
	@Value("${aranet.influxdb.username}")
	private String _username = null;

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return _database;
	}

	/**
	 * @return the measurement
	 */
	public String getMeasurement() {
		return _measurement;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return _password;
	}

	/**
	 * @return the retentionPolicy
	 */
	public String getRetentionPolicy() {
		return _retentionPolicy;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return _tag;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return _uri;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return _username;
	}
}
