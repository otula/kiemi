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

package tuni.saatiedot.ilmatieteenlaitos.datatypes.gml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import tuni.saatiedot.ilmatieteenlaitos.Definitions;

/**
 * 
 * 
 */
@Root(name = Definitions.XML_ELEMENT_GML_MULTIPOINT, strict = false)
public class MultiPoint {
	@Element(name = Definitions.XML_ELEMENT_GML_POINT_MEMBERS)
	private PointMembers _members = null;

	/**
	 * @return the members
	 */
	public PointMembers getMembers() {
		return _members;
	}
}
