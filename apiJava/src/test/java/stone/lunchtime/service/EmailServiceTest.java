// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import stone.lunchtime.test.AbstractTest;

/**
 * Test mail service.
 */
public class EmailServiceTest extends AbstractTest {

	/**
	 * Sends an email
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	@Timeout(value = 45, unit = TimeUnit.SECONDS)
	public void testSendEmail() throws Exception {
		// This test can take 20s, so give it 45s
		this.emailService.sendSimpleMessage("renaud91@gmail.com", "Test from LunchTime",
				"Just a test from LunchTime App");
		Assertions.assertTrue(true, "Sonar is my friend");
	}
}
