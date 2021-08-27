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
package tuni.lorawan.sensors.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 * common utils for sensors
 * 
 */
public final class Utils {
	
	/**
	 * 
	 * @param UUIDs
	 * @return the given UUID list as string list or null if null/empty list was
	 *         passed
	 */
	public static List<String> toStringList(List<UUID> UUIDs) {
		ArrayList<String> strings = null;
		if (UUIDs != null && !UUIDs.isEmpty()) {
			strings = new ArrayList<>(UUIDs.size());
			for (UUID uuid : UUIDs) {
				if(uuid != null) {
					strings.add(uuid.toString());
				}
			}
		}
		return strings;
	}
	
	/**
	 * 
	 */
	private Utils() {
		// nothing needed
	}
}
