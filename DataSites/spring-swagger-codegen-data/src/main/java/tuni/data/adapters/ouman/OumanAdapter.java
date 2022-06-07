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
package tuni.data.adapters.ouman;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import tuni.data.adapters.Adapter;
import tuni.data.adapters.datatypes.Data;
import tuni.data.exception.InvalidParameterException;
import tuni.sites.model.Sensor;
import tuni.sites.model.Sensor.ServiceType;

/**
 * Implementation removed from public repository.
 * 
 */
@Component
public class OumanAdapter implements Adapter {

	@Override
	public List<Data> getData(Sensor sensor, Date from, Date to, Map<String, String> params) throws InvalidParameterException {
		throw new InvalidParameterException("Type "+ServiceType.ouman.name()+" not supported by this service.");
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.ouman;
	}

	@Override
	public Class<?> getDataClass() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
