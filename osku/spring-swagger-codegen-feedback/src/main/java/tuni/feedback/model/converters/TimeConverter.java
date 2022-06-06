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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 
 */
@Converter
public class TimeConverter implements AttributeConverter<String, Long> {
	/** hours:mins:seconds separator */
	public static final char SEPARATOR = ':';
	/** time format for HH:mm:ss using 24h clock and leading zeroes */
	public static final String TIME_FORMAT_REGEX = "^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$";
	private static final char PREFIX_ZERO = '0';
	private static final String SUFFIX_ZULU = "Z";
	
	/**
	 * 
	 * @param time utc time hh:mm:ssZ
	 * @return the time as seconds from utc midnight (00:00:00Z)
	 * @throws IllegalArgumentException 
	 * @throws NumberFormatException 
	 */
	public static long timeToSeconds(String time) throws IllegalArgumentException, NumberFormatException {
		if(StringUtils.isBlank(time)) {
			throw new IllegalArgumentException("Invalid time: "+time);
		}
		
		if(!StringUtils.endsWith(time, SUFFIX_ZULU)) {
			throw new IllegalArgumentException("Invalid time string: "+time);
		}
		time = StringUtils.chop(time);
		
		String[] parts = StringUtils.split(time, SEPARATOR);
		if(ArrayUtils.getLength(parts) != 3) {
			throw new IllegalArgumentException("Invalid time string: "+time);
		}
		
		return timeToSeconds(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
	}
	
	/**
	 * 
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return the time as total seconds passed since midnight
	 */
	public static long timeToSeconds(int hours, int minutes, int seconds) {
		return hours*3600+minutes*60+seconds;
	}
	
	@Override
	public Long convertToDatabaseColumn(String time){
		if(StringUtils.isBlank(time)) {
			return null;
		}else {
			return timeToSeconds(time);
		}
	}
	
	/**
	 * 
	 * @param seconds the time as seconds from utc midnight (00:00:00Z)
	 * @return time utc time hh:mm:ssZ
	 */
	public static String secondsToTime(long seconds) {
		StringBuilder sb = new StringBuilder();
		
		append(sb, (int) Math.floor(seconds/3600));
		
		sb.append(SEPARATOR);
		long remainder = seconds % 3600;
		append(sb, (long) Math.floor(remainder/60));
		
		sb.append(SEPARATOR);
		append(sb, remainder % 60);
		
		sb.append(SUFFIX_ZULU);
		
		return sb.toString();
	}

	@Override
	public String convertToEntityAttribute(Long row) {
		if(row == null) {
			return null;
		}
		return secondsToTime(row);
	}
	
	/**
	 * 
	 * @param sb
	 * @param value
	 */
	private static void append(StringBuilder sb, long value) {
		if(value < 10) {
			sb.append(PREFIX_ZERO);
		}
		sb.append(value);
	}
	
	/**
	 * Check that the given time is of valid format (HH:mm:ss) OR null/empty (not a value). I.e. a value that would pass this converter.
	 * 
	 * @param time
	 * @return true if the time is valid
	 */
	public static boolean isValid(String time) {
		if(StringUtils.isBlank(time)) {
			return true;
		}else {
			if(!StringUtils.endsWith(time, SUFFIX_ZULU)) {
				return false;
			}
			return StringUtils.chop(time).matches(TIME_FORMAT_REGEX);
		}
	}
}
