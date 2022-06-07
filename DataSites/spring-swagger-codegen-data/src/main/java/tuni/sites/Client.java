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
package tuni.sites;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tuni.sites.model.Sensor;


/**
 * Fourdeg client
 * 
 */
public class Client implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
	private static final String PARAMETER_BASIC = "Basic ";
	private static final String PATH_SENSORS = "/sensors";
	private static final char SEPARATOR_URI = '/';
	private CloseableHttpClient _client = null;
	private Gson _gson = null;
	private String _basic = null;
	private String _serviceUri = null;
	
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
	 * @param username
	 * @param password
	 * @throws IllegalArgumentException
	 */
	private void setToken(String username, String password) throws IllegalArgumentException {
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("Invalid username or password.");
		}
		
		_basic = PARAMETER_BASIC+Base64.encodeBase64String((username+":"+password).getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param serviceUri 
	 */
	public Client(String username, String password, String serviceUri) {
		setToken(username, password);
		_client = HttpClients.createDefault();
		_gson = new GsonBuilder().create();
		_serviceUri = serviceUri;
	}
	
	/**
	 * 
	 * @param request
	 */
	private void authorize(HttpUriRequest request) {
		request.setHeader(HttpHeaders.AUTHORIZATION, _basic);
	}
	
	/**
	 * 
	 * @param sensorId
	 * @return sensor of null if not found
	 */
	public Sensor getSensor(Long sensorId) {
		String url = _serviceUri+PATH_SENSORS+SEPARATOR_URI+sensorId;
		HttpGet get = new HttpGet(url);
		authorize(get);
		LOGGER.debug("Calling GET "+url);
		try(CloseableHttpResponse response = _client.execute(get)){
			int code = response.getCode();
			if(code < 200 || code >= 300) {
				LOGGER.error("Server responded: "+code);
				return null;
			}
			try(InputStreamReader r = new InputStreamReader(response.getEntity().getContent())){
				Sensor[] sensors = _gson.fromJson(r, Sensor[].class);
				return (ArrayUtils.isEmpty(sensors) ? null : sensors[0]);
			}
		}catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;		
	}
}
