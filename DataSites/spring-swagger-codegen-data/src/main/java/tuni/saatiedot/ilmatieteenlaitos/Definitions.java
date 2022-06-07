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
package tuni.saatiedot.ilmatieteenlaitos;

/**
 * 
 * definitions
 */
public final class Definitions {
	/* base URIs */
	/** base URI for Ilmatieteenlaitos API */
	public static final String BASE_URI = "http://opendata.fmi.fi/wfs/fin";
	
	/* parameters */
	/** URI parameter */
	public static final String PARAMETER_END_TIME = "endtime";
	/** URI parameter */
	public static final String PARAMETER_PARAMETERS = "parameters";
	/** URI parameter */
	public static final String PARAMETER_PLACE = "place";
	/** URI parameter */
	public static final String PARAMETER_REQUEST = "request";
	/** URI parameter */
	public static final String PARAMETER_SERVICE = "service";
	/** URI parameter */
	public static final String PARAMETER_START_TIME = "starttime";
	/** URI parameter */
	public static final String PARAMETER_STORED_QUERY_ID = "storedquery_id";
	/** URI parameter */
	public static final String PARAMETER_VERSION = "version";

	/* request types */
	/** request type for get feature request */
	public static final String REQUEST_GET_FEATURE = "GetFeature";
	
	/* separators */
	/** URL separator */
	public static final char SEPARATOR_SERVICE = '?';
	/** URL separator */
	public static final char SEPARATOR_PARAMETER = '&';
	/** URL separator */
	public static final char SEPARATOR_PARAMETER_VALUE = '=';
	/** URL separator */
	public static final char SEPARATOR_VALUE = ',';
	
	/* service types */
	/** WFS Service name */
	public static final String SERVICE_WFS = "WFS";
	
	/* common */
	/** timestamp format */
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	/** Ilmatieteenlaitos API version */
	public static final String VERSION = "2.0.0";
	
	/* XML attributes */
	/** XML attribute name */
	public static final String XML_ATTRIBUTE_GML_ID = "id";
	
	/* XML elements */
	/** XML element name */
	public static final String XML_ELEMENT_GML_MULTIPOINT = "MultiPoint";
	/** XML element name */
	public static final String XML_ELEMENT_GML_NAME = "name";
	/** XML element name */
	public static final String XML_ELEMENT_GML_POINT = "Point";
	/** XML element name */
	public static final String XML_ELEMENT_GML_POINT_MEMBERS = "pointMembers";
	/** XML element name */
	public static final String XML_ELEMENT_GML_POS = "pos";
	/** XML element name */
	public static final String XML_ELEMENT_OM_FEATURE_OF_INTEREST = "featureOfInterest";
	/** XML element name */
	public static final String XML_ELEMENT_OM_RESULT = "result";
	/** XML element name */
	public static final String XML_ELEMENT_OMSO_POINT_TIME_SERIES_OBSERVATION = "PointTimeSeriesObservation";
	/** XML element name */
	public static final String XML_ELEMENT_SAMS_SF_SPATIAL_SAMPLING_FEATURE = "SF_SpatialSamplingFeature";
	/** XML element name */
	public static final String XML_ELEMENT_SAMS_SHAPE = "shape";
	/** XML element name */
	public static final String XML_ELEMENT_WFS_FEATURE_COLLECTION = "FeatureCollection";
	/** XML element name */
	public static final String XML_ELEMENT_WFS_MEMBER = "member";
	/** XML element name */
	public static final String XML_ELEMENT_WML_MEASUREMENT_TIMESERIES = "MeasurementTimeseries";
	/** XML element name */
	public static final String XML_ELEMENT_WML2_POINT = "point";
	/** XML element name */
	public static final String XML_ELEMENT_WML2_MEASUREMENT_TVP = "MeasurementTVP";
	/** XML element name */
	public static final String XML_ELEMENT_WML2_TIME = "time";
	/** XML element name */
	public static final String XML_ELEMENT_WML2_VALUE = "value";
	
	/* common */
	/** maximum interval for result retrieval */
	public static final long MAX_INTERVAL = 604800000l; // 7 days in ms
	
	/**
	 * 
	 */
	private Definitions() {
		// nothing needed
	}
}
