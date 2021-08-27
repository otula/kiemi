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
package tuni.lorawan.sensors.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * 
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
	@Autowired
	private UserService _userService = null;
	
	@Override
 	protected void configure(HttpSecurity http) throws Exception {
		LOG.debug("Configuring HTTPSecurity...");
		// To disable cors and csrf: http.csrf().disable().cors().and().authorizeRequests().antMatchers("/tasks/**", "/projects/**").hasRole("USER").and().httpBasic();
 		//NOTE; csrf is disabled to allow easier testing of web pages, it should be enabled on production
		http.csrf().disable().authorizeRequests().antMatchers("/data/**", "/nodes/**").hasRole("USER").and().httpBasic();
 	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOG.debug("Configuring authentication manager...");
		DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
		dap.setPasswordEncoder(new PasswordEncoder() { // override the default password encoder with an encoder that basically does nothing to allow plain-text password for this simple use case
			
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return encodedPassword.equals(rawPassword);
			}
			
			@Override
			public String encode(CharSequence rawPassword) {
				return rawPassword.toString();
			}
		});
		dap.setUserDetailsService(_userService);
		auth.authenticationProvider(dap);
	}
	
//	/**
//	 * 
//	 * @return url based configuration that allows cross-origin requests
//	 */
//	@Bean
//    UrlBasedCorsConfigurationSource corsConfigurationSource()
//    {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://10.1.1.2"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST", "DELETE"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
