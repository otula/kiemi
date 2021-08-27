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
package io.swagger;

import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.text.FieldPosition;
import java.util.Date;


/**
*
* 
*/
public class RFC3339DateFormat extends StdDateFormat {
	private static final long serialVersionUID = 1L;

	// Same as ISO8601DateFormat but serializing milliseconds.
	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		String value = StdDateFormat.DATE_FORMAT_ISO8601.format(date);
		toAppendTo.append(value);
		return toAppendTo;
	}
}