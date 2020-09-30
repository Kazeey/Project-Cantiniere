// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.SendMailException;

/**
 * Authentication service.
 */
public interface IAuthenticationService extends AuthenticationProvider {

	/**
	 * Spring Security method.
	 *
	 * @param pAuthentication authentication object with login and password
	 * @return authentication object with role
	 * @throws AuthenticationException if an error occurred
	 */
	@Override
	public abstract Authentication authenticate(Authentication pAuthentication);

	/**
	 * Spring Security method.
	 *
	 * @param pAuthentication authentication object
	 * @return true if parameter belong to Authentication family
	 */
	@Override
	public abstract boolean supports(Class<?> pAuthentication);

	/**
	 * Sends an email to the user with its current password.
	 *
	 * @param pEmail an email
	 * @throws EntityNotFoundException     if user was not found
	 * @throws ParameterException          if parameter is invalid
	 * @throws InconsistentStatusException if user status is not enabled
	 * @throws SendMailException           if mail was not sent
	 */
	public abstract void forgotPassword(String pEmail)
			throws EntityNotFoundException, SendMailException, InconsistentStatusException;

}
