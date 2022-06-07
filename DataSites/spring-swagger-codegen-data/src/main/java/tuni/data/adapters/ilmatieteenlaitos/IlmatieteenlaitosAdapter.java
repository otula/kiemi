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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tuni.data.adapters.Adapter;
import tuni.data.adapters.ilmatieteenlaitos.datatypes.IlmatieteenlaitosData;
import tuni.saatiedot.ilmatieteenlaitos.Client;
import tuni.saatiedot.ilmatieteenlaitos.datatypes.wfs.FeatureCollection;
import tuni.sites.model.Sensor;
import tuni.sites.model.Sensor.ServiceType;

/**
 * 
 * 
 */
@Component
public class IlmatieteenlaitosAdapter implements Adapter, InitializingBean, DisposableBean {
	private Client _fmiClient = null;
	@Autowired
	private IlmatieteenlaitosProperties _properties = null;

	@Override
	public void destroy() throws Exception {
		if(_fmiClient != null) {
			_fmiClient.close();
			_fmiClient = null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		_fmiClient = new Client();
	}

	@Override
	public List<IlmatieteenlaitosData> getData(Sensor sensor, Date from, Date to, Map<String, String> params) {
		boolean onlyNewest = false;
		if(from != null) {
			if(to == null) {
				long toTime = from.getTime()+tuni.data.adapters.ilmatieteenlaitos.Definitions.NO_DATES_RANGE;
				long now = System.currentTimeMillis();
				if(toTime > now) {
					to = new Date(now);
				}else {
					to = new Date(toTime);
				}
			}
		}else if(to != null) {
			if(from == null) {
				long fromTime = to.getTime()-tuni.data.adapters.ilmatieteenlaitos.Definitions.NO_DATES_RANGE;
				if(fromTime < 0) {
					from = new Date(0);
				}else {
					from = new Date(fromTime);
				}
			}
		}else { // from == null && to == null
			onlyNewest = true;
			to = new Date();
			from = new Date(to.getTime()-tuni.data.adapters.ilmatieteenlaitos.Definitions.NO_DATES_RANGE);
		}
		
		FeatureCollection fc = _fmiClient.getFeatureCollection(sensor.getExternalId(), from, to, _properties.getParameters(), _properties.getStoredQueryId());
		DataCombiner dc = new DataCombiner();
		dc.combine(fc);
		List<IlmatieteenlaitosData> data = dc.generate();
		if(onlyNewest && !data.isEmpty()) {
			IlmatieteenlaitosData d = data.iterator().next();
			data.clear();
			data.add(d);
		}
		return data;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.fmi;
	}

	@Override
	public Class<?> getDataClass() {
		return IlmatieteenlaitosData.class;
	}
}
