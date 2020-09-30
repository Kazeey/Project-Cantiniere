// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.spring.security.filter.SecurityConstants;
import stone.lunchtime.test.AbstractWebTest;
import stone.lunchtime.test.InitDataBase;

/**
 * Test for login controller, using Mock.
 */
public class LoginRestControllerTest extends AbstractWebTest {

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin01() throws Exception {
		// The call to controller
		ResultActions result = super.logMeInAsLunchLady();
		result.andExpect(MockMvcResultMatchers.header().exists(SecurityConstants.TOKEN_HEADER));
		UserDtoOut user = super.getUserInToken(result);
		Assertions.assertNotNull(user, "Header shoud contain user");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, user.getEmail(), "Header shoud contain user with email");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin02() throws Exception {
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", InitDataBase.USER_EXISTING_EMAIL).param("password", new String[] { null }));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin03() throws Exception {
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", new String[] { null }).param("password", InitDataBase.USER_DEFAULT_PWD));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin04() throws Exception {
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", InitDataBase.USER_EXISTING_EMAIL).param("password", ""));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin05() throws Exception {
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", "").param("password", InitDataBase.USER_DEFAULT_PWD));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin06() throws Exception {
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", InitDataBase.USER_EXISTING_EMAIL).param("password", "wrongpwd"));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin07() throws Exception {
		UserEntity user = this.userService.disable(InitDataBase.USER_EXISTING_ID);
		Assertions.assertNotNull(user, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, user.getEmail(), "User should have the good email");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_ID, user.getId(), "User should have first id");
		Assertions.assertTrue(user.isDisabled(), "User should be disabled");
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", InitDataBase.USER_EXISTING_EMAIL).param("password", InitDataBase.USER_DEFAULT_PWD));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testLogin08() throws Exception {
		UserEntity user = this.userService.delete(InitDataBase.USER_EXISTING_ID);
		Assertions.assertNotNull(user, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, user.getEmail(), "User should have the good email");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_ID, user.getId(), "User should have first id");
		Assertions.assertTrue(user.isDeleted(), "User should be delted");
		// The call to controller
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", InitDataBase.USER_EXISTING_EMAIL).param("password", InitDataBase.USER_DEFAULT_PWD));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
}
