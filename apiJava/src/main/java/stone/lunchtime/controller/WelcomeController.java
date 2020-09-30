// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * default root controller.
 */
@RestController
@RequestMapping("/")
public class WelcomeController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Redirect to Swagger URL
	 *
	 * @param request the request
	 * @return the root page (swagger here)
	 */
	@GetMapping
	public ModelAndView welcome(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		WelcomeController.LOG.info("[{}] --> welcome", remoteIP);
		return new ModelAndView("redirect:/swagger-ui.html");
	}
}
