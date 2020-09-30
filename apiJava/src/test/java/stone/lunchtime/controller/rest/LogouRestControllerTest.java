// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import stone.lunchtime.spring.security.filter.SecurityConstants;
import stone.lunchtime.test.AbstractWebTest;
import stone.lunchtime.test.InitDataBase;

/**
 * Test for logout controller, using Mock.
 */
public class LogouRestControllerTest extends AbstractWebTest {
	private static final String URL = "/logout";

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogout01() throws Exception {
		final String email = InitDataBase.USER_EXISTING_EMAIL;
		final String password = InitDataBase.USER_DEFAULT_PWD;

		// The call to controller
		ResultActions result = super.logMeIn(email, password);
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		result = super.mockMvc.perform(
				MockMvcRequestBuilders.put(LogouRestControllerTest.URL).contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
