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
package tuni.data.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default error handler
 * 
 */
@RestController
public class ErrorHandler implements ErrorController {
	private static final Logger LOGGER = LogManager.getLogger(ErrorHandler.class);
	private static final String PATH = "/error";

	/**
	 * 
	 * @param request
	 * @param response
	 * @return error page
	 */
    @RequestMapping(value = PATH)
    public ErrorResponse error(HttpServletRequest request, HttpServletResponse response) {
    	HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    	try {
    		status = HttpStatus.valueOf(response.getStatus());
    	} catch (IllegalArgumentException ex) {
    		LOGGER.error(ex.getMessage(), ex);
    	}
        return new ErrorResponse(null, status);
    }
}
