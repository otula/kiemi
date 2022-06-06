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
package tuni.feedback.model.converters;

import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 * 
 */
@Converter
public class DateConverter implements AttributeConverter<Date, Long> {
	 	
	@Override
	public Long convertToDatabaseColumn(Date date) {
		if(date == null) {
			return null;
		}
		return date.getTime();
	}
	@Override
	public Date convertToEntityAttribute(Long row) {
		if(row == null) {
			return null;
		}
		return new Date(row);
	}
}
