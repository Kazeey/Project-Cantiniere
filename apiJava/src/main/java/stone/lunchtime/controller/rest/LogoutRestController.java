// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import stone.lunchtime.controller.AbstractController;
import stone.lunchtime.dto.out.UserDtoOut;

/**
 * Logout controller. <br>
 *
 * @deprecated Old controller for logout. No need since we exchange a JWT. <br>
 */
// @RestController
// @RequestMapping("/oldlogout")
@Deprecated
public class LogoutRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Unlogges the connected user. <br>
	 *
	 * Session will be cleared.
	 *
	 * @param request the HttpServletRequest
	 * @return the user unlogged, throws an exception if error
	 * @throws ServletException if an error occurred
	 */
	@GetMapping
	public ResponseEntity<UserDtoOut> signOut(HttpServletRequest request) throws ServletException {
		final String remoteIP = request.getRemoteAddr();
		LogoutRestController.LOG.error("[{}] --> signOut - do not use this API, no need", remoteIP);
		LogoutRestController.LOG.info("[{}] --> signOut", remoteIP);
		HttpSession session = request.getSession(true);
		UserDtoOut dtoOut = super.getConnectedUser();
		if (dtoOut == null) {
			LogoutRestController.LOG.info("[{}] <-- signOut - NO session found", remoteIP);
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
		session.removeAttribute("user");
		session.invalidate();
		request.logout();
		LogoutRestController.LOG.info("[{}] <-- signOut - Session for user id {} removed", remoteIP, dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}
}
