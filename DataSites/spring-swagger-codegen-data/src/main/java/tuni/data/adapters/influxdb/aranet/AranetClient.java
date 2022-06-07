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
package tuni.data.adapters.influxdb.aranet;

import java.io.Closeable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import tuni.data.adapters.influxdb.aranet.datatypes.AranetData;

/**
 * 
 * 
 */
public class AranetClient implements Closeable {
	private static final Logger LOGGER = LogManager.getLogger(AranetClient.class);
	private static final FastDateFormat DATETIME_FORMATTER = FastDateFormat.getInstance(Definitions.DATE_FORMAT);
	private static final char SEPARATOR_MINUS = '-';
	private static final char SEPARATOR_MS = '.';
	private static final char SEPARATOR_PLUS = '+';
	private static final char SEPARATOR_Z = 'Z';
	private String _retentionPolicy = null;
	private String _tag = null;
	private InfluxDB _influxDB = null;

	/**
	 * 
	 * @param uri
	 * @param username
	 * @param password
	 * @param retentionPolicy
	 * @param tag
	 */
	public AranetClient(String uri, String username, String password, String retentionPolicy, String tag) {
		_tag = tag;
		_retentionPolicy = retentionPolicy;
		_influxDB = InfluxDBFactory.connect(uri, username, password);
	}
	
	@Override
	public void close() {
		if(_influxDB != null) {
			_influxDB.close();
			_influxDB = null;
		}
	}
	
	/**
	 * If from and to are null, this will only return the newest result
	 * @param database 
	 * @param measurement 
	 * 
	 * @param tagName
	 * @param from
	 * @param to
	 * @return data
	 */
	public List<AranetData> getData(String database, String measurement, String tagName, Date from, Date to) {
		return getData(database, measurement, tagName, from, to, _retentionPolicy);
	}
	
	/**
	 * If from and to are null, this will only return the newest result
	 * @param database 
	 * @param measurement 
	 * 
	 * @param tagName
	 * @param from
	 * @param to
	 * @param policy use this policy instead of the configured one
	 * @return data
	 */
	public List<AranetData> getData(String database, String measurement, String tagName, Date from, Date to, String policy) {
		ArrayList<AranetData> data = new ArrayList<>();
		Query q = null;
		if(from == null && to == null) {
			q = new Query("select * from \""+policy+"\".\""+measurement+"\" where \""+_tag+"\"='"+tagName+"' order by desc limit 1", database);
		}else if(from != null) {
			if(to != null) { // from != null && to != null
				q = new Query("select * from \""+policy+"\".\""+measurement+"\" where \""+_tag+"\"='"+tagName+"' and time>="+(from.getTime()*1000000)+" and time<="+(to.getTime()*1000000)+" order by desc", database);
			}else { // from != null && to == null
				q = new Query("select * from \""+policy+"\".\""+measurement+"\" where \""+_tag+"\"='"+tagName+"' and time>="+(from.getTime()*1000000)+" order by desc", database);
			}
		}else { // from == null && to != null
			q = new Query("select * from \""+policy+"\".\""+measurement+"\" where \""+_tag+"\"='"+tagName+"' and time<="+(to.getTime()*1000000)+" order by desc", database);
		}
		
		QueryResult results = _influxDB.query(q);
		List<Result> resultList = results.getResults();
		if(resultList == null || resultList.isEmpty()) {
			return data;
		}
		for(Result r : resultList) {
			List<Series> series = r.getSeries();
			if(series == null || series.isEmpty()) {
				continue;
			}
			for(Series s : series) {
				List<String> columns = s.getColumns();
				if(columns == null || columns.isEmpty()) {
					LOGGER.warn("No columns for tag, name: "+tagName);
					continue;
				}

				List<List<Object>> values = s.getValues();
				if(values == null || values.isEmpty()) {
					continue;
				}

				for(List<Object> objects : values) {
					if(objects == null || objects.isEmpty()) {
						continue;
					}else if(objects.size() != columns.size()) {
						LOGGER.warn("Column count does not match value count, tag name: "+tagName);
						continue;
					}
					addData(data, columns, objects);
				}
			}
		}
		return data;
	}
	
	/**
	 * 
	 * @param column
	 * @param aranet
	 * @param value
	 */
	private void processColumn(String column, AranetData aranet, Object value) {
		switch (column) {
			case Definitions.JSON_HUMIDITY:
				aranet.setHumidity((Double) value);
				break;
			case Definitions.JSON_NAME:
				aranet.setName((String) value);
				break;
			case Definitions.JSON_PRESSURE:
				aranet.setPressure(((Double) value) / 100); // convert Pa -> hPa
				break;
			case Definitions.JSON_RSSI:
				aranet.setRssi((Double) value);
				break;
			case Definitions.JSON_TEMPERATURE:
				aranet.setTemperature((Double) value);
				break;
			case Definitions.JSON_CO2:
				aranet.setCO2((Double) value);
				break;
			case Definitions.JSON_TIME:
				try {
					String temp = (String) value;
					// the influxdb timestamp can have any precision of "milliseconds" (e.g. 2020-11-05T13:13:01.864595762Z), making the string problematic to parse, so simply remove the milliseconds
					if(StringUtils.contains(temp, SEPARATOR_Z)) {
						temp = StringUtils.split(temp, SEPARATOR_MS)[0]+SEPARATOR_Z;
					} else {
						int separatorIndex = StringUtils.indexOf(temp, SEPARATOR_PLUS);
						if(separatorIndex < 0) {
							separatorIndex = StringUtils.indexOf(temp, SEPARATOR_MINUS);
							if(separatorIndex < 0) {
								throw new ParseException("Invalid timestring: "+temp, -1);
							}
						}
						temp = StringUtils.split(temp, SEPARATOR_MS)[0]+StringUtils.substring(temp, separatorIndex);
					}
					aranet.setTime(DATETIME_FORMATTER.parse(temp));
				} catch (ParseException ex) {
					LOGGER.error(ex.getMessage(), ex);
					return;
				}
				break;
			default:
				break; // silently ignore unknown columns
		} // switch
	}
	
	/**
	 * 
	 * @param data
	 * @param columns
	 * @param values
	 */
	private void addData(List<AranetData> data, List<String> columns, List<Object> values) {
		int index = 0;
		AranetData aranet = new AranetData();
		for(String column : columns) {
			column = StringUtils.removeStart(column, Definitions.JSON_PREFIX_MEAN);
			Object value = values.get(index);
			if(value != null) {
				processColumn(column, aranet, value);
			} // if
			++index;
		} // for
		data.add(aranet);
	}
}
