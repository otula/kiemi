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

package tuni.saatiedot.ilmatieteenlaitos.datatypes.om;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import tuni.saatiedot.ilmatieteenlaitos.Definitions;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wml2.MeasurementTimeseries;

/**
 * 
 *
 */
@Root(name = Definitions.XML_ELEMENT_OM_RESULT, strict = false)
public class Result {
	@Element(name = Definitions.XML_ELEMENT_WML_MEASUREMENT_TIMESERIES)
	private MeasurementTimeseries _measurementTimeseries = null;

	/**
	 * 
	 * @return the time series
	 */
	public MeasurementTimeseries getMeasurementTimeseries() {
		return _measurementTimeseries;
	}
}
