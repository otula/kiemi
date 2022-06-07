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
package tuni.sites.security;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import tuni.sites.security.model.Definitions;

/**
 * 
 * 
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger LOGGER = LogManager.getLogger(WebSecurityConfig.class);
	@Autowired
	private UserService _userService = null;
	
	@Override
 	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.debug("Configuring HTTPSecurity...");
		// To disable cors and csrf: 
		http/*.requiresChannel().anyRequest().requiresSecure().and()*/.cors().and().csrf().disable().authorizeRequests().antMatchers("/sites/**", "/layers/**", "/sensors/**").hasAuthority(Definitions.ROLE_USER).and().httpBasic();
 		//NOTE; csrf is disabled to allow easier testing of web pages, it should be enabled on production
		//http.csrf().disable().authorizeRequests().antMatchers("/sites/**").hasRole("USER").and().httpBasic();
 	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOGGER.debug("Configuring authentication manager...");
		DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
		dap.setPasswordEncoder(new BCryptPasswordEncoder());
		dap.setUserDetailsService(_userService);
		auth.authenticationProvider(dap);
	}
	
	/**
	 * TODO: this is a temporary fix for allowing cross-domain requests from sites service hosted in 44443 port, fix this when properly deploying!
	 * 
	 * @return url based configuration that allows cross-origin requests
	 */
	@Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
