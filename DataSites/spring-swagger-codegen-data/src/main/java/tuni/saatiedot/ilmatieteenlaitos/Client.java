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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.simpleframework.xml.transform.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuni.saatiedot.ilmatieteenlaitos.datatypes.gml.Position;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wfs.FeatureCollection;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wfs.Member;

/**
 * Client for access data from Ilmatieteenlaitos APIs
 * 
 * https://ilmatieteenlaitos.fi/latauspalvelun-pikaohje
 * 
 */
public class Client implements Closeable {
	private static final FastDateFormat DATETIME_FORMATTER = FastDateFormat.getInstance(Definitions.TIMESTAMP_FORMAT, TimeZone.getTimeZone("UTC"));
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
	private static final String URI_GET_FEATURE = Definitions.BASE_URI+Definitions.SEPARATOR_SERVICE+Definitions.PARAMETER_SERVICE+Definitions.SEPARATOR_PARAMETER_VALUE+Definitions.SERVICE_WFS+Definitions.SEPARATOR_PARAMETER+Definitions.PARAMETER_VERSION+Definitions.SEPARATOR_PARAMETER_VALUE+Definitions.VERSION+Definitions.SEPARATOR_PARAMETER+Definitions.PARAMETER_REQUEST+Definitions.SEPARATOR_PARAMETER_VALUE+Definitions.REQUEST_GET_FEATURE+Definitions.SEPARATOR_PARAMETER;
	private CloseableHttpClient _client = null;
	private Serializer _serializer = null;
	
	/**
	 * 
	 */
	public Client() {
		_client = HttpClients.createDefault();
		RegistryMatcher m = new RegistryMatcher();
		m.bind(Date.class, new TimeFormatter());
		m.bind(Position.class, new PositionConverter());

		_serializer = new Persister(m);
	}
	
	/**
	 * 
	 * @param place
	 * @param start if missing, but end is given, start = (end - one week)
	 * @param end if missing, but start is given end = now()
	 * @param parameters
	 * @param storedQueryId
	 * @return collection
	 */
	public FeatureCollection getFeatureCollection(String place, Date start, Date end, Set<String> parameters, String storedQueryId) {
		if(start == null && end == null) {
			return getPartialFeatureCollection(place, start, end, parameters, storedQueryId);
		}
		long startms = (start == null ? end.getTime()-Definitions.MAX_INTERVAL : start.getTime());
		long lastms = (end == null ? System.currentTimeMillis() : end.getTime());
		
		if(lastms-startms <= Definitions.MAX_INTERVAL) {
			return getPartialFeatureCollection(place, start, end, parameters, storedQueryId);
		}
		
		ArrayList<Member> members = new ArrayList<>();
		do {
			long endms = startms + Definitions.MAX_INTERVAL;
			if(endms > lastms) {
				endms = lastms;
			}
			FeatureCollection c = getPartialFeatureCollection(place, new Date(startms), new Date(endms), parameters, storedQueryId);
			if(c != null) {
				List<Member> m = c.getMembers();
				if(m != null && !m.isEmpty()) {
					members.addAll(m);
				}
			}
			startms = endms;
		} while (startms < lastms);
		
		FeatureCollection fc = new FeatureCollection();
		fc.setMembers(members);
		return fc;
	}
	
	/**
	 * 
	 * @param place
	 * @param start optional start date
	 * @param end optional end date
	 * @param parameters optional set of parameters
	 * @param storedQueryId 
	 * @return collection
	 */
	private FeatureCollection getPartialFeatureCollection(String place, Date start, Date end, Set<String> parameters, String storedQueryId) {
		StringBuilder uri = new StringBuilder();
		uri.append(URI_GET_FEATURE);
		uri.append(Definitions.PARAMETER_PLACE);
		uri.append(Definitions.SEPARATOR_PARAMETER_VALUE);
		uri.append(place);
		
		uri.append(Definitions.SEPARATOR_PARAMETER);
		uri.append(Definitions.PARAMETER_STORED_QUERY_ID);
		uri.append(Definitions.SEPARATOR_PARAMETER_VALUE);
		uri.append(storedQueryId);
		
		if(start != null) {
			uri.append(Definitions.SEPARATOR_PARAMETER);
			uri.append(Definitions.PARAMETER_START_TIME);
			uri.append(Definitions.SEPARATOR_PARAMETER_VALUE);
			DATETIME_FORMATTER.format(start, uri);
		}
		
		if(end != null) {
			uri.append(Definitions.SEPARATOR_PARAMETER);
			uri.append(Definitions.PARAMETER_END_TIME);
			uri.append(Definitions.SEPARATOR_PARAMETER_VALUE);
			DATETIME_FORMATTER.format(end, uri);
		}
		
		if(parameters != null && !parameters.isEmpty()) {
			uri.append(Definitions.SEPARATOR_PARAMETER);
			uri.append(Definitions.PARAMETER_PARAMETERS);
			uri.append(Definitions.SEPARATOR_PARAMETER_VALUE);
			Iterator<String> iter = parameters.iterator();
			uri.append(iter.next());
			while(iter.hasNext()) {
				uri.append(Definitions.SEPARATOR_VALUE);
				uri.append(iter.next());
			}
		}
		
		String uriString = uri.toString();
		LOGGER.debug("Callig GET "+uriString);
		try(CloseableHttpResponse response = _client.execute(new HttpGet(uriString))){
			int code = response.getCode();
			if(code != 200) {
				LOGGER.error("Server responded: "+code+" "+response.getReasonPhrase());
			}else {
				return _serializer.read(FeatureCollection.class, response.getEntity().getContent());
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public void close() {
		try {
			_client.close();
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 
	 * 
	 */
	private static class TimeFormatter implements Transform<Date> {
		@Override
		public Date read(String value) throws Exception {
			return (StringUtils.isBlank(value) ? null : DATETIME_FORMATTER.parse(value));
		}

		@Override
		public String write(Date value) throws Exception {
			return (value == null ? null : DATETIME_FORMATTER.format(value));
		}
	} // class TimeFormatter
	
	/**
	 * 
	 *
	 */
	private static class PositionConverter implements Transform<Position> {
		private static final char SEPARATOR_COORDINATES = ' ';

		@Override
		public Position read(String value) throws Exception {
			String[] parts = StringUtils.split(value, SEPARATOR_COORDINATES);
			return (ArrayUtils.isEmpty(parts) ? null :new Position(Double.valueOf(parts[0]), Double.valueOf(parts[1])));
		}

		@Override
		public String write(Position value) throws Exception {
			return (value == null ? null : value.getLatitude().toString()+SEPARATOR_COORDINATES+value.getLongitude().toString());
		}
	} // class PositionConverter
}
