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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * 
 */
@Converter
public class LongArrayConverter implements AttributeConverter<List<Long>, String> {
	private static final Logger LOGGER = LogManager.getLogger(LongArrayConverter.class);
	private static final char SEPARATOR = ',';

	@Override
	public String convertToDatabaseColumn(List<Long> list) {
		if(list == null || list.isEmpty()) {
			return null;
		}else {
			StringBuilder sb = new StringBuilder();
			Iterator<Long> iter = list.iterator();
			sb.append(iter.next());
			while(iter.hasNext()) {
				sb.append(SEPARATOR);
				sb.append(iter.next());
			}
			return sb.toString();
		}
	}

	@Override
	public List<Long> convertToEntityAttribute(String row) {
		if(StringUtils.isBlank(row)) {
			return null;
		}
		String[] parts = StringUtils.split(row, SEPARATOR);
		ArrayList<Long> list = new ArrayList<>(parts.length);
		try {
			for(String p : parts) {
				list.add(Long.valueOf(p));
			}
		} catch (NumberFormatException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw ex;
		}
		
		return list;
	}
}
