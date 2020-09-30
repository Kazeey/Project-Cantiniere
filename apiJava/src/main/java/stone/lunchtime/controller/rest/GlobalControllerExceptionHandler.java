// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.service.exception.AbstractFunctionalException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Will handle default HTTP Status and response body for exceptions.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Handles functional exceptions.
	 *
	 * @param pException the targeted exception
	 * @param request    the HttpServletRequest request
	 * @return the HttpStatus and body regarding the exception
	 */
	@ExceptionHandler(AbstractFunctionalException.class)
	public ResponseEntity<ExceptionDtoOut> exceptionHandler(AbstractFunctionalException pException,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		GlobalControllerExceptionHandler.LOG.error("[{}] --> exceptionHandler", remoteIP, pException);
		ExceptionDtoOut dtoOut = new ExceptionDtoOut(pException);
		GlobalControllerExceptionHandler.LOG.error("[{}] <-- exceptionHandler", remoteIP);
		return new ResponseEntity<>(dtoOut, HttpStatus.PRECONDITION_FAILED); // 412
	}

	/**
	 * Handles parameter exceptions.
	 *
	 * @param pException the targeted exception
	 * @param request    the HttpServletRequest request
	 * @return the HttpStatus and body regarding the exception
	 */
	@ExceptionHandler(ParameterException.class)
	public ResponseEntity<ExceptionDtoOut> exceptionHandler(ParameterException pException, HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		GlobalControllerExceptionHandler.LOG.error("[{}] --> exceptionHandler", remoteIP, pException);
		ExceptionDtoOut dtoOut = new ExceptionDtoOut(pException);
		GlobalControllerExceptionHandler.LOG.error("[{}] <-- exceptionHandler", remoteIP);
		return new ResponseEntity<>(dtoOut, HttpStatus.BAD_REQUEST); // 400
	}

	/**
	 * Handles authentication and habilitation exceptions.
	 *
	 * @param pException the targeted exception
	 * @param request    the HttpServletRequest request
	 * @return the HttpStatus and body regarding the exception
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ExceptionDtoOut> exceptionHandler(AuthenticationException pException,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		GlobalControllerExceptionHandler.LOG.error("[{}] --> exceptionHandler", remoteIP, pException);
		ExceptionDtoOut dtoOut = new ExceptionDtoOut(pException);
		GlobalControllerExceptionHandler.LOG.error("[{}] <-- exceptionHandler", remoteIP);
		return new ResponseEntity<>(dtoOut, HttpStatus.UNAUTHORIZED); // 401
	}
}
