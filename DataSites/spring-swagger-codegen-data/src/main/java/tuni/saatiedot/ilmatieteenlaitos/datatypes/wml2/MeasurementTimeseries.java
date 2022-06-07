/**
 * Copyright 2019 Tampere University
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

package tuni.saatiedot.ilmatieteenlaitos.datatypes.wml2;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import tuni.saatiedot.ilmatieteenlaitos.Definitions;

/**
 * 
 * 
 */
@Root(name = Definitions.XML_ELEMENT_WML_MEASUREMENT_TIMESERIES, strict = false)
public class MeasurementTimeseries {
	@Attribute(name = Definitions.XML_ATTRIBUTE_GML_ID)
	private String _id = null;
	@ElementList(inline = true)
	private List<Point> _points = null;

	/**
	 * 
	 * @return id
	 */
	public String getId() {
		return _id;
	}

	/**
	 * 
	 * @return ponts
	 */
	public List<Point> getPoints() {
		return _points;
	}
}
