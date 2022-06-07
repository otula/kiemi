/**
 * Copyright 2022 Tampere University
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
package tuni.data.adapters.ilmatieteenlaitos;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuni.data.adapters.ilmatieteenlaitos.datatypes.IlmatieteenlaitosData;
import tuni.saatiedot.ilmatieteenlaitos.Definitions;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.gml.MultiPoint;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.gml.Point;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.gml.PointMembers;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.gml.Position;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.om.FeatureOfInterest;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.om.Result;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.omso.PointTimeSeriesObservation;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.sams.SFSpatialSamplingFeature;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.sams.Shape;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wfs.FeatureCollection;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wfs.Member;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wml2.MeasurementTVP;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wml2.MeasurementTimeseries;

/**
 * 
 * 
 */
public class DataCombiner {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataCombiner.class);
	private HashMap<Point, ListOrderedMap<Date, HashMap<String, Object>>> _data = new HashMap<>();
	
	/**
	 * 
	 * @param fc
	 */
	public void combine(FeatureCollection fc) {
		if(fc == null) {
			LOGGER.debug("Empty collection.");
			return;
		}
		List<Member> members = fc.getMembers();
		if(members == null || members.isEmpty()) {
			LOGGER.debug("No "+Definitions.XML_ELEMENT_WFS_MEMBER);
			return;
		}
		
		for(Member m : members) {
			PointTimeSeriesObservation ptso = m.getPointSeriesObservation();
			if(ptso == null) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_OMSO_POINT_TIME_SERIES_OBSERVATION);
				continue;
			}
			FeatureOfInterest foi = ptso.getFeatureOfInterest();
			if(foi == null) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_OM_FEATURE_OF_INTEREST);
				continue;
			}
			SFSpatialSamplingFeature ssf = foi.getSFSamplingFeature();
			if(ssf == null) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_SAMS_SF_SPATIAL_SAMPLING_FEATURE);
				continue;
			}
			Shape shape = ssf.getShape();
			if(shape == null) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_SAMS_SHAPE);
				continue;
			}
			Point point = shape.getPoint();
			if(point == null) {
				MultiPoint mp = shape.getMultiPoint();
				if(mp == null) {
					LOGGER.debug("No "+Definitions.XML_ELEMENT_GML_POINT+" or "+Definitions.XML_ELEMENT_GML_MULTIPOINT);
					continue;
				}
				PointMembers pm = mp.getMembers();
				List<Point> points = null;
				if(pm == null || (points = pm.getPoints()) == null || points.isEmpty()) {
					LOGGER.debug("Empty "+Definitions.XML_ELEMENT_GML_MULTIPOINT);
					continue;
				}
				if(points.size() > 1) {
					LOGGER.debug(Definitions.XML_ELEMENT_GML_MULTIPOINT+" has more than one point, using the first one.");
				}
				point = points.get(0);
			}
			Position position = point.getPosition();
			LOGGER.debug("Found point: "+point.getName()+", latitude: "+position.getLatitude()+", longitude: "+position.getLongitude());
			ListOrderedMap<Date, HashMap<String, Object>> map = _data.get(point);
			if(map == null) {
				map = new ListOrderedMap<>();
				_data.put(point, map);
			}
			
			Result result = ptso.getResult();
			if(result == null) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_OM_RESULT);
				continue;
			}
			MeasurementTimeseries series = result.getMeasurementTimeseries();
			if(series == null) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_WML_MEASUREMENT_TIMESERIES);
				continue;
			}
			String[] idParts = StringUtils.split(series.getId(), '-');
			String key = idParts[idParts.length-1];
			List<tuni.saatiedot.ilmatieteenlaitos.datatypes.wml2.Point> points = series.getPoints();
			if(points == null || points.isEmpty()) {
				LOGGER.debug("No "+Definitions.XML_ELEMENT_WML2_POINT);
				continue;
			}
			for(tuni.saatiedot.ilmatieteenlaitos.datatypes.wml2.Point p : points) {
				MeasurementTVP measurement = p.getMeasurementTVP();
				if(measurement == null) {
					LOGGER.debug("No "+Definitions.XML_ELEMENT_WML2_MEASUREMENT_TVP);
					continue;
				}
				Double value = measurement.getValue();
				if(value == null || value.isNaN() || value.isInfinite()) { // silently ignore NaN numbers
					continue;
				}				
				Date time = measurement.getTime();
				HashMap<String, Object> d = getData(map, time);
				d.put(key, value);
			}
		}
	}
	
	/**
	 * 
	 * @param map
	 * @param time
	 * @return the data from the map if one exists or creates a new one if not found
	 */
	private HashMap<String, Object> getData(ListOrderedMap<Date, HashMap<String, Object>> map, Date time) {
		HashMap<String, Object> d = map.get(time);
		if(d == null) {
			d = new HashMap<>();
			int index = 0;
			boolean wasNotAdded = true;
			for(Entry<Date, HashMap<String, Object>> e : map.entrySet()) {
				if(time.before(e.getKey())) {
					map.put(index, time, d);
					break;
				}
				++index;
			}
			if(wasNotAdded) {
				map.put(time, d);
			}
		}
		return d;
	}
	
	/**
	 * 
	 * @return map as array
	 */
	public List<IlmatieteenlaitosData> generate() {
		LinkedList<IlmatieteenlaitosData> ild = new LinkedList<>();
		
		for(Entry<Point, ListOrderedMap<Date, HashMap<String, Object>>> ep : _data.entrySet()) {
			String place = ep.getKey().getName();
			for(Entry<Date, HashMap<String, Object>> ed : ep.getValue().entrySet()) {
				IlmatieteenlaitosData d = new IlmatieteenlaitosData();
				d.setPlace(place);
				d.setTime(ed.getKey());
				boolean dataAdded = false;
				for(Entry<String, Object> ek : ed.getValue().entrySet()) {
					String key = ek.getKey();
					if(tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_TIMESTAMP.equals(key)) {
						continue;
					}
					
					try {
						Double value = (Double) ek.getValue();
						if(value.isNaN()) { // The ilmatieteenlaitos api sometimes returns NaN values, ignore them
							continue;
						}
						
						switch(key) {
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_CLOUD_COVER:
								d.setCloudCover(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_PRESSURE_SEA_LEVEL:
								d.setPressure(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_RAIN_INTENSITY_10MIN:
								d.setRainIntensity(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_RH:
								d.setHumidity(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_RAIN_1H:
								d.setRain(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_TEMPERATURE_2_MIN:
								d.setTemperature(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_WIND_DIRECTION_10MIN:
								d.setWindDirection(value);
								dataAdded = true;
								break;
							case tuni.data.adapters.ilmatieteenlaitos.Definitions.FMI_WIND_SPEED_10MIN:
								d.setWindSpeed(value);
								dataAdded = true;
								break;
							default:
								LOGGER.debug("Ignored unknown data key: "+key);
								break;
						} // switch
					
					} catch (ClassCastException ex) {
						LOGGER.debug(ex.getMessage(), ex);
					}
				}
				if(dataAdded) {
					ild.addFirst(d);
				}
			}
		}
		
		return ild;
	}
}
