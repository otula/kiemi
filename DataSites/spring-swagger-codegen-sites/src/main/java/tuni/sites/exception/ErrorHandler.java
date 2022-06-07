package tuni.sites.exception;

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
