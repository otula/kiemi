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
package tuni.feedback.exception;

/**
 * Permission was denied (i.e. 403 Forbidden).
 * 
 */
public class ForbiddenException extends IllegalArgumentException {
	/** serial version UID */
	private static final long serialVersionUID = 7737695336961995739L;

	/**
	 * 
	 */
	public ForbiddenException() {
		super();
	}

	/**
	 * 
	 * @param s
	 */
	public ForbiddenException(String s) {
		super(s);
	}
}
