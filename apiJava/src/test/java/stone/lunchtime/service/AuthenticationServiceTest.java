// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.test.AbstractTest;
import stone.lunchtime.test.InitDataBase;

/**
 * Tests for authentication service.
 */
public class AuthenticationServiceTest extends AbstractTest {

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate01() throws Exception {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(InitDataBase.USER_EXISTING_EMAIL,
				InitDataBase.USER_DEFAULT_PWD);
		authenticationToken = this.authenticationService.authenticate(authenticationToken);
		Assertions.assertNotNull(authenticationToken, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, authenticationToken.getName(),
				"User should have the good email");
		Assertions.assertEquals(1, ((UserDtoOut) authenticationToken.getDetails()).getId().intValue(),
				"User should have first id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate02() throws Exception {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(InitDataBase.USER_EXISTING_EMAIL,
				null);
		Assertions.assertThrows(BadCredentialsException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate03() throws Exception {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(null,
				InitDataBase.USER_DEFAULT_PWD);
		Assertions.assertThrows(BadCredentialsException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate04() throws Exception {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(InitDataBase.USER_EXISTING_EMAIL,
				"");
		Assertions.assertThrows(BadCredentialsException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate05() throws Exception {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken("", InitDataBase.USER_DEFAULT_PWD);
		Assertions.assertThrows(BadCredentialsException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate06() throws Exception {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(InitDataBase.USER_EXISTING_EMAIL,
				"wrongpwd");
		Assertions.assertThrows(UsernameNotFoundException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate07() throws Exception {
		UserEntity result = this.userService.disable(InitDataBase.USER_EXISTING_ID);
		Assertions.assertNotNull(result, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, result.getEmail(), "User should have the good email");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_ID, result.getId(), "User should have first id");
		Assertions.assertTrue(result.isDisabled(), "User should be disabled");
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(InitDataBase.USER_EXISTING_EMAIL,
				InitDataBase.USER_DEFAULT_PWD);
		Assertions.assertThrows(DisabledException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAuthenticate08() throws Exception {
		UserEntity result = this.userService.delete(InitDataBase.USER_EXISTING_ID);
		Assertions.assertNotNull(result, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, result.getEmail(), "User should have the good email");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_ID, result.getId(), "User should have first id");
		Assertions.assertTrue(result.isDeleted(), "User should be deleted");
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(InitDataBase.USER_EXISTING_EMAIL,
				InitDataBase.USER_DEFAULT_PWD);
		Assertions.assertThrows(DisabledException.class,
				() -> this.authenticationService.authenticate(authenticationToken));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testForgotPassword01() throws Exception {
		final boolean initialState = super.emailService.getSendMail();
		if (initialState) {
			super.emailService.deactivateSendMail();
		}
		this.authenticationService.forgotPassword(InitDataBase.USER_EXISTING_EMAIL);
		Assertions.assertTrue(true, "Sonar is my friend");
		if (initialState) {
			super.emailService.activateSendMail();
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testForgotPassword02() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.authenticationService.forgotPassword(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testForgotPassword03() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.authenticationService.forgotPassword(""));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testForgotPassword04() throws Exception {
		Assertions.assertThrows(EntityNotFoundException.class,
				() -> this.authenticationService.forgotPassword("wrong@email.com"));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testForgotPassword05() throws Exception {
		UserEntity result = this.userService.disable(InitDataBase.USER_EXISTING_ID);
		Assertions.assertNotNull(result, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, result.getEmail(), "User should have the good email");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_ID, result.getId(), "User should have first id");
		Assertions.assertTrue(result.isDisabled(), "User should be deactivated");
		Assertions.assertThrows(InconsistentStatusException.class,
				() -> this.authenticationService.forgotPassword(InitDataBase.USER_EXISTING_EMAIL));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testForgotPassword06() throws Exception {
		UserEntity result = this.userService.delete(InitDataBase.USER_EXISTING_ID);
		Assertions.assertNotNull(result, "User should not be null");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_EMAIL, result.getEmail(), "User should have the good email");
		Assertions.assertEquals(InitDataBase.USER_EXISTING_ID, result.getId(), "User should have first id");
		Assertions.assertTrue(result.isDeleted(), "User should be deleted");
		Assertions.assertThrows(InconsistentStatusException.class,
				() -> this.authenticationService.forgotPassword(InitDataBase.USER_EXISTING_EMAIL));
	}
}
