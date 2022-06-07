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
package tuni.sites.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sensor
 */
@Validated
public class ErrorResponse {
	@JsonProperty(Definitions.JSON_PROPERTY_MESSAGE)
	private String _message = null;
	@JsonProperty(Definitions.JSON_PROPERTY_STATUS)
	private HttpStatus _status = null;
	
	/**
	 * 	
	 * @param message
	 * @param status
	 */
	public ErrorResponse(String message, HttpStatus status) {
		_message = message;
		_status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return _message;
	}
	
	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return _status;
	} 
}
