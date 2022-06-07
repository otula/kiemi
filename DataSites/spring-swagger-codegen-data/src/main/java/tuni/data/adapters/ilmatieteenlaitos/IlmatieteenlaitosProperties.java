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
package tuni.data.adapters.ilmatieteenlaitos;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Ilmatieteenlaitos properties
 * 
 */
@Configuration
@PropertySource("classpath:adapter-ilmatieteenlaitos.properties")
public class IlmatieteenlaitosProperties {
	@Value("${ilmatieteenlaitos.storedqueryid}")
	private String _storedQueryId = null;
	@Value("#{'${ilmatieteenlaitos.parameters}'.split(',')}")
	private Set<String> _parameters = null;
	
	/**
	 * @return the storedQueryId
	 */
	public String getStoredQueryId() {
		return _storedQueryId;
	}
	/**
	 * @return the parameters
	 */
	public Set<String> getParameters() {
		return _parameters;
	}
}
