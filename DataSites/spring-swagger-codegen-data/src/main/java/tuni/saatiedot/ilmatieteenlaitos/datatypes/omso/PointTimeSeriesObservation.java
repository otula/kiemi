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

package tuni.saatiedot.ilmatieteenlaitos.datatypes.omso;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import tuni.saatiedot.ilmatieteenlaitos.Definitions;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.om.FeatureOfInterest;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.om.Result;

/**
 * 
 * 
 */
@Root(name = Definitions.XML_ELEMENT_OMSO_POINT_TIME_SERIES_OBSERVATION, strict = false)
public class PointTimeSeriesObservation {
	@Element(name = Definitions.XML_ELEMENT_OM_FEATURE_OF_INTEREST)
	private FeatureOfInterest _featureOfInterest = null;
	@Element(name = Definitions.XML_ELEMENT_OM_RESULT)
	private Result _result = null;
	
	/**
	 * 
	 * @return feature of interest
	 */
	public FeatureOfInterest getFeatureOfInterest() {
		return _featureOfInterest;
	}
	
	/**
	 * 
	 * @return result
	 */
	public Result getResult() {
		return _result;
	}
}
