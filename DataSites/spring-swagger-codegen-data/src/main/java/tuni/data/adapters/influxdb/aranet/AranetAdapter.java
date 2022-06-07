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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tuni.data.adapters.Adapter;
import tuni.data.adapters.influxdb.aranet.datatypes.AranetData;
import tuni.data.exception.InvalidParameterException;
import tuni.sites.model.Sensor;
import tuni.sites.model.Sensor.ServiceType;

/**
 * 
 * 
 */
@Component
public class AranetAdapter implements Adapter, InitializingBean, DisposableBean {
	private static final char SEPARATOR_PATH =  '/';
	private AranetClient _aClient = null;
	@Autowired
	private AranetProperties _properties = null;
	

	@Override
	public void destroy() throws Exception {
		if(_aClient != null) {
			_aClient.close();
			_aClient = null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		_aClient = new tuni.data.adapters.influxdb.aranet.AranetClient(_properties.getUri(), _properties.getUsername(), _properties.getPassword(), _properties.getRetentionPolicy(), _properties.getTag());
	}

	@Override
	public List<AranetData> getData(Sensor sensor, Date from, Date to, Map<String, String> params) throws InvalidParameterException {
		String eId = sensor.getExternalId();
		String database = null;
		String measurement = null;
		String[] parts = StringUtils.split(eId, SEPARATOR_PATH);
		if(parts.length == 3) {
			database = parts[0];
			measurement = parts[1];
			eId = parts[2];
		}else if(parts.length == 1) {
			eId = parts[0];
			database = _properties.getDatabase();
			measurement = _properties.getMeasurement();
		}else {
			throw new InvalidParameterException("Invalid external identifier: "+eId);
		}
		String policy = null;
		if(params != null && (policy = params.get(tuni.data.adapters.influxdb.ruuvi.Definitions.PARAMETER_RETENTION_POLICY)) != null) {
			return _aClient.getData(database, measurement, eId, from, to, policy);
		}else {
			return _aClient.getData(database, measurement, eId, from, to);
		}
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.aranet;
	}

	@Override
	public Class<?> getDataClass() {
		return AranetData.class;
	}
}
