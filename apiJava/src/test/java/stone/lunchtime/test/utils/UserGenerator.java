// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.test.utils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;

import stone.lunchtime.dto.in.UserDtoIn;

/**
 * Not a test class. Will generate users.
 */
@Disabled("Not for tests, used for data base generation.")
public final class UserGenerator {
	private static final Logger LOG = LogManager.getLogger();

	private static final String[] MAIL_EXT = { "gmail.com", "free.fr", "aol.com", "orange.fr", "stone.fr" };

	/**
	 * Constructor of the object.
	 */
	private UserGenerator() {
		throw new IllegalAccessError("Not for use");
	}

	/**
	 * Generates users.
	 *
	 * @param pHowMany       how many user do you want
	 * @param pDefaultPwd    the default password to use. If null will generate one.
	 * @return a user list
	 */
	public static List<UserDtoIn> generate(int pHowMany, String pDefaultPwd) {
		List<String> names = TestFileReader.readNames();
		List<String> firstnamesForMen = TestFileReader.readManFirstname();
		List<String> firstnamesForWomen = TestFileReader.readWomanFirstname();
		List<String> addresses = TestFileReader.readAddresses();
		Map<Integer, List<String>> towns = TestFileReader.readTowns();
		Random random = new Random();
		List<UserDtoIn> result = new ArrayList<>(pHowMany);
		for (int i = 0; i < pHowMany; i++) {
			UserGenerator.LOG.debug("Creating user {}/{}", i, pHowMany);
			UserDtoIn userDto = new UserDtoIn();
			userDto.setName(names.get(random.nextInt(names.size())));
			byte sex = (byte) random.nextInt(3);
			userDto.setSex(Byte.valueOf(sex));
			if (userDto.isMan()) {
				userDto.setFirstname(firstnamesForMen.get(random.nextInt(firstnamesForMen.size())));
			} else if (userDto.isWoman()) {
				userDto.setFirstname(firstnamesForWomen.get(random.nextInt(firstnamesForWomen.size())));
			} else {
				if (random.nextBoolean()) {
					userDto.setFirstname(firstnamesForWomen.get(random.nextInt(firstnamesForWomen.size())));
				} else {
					userDto.setFirstname(firstnamesForMen.get(random.nextInt(firstnamesForMen.size())));
				}
			}

			if (random.nextBoolean()) {
				userDto.setAddress(addresses.get(random.nextInt(addresses.size())));
				// 1000 a 98890
				List<String> towNames = null;
				int cp = -1;
				while (towNames == null) {
					cp = random.nextInt(98890 + 1);
					towNames = towns.get(Integer.valueOf(cp));
				}
				userDto.setPostalCode(String.valueOf(cp));
				userDto.setTown(towNames.get(random.nextInt(towNames.size())));
			}
			userDto.setEmail(UserGenerator.generateEmail(userDto.getName(), userDto.getFirstname()));
			if (random.nextBoolean()) {
				userDto.setWallet(BigDecimal.valueOf(random.nextDouble() * random.nextInt(100)));
			}
			if (pDefaultPwd != null) {
				userDto.setPassword(pDefaultPwd);
			} else {
				userDto.setPassword(UserGenerator.generatePassword(8, false));
			}
			if (random.nextBoolean()) {
				StringBuilder nu = new StringBuilder();
				for (int j = 0; j < 10; j++) {
					nu.append(random.nextInt(10));
				}
				userDto.setPhone(nu.toString());
			}
			result.add(userDto);
		}
		return result;
	}

	/**
	 * Generates an email.
	 *
	 * @param pName      the name
	 * @param pFirstname the first name
	 * @return an email
	 */
	private static String generateEmail(String pName, String pFirstname) {
		StringBuilder email = new StringBuilder();
		if (pFirstname.length() > 5) {
			email.append(pFirstname.substring(0, 5));
		} else {
			email.append(pFirstname);
		}
		email.append('.');
		if (pName.length() > 5) {
			email.append(pName.substring(0, 5));
		} else {
			email.append(pName);
		}
		email.append('@');
		Random random = new Random();
		email.append(UserGenerator.MAIL_EXT[random.nextInt(UserGenerator.MAIL_EXT.length)]);
		return email.toString().toLowerCase();
	}

	/**
	 * Generates a password.
	 *
	 * @param pSize             amount of char to generate
	 * @param pAllowSpecialChar if true will also used special chars
	 * @return the password
	 */
	private static String generatePassword(int pSize, boolean pAllowSpecialChar) {
		Random random = new SecureRandom();
		// http://www.asciitable.com/
		// 33-46 [special char]
		// 48-57 [0,...,9]
		// 65-90 [A,B,...,Z]
		// 97-122 [a,b,...,z]
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pSize; i++) {
			final int val = pAllowSpecialChar ? random.nextInt(4) : random.nextInt(3);
			switch (val) {
			case 0:
				sb.append((char) (random.nextInt(91 - 65) + 65));
				break;
			case 1:
				sb.append((char) (random.nextInt(58 - 48) + 48));
				break;
			case 2:
				sb.append((char) (random.nextInt(123 - 97) + 97));
				break;
			case 3:
				char c = (char) (random.nextInt(47 - 33) + 33);
				while (c == '"' || c == '\'' || c == ',' || c == '.') {
					c = (char) (random.nextInt(47 - 33) + 33);
				}
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
