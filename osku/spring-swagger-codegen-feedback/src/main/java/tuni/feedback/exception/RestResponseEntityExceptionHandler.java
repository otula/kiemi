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
package tuni.feedback.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * error handler
 * 
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger LOGGER = LogManager.getLogger(RestResponseEntityExceptionHandler.class);

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOGGER.debug(ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), status), status);
	}
	
	/**
	 * 
	 * @param ex
	 * @param request
	 * @return response
	 */
	@ExceptionHandler(value = { IdNotFoundException.class })
	protected ResponseEntity<ErrorResponse> handleIdNotFoundException(IdNotFoundException ex, WebRequest request) {
		LOGGER.debug(ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 
	 * @param ex
	 * @param request
	 * @return response
	 */
	@ExceptionHandler(value = { ForbiddenException.class })
	protected ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex, WebRequest request) {
		LOGGER.debug(ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
	}
	
	/**
	 * 
	 * @param ex
	 * @param request
	 * @return response
	 */
	@ExceptionHandler(value = { InvalidParameterException.class })
	protected ResponseEntity<ErrorResponse> handleInvalidParameterException(InvalidParameterException ex, WebRequest request) {
		LOGGER.debug(ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 
	 * @param ex
	 * @param request
	 * @return response
	 */
	@ExceptionHandler(value = { RequestRejectedException.class })
	protected ResponseEntity<ErrorResponse> handleRequestRejectedException(RequestRejectedException ex, WebRequest request) {
		LOGGER.debug(ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
}
