// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IUserDao;
import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.entity.RoleEntity;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.service.exception.SendMailException;

/**
 * Authentication service.
 */
@Service
public class AuthenticationService implements IAuthenticationService {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IUserDao userDao;

	@Autowired
	private EmailService emailService;

	@Value("${configuration.forgot.password.email.body}")
	private String mailBody;
	@Value("${configuration.forgot.password.email.subject}")
	private String mailSubject;

	/**
	 * Spring Security method.
	 *
	 * @param pAuthentication authentication object with login and password
	 * @return authentication object with role
	 * @throws AuthenticationException if an error occurred
	 */
	@Override
	public Authentication authenticate(Authentication pAuthentication) {
		String name = pAuthentication.getName();
		String password = pAuthentication.getCredentials() != null ? pAuthentication.getCredentials().toString() : null;
		AuthenticationService.LOG.debug("Spring Security Authenticate name={}", name);
		UserEntity user = this.authenticate(name, password);
		if (user != null) {
			AuthenticationService.LOG.debug("Spring Security Authenticate found {}", user);
			Collection<GrantedAuthority> springSecurityRoles = new ArrayList<>(2);
			// Get role in data base, become a role in SS
			for (RoleEntity role : user.getRoles()) {
				GrantedAuthority ga = new SimpleGrantedAuthority(role.getLabel().toString());
				springSecurityRoles.add(ga);
			}
			UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(name, password,
					springSecurityRoles);
			upat.setDetails(new UserDtoOut(user));
			return upat;
		}
		return null;
	}

	/**
	 * Spring Security method.
	 *
	 * @param pAuthentication authentication object
	 * @return true if parameter belong to Authentication family
	 */
	@Override
	public boolean supports(Class<?> pAuthentication) {
		AuthenticationService.LOG.debug("support : {} ?", pAuthentication);
		boolean resu = Authentication.class.isAssignableFrom(pAuthentication);
		AuthenticationService.LOG.debug("support : {}={}", pAuthentication, resu);
		return resu;
	}

	/**
	 * Authenticates a user (for internal use).
	 *
	 * @param pEmail    an email
	 * @param pPassword a password
	 * @return the user found, throws an exception if an error occurred
	 *
	 * @throws BadCredentialsException   if parameter is invalid
	 * @throws DisabledException         if user status is not enabled
	 * @throws UsernameNotFoundException if authentication is wrong
	 * @throws AuthenticationException if an error occurred
	 */
	protected UserEntity authenticate(String pEmail, String pPassword) {
		AuthenticationService.LOG.debug("authentifier - {}, XXX", pEmail);
		if (pEmail == null || pPassword == null) {
			AuthenticationService.LOG.error("authentifier - null?, null?");
			throw new BadCredentialsException("email ou modepasse est null !");
		}
		if (pEmail.trim().isEmpty() || pPassword.trim().isEmpty()) {
			AuthenticationService.LOG.error("authentifier - \"\"?, \"\"?");
			throw new BadCredentialsException("email ou modepasse est vide !");
		}
		Optional<UserEntity> result = this.userDao.findOneByEmailAndPassword(pEmail, pPassword);
		if (result.isPresent()) {
			UserEntity user = result.get();
			if (user.isEnabled()) {
				AuthenticationService.LOG.debug("authentifier - {},XXX found user with id={}", pEmail, user.getId());
				return user;
			} else {
				AuthenticationService.LOG.warn("authentifier - {}, Status {}", pEmail, user.getStatus());
				throw new DisabledException(
						"Erreur d'authentification, l'utilisateur est dans l'état [" + user.getStatus() + "]");
			}
		}
		AuthenticationService.LOG.warn("authentifier - User with email {} was not found", pEmail);
		throw new UsernameNotFoundException("Erreur d'authentification");
	}

	/**
	 * Sends an email to the user with its current password.
	 *
	 * @param pEmail an email
	 * @throws EntityNotFoundException     if user was not found
	 * @throws ParameterException          if parameter is invalid
	 * @throws InconsistentStatusException if user status is not enabled
	 * @throws SendMailException           if mail was not sent
	 */
	@Override
	@Transactional(readOnly = true)
	public void forgotPassword(String pEmail)
			throws EntityNotFoundException, SendMailException, InconsistentStatusException {
		AuthenticationService.LOG.debug("forgotPassword - {}", pEmail);
		if (pEmail == null) {
			AuthenticationService.LOG.error("forgotPassword - null?");
			throw new ParameterException("email est null !", "pEmail");
		}
		if (pEmail.trim().isEmpty()) {
			AuthenticationService.LOG.error("forgotPassword - \"\"?");
			throw new ParameterException("email est vide !", "pEmail");
		}
		Optional<UserEntity> result = this.userDao.findOneByEmail(pEmail);
		if (result.isPresent()) {
			UserEntity user = result.get();
			if (user.isEnabled()) {
				AuthenticationService.LOG.debug("forgotPassword - found user with id {}", user.getId());
				this.mailBody = MessageFormat.format(this.mailBody, user.getPassword());
				this.emailService.sendSimpleMessage(user.getEmail(), this.mailSubject, this.mailBody);
				return;
			}
			AuthenticationService.LOG.warn("forgotPassword - {}, Status {}", pEmail, user.getStatus());
			throw new InconsistentStatusException(
					"Erreur d'authentification, l'utilisateur est dans l'état [" + user.getStatus() + "]");
		}
		AuthenticationService.LOG.warn("forgotPassword - No user found with email={}", pEmail);
		throw new EntityNotFoundException("Utilisateur introuvable", pEmail);
	}

}
