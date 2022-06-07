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
package tuni.data.exception;

/**
 * Invalid parameter was given in the request (i.e. 400 Bad Request).
 * 
 */
public class InvalidParameterException extends IllegalArgumentException {
	/** serial version UID */
	private static final long serialVersionUID = 6238494357035062903L;

	/**
	 * 
	 */
	public InvalidParameterException() {
		super();
	}

	/**
	 * 
	 * @param s
	 */
	public InvalidParameterException(String s) {
		super(s);
	}
}
