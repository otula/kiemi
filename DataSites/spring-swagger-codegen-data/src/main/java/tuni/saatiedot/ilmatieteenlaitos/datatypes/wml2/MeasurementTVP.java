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

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import tuni.saatiedot.ilmatieteenlaitos.Definitions;

/**
 * 
 * 
 */
@Root(name = Definitions.XML_ELEMENT_WML2_MEASUREMENT_TVP, strict = false)
public class MeasurementTVP {
	@Element(name = Definitions.XML_ELEMENT_WML2_TIME)
	private Date _time = null;
	@Element(name = Definitions.XML_ELEMENT_WML2_VALUE)
	private Double _value = null;
	
	/**
	 * 
	 * @return time
	 */
	public Date getTime() {
		return _time;
	}
	
	/**
	 * 
	 * @return value
	 */
	public Double getValue() {
		return _value;
	}
}
