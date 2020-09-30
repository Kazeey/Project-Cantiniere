// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import stone.lunchtime.controller.AbstractController;
import stone.lunchtime.dto.in.LoginDtoIn;
import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.service.IAuthenticationService;
import stone.lunchtime.service.exception.InconsistentStatusException;

/**
 * Login controller.
 *
 * @deprecated Old controller for login. Spring Security handle this part for us
 *             using the JwtAuthenticationFilter. <br>
 *             Default url with Spring Security is /login with GET/POST/PUT ...
 *             <ul>
 *             <li>paramters : "email" and "password" :
 *             http://localhost:8080/lunchtime/login?email=toto@gmail.com&password=bonjour</li>
 *             <li>JSon : {"email":"toto@gmail.com","password":"bonjour"} :
 *             http://localhost:8080/lunchtime/login</li>
 *             </ul>
 */
// @RestController
// @RequestMapping("/oldlogin")
@Deprecated
public class LoginRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IAuthenticationService service;

	/**
	 * Authenticates a user. <br>
	 *
	 * Using post and parameter. <br>
	 *
	 * After authentication, user will be kept in HttpSession. This is not a
	 * RestFULL approach but it is more simple since no potential information is
	 * known about the client. <br>
	 *
	 * Session will be cleared by a call to logout.
	 *
	 * @param pEmail    user email
	 * @param pPassword user password
	 * @param request   the HttpServletRequest
	 * @return the user authenticate, throws an exception if error
	 * @throws AuthenticationException     if an error occurred
	 * @throws InconsistentStatusException if an error occurred
	 */
	@PostMapping
	public ResponseEntity<UserDtoOut> authenticatePostParameters(@RequestParam("email") String pEmail,
			@RequestParam("password") String pPassword, HttpServletRequest request) throws InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		LoginRestController.LOG.error("[{}] --> signOut - do not use this API, use /login", remoteIP);
		LoginRestController.LOG.info("[{}] --> authenticatePostParameters - {}", remoteIP, pEmail);
		LoginDtoIn pLogin = new LoginDtoIn(pEmail, pPassword);
		ResponseEntity<UserDtoOut> resutl = this.authenticate(pLogin, request);
		LoginRestController.LOG.info("[{}] <-- authenticatePostParameters - {} Xxx = {}", remoteIP, pEmail,
				resutl.getBody().getId());
		return resutl;
	}

	/**
	 * Authenticates a user. <br>
	 *
	 * Using put and JSon. <br>
	 *
	 * After authentication, user will be kept in HttpSession. This is not a
	 * RestFULL approach but it is more simple since no potential information is
	 * known about the client. <br>
	 *
	 * Session will be cleared by a call to logout.
	 *
	 * @param pLogin  user email and user password
	 * @param request the HttpServletRequest
	 * @return the user authenticate, throws an exception if error
	 * @throws AuthenticationException if an error occurred
	 */
	@PutMapping
	public ResponseEntity<UserDtoOut> authenticatePutJson(@RequestBody LoginDtoIn pLogin, HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		LoginRestController.LOG.info("[{}] --> authenticatePutJson - {}", remoteIP, pLogin);
		LoginRestController.LOG.error("[{}] --> signOut - do not use this API, use /login", remoteIP);
		ResponseEntity<UserDtoOut> resutl = this.authenticate(pLogin, request);
		LoginRestController.LOG.info("[{}] <-- authenticatePutJson - {} Xxx = {}", remoteIP, pLogin.getEmail(),
				resutl.getBody().getId());
		return resutl;
	}

	/**
	 * Authenticates a user. <br>
	 *
	 * After authentication, user will be kept in HttpSession. This is not a
	 * RestFULL approach but it is more simple since no potential information is
	 * known about the client. <br>
	 *
	 * Session will be cleared by a call to logout.
	 *
	 * @param pLogin  user email and user password
	 * @param request the HttpServletRequest
	 * @return the user authenticate, throws an exception if error
	 * @throws AuthenticationException if an error occurred
	 */
	private ResponseEntity<UserDtoOut> authenticate(LoginDtoIn pLogin, HttpServletRequest request) {
		pLogin.validate();
		Authentication upat = new UsernamePasswordAuthenticationToken(pLogin.getEmail(), pLogin.getPassword());
		upat = this.service.authenticate(upat);
		UserDtoOut dtoOut = (UserDtoOut) upat.getDetails();
		HttpSession session = request.getSession(true);
		session.setAttribute(AbstractController.KEY_USER, dtoOut);
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}
}
