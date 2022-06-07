/**
 * Copyright 2020 Tampere University
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
package tuni.data.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import tuni.data.adapters.Adapter;
import tuni.data.adapters.datatypes.Data;
import tuni.data.adapters.fourdeg.FourdegAdapter;
import tuni.data.adapters.ilmatieteenlaitos.IlmatieteenlaitosAdapter;
import tuni.data.adapters.influxdb.aranet.AranetAdapter;
import tuni.data.adapters.influxdb.ruuvi.RuuviAdapter;
import tuni.data.adapters.ouman.OumanAdapter;
import tuni.data.adapters.porienergia.PoriEnergiaAdapter;
import tuni.data.exception.IdNotFoundException;
import tuni.data.exception.InvalidParameterException;
import tuni.sites.model.Sensor;
import tuni.sites.model.Sensor.ServiceType;

/**
 * 
 * 
 */
@Service
public class DataService implements InitializingBean, DisposableBean {
	@Autowired
	private Environment _environment = null;
	@Autowired
	private FourdegAdapter _fourdegAdapter = null;
	@Autowired
	private OumanAdapter _oumanAdapter = null;
	@Autowired
	private PoriEnergiaAdapter _poriEnergiaAdapter = null;
	@Autowired
	private RuuviAdapter _ruuviAdapter = null;
	@Autowired
	private AranetAdapter _aranetAdapter = null;
	@Autowired
	private IlmatieteenlaitosAdapter _ilmatieteenlaitosAdapter = null;
	private tuni.sites.Client _sClient = null;

	/**
	 * 
	 * @param sensorId validity of sensor id is checked from the configured sites service
	 * @param from
	 * @param to
	 * @param params 
	 * @return list of data
	 */
	public List<? extends Data> getData(Long sensorId, Date from, Date to, Map<String, String> params) {
		return getData(getSensor(sensorId), from, to, params);
	}
	
	/**
	 * 
	 * @param sensorId
	 * @return sensor
	 * @throws IdNotFoundException 
	 */
	public Sensor getSensor(Long sensorId) throws IdNotFoundException {
		Sensor sensor = _sClient.getSensor(sensorId);
		if(sensor == null) {
			throw new IdNotFoundException("Sensor, id "+sensorId+" was not found.");
		}
		return sensor;
	}
	
	/**
	 * 
	 * @param sensor
	 * @param from
	 * @param to
	 * @param params 
	 * @return list of data
	 * @throws IdNotFoundException 
	 */
	public List<? extends Data> getData(Sensor sensor, Date from, Date to, Map<String, String> params) throws IdNotFoundException {
		return getAdapter(sensor.getServiceType()).getData(sensor, from, to, params);
	}
	
	/**
	 * 
	 * @param type
	 * @return adapter
	 * @throws InvalidParameterException 
	 */
	public Adapter getAdapter(ServiceType type) throws InvalidParameterException {
		switch(type) {
			case fourdeg:
				return _fourdegAdapter;
			case ruuvi:
				return _ruuviAdapter;
			case aranet:
				return _aranetAdapter;
			case ouman:
				return _oumanAdapter;
			case porienergia:
				return _poriEnergiaAdapter;
			case fmi:
				return _ilmatieteenlaitosAdapter;
			default:
				throw new InvalidParameterException("Unsupported service type: "+type.toString());
		}
	}

	@Override
	public void destroy() {
		if(_sClient != null) {
			_sClient.close();
			_sClient = null;
		}
	}

	@Override
	public void afterPropertiesSet() {
		_sClient = new tuni.sites.Client(_environment.getProperty(Definitions.PROPERTY_SITES_USERNAME), _environment.getProperty(Definitions.PROPERTY_SITES_PASSWORD), _environment.getProperty(Definitions.PROPERTY_SITES_URI));
	}
}
