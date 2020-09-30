// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.UserDtoIn;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.RoleLabel;
import stone.lunchtime.entity.Sex;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.EntityAlreadySavedException;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentRoleException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.LackOfMoneyException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.test.AbstractTest;
import stone.lunchtime.test.InitDataBase;

/**
 * User service test class.
 */
public class UserServiceTest extends AbstractTest {
	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindEmail01() throws Exception {
		final String email = InitDataBase.USER_EXISTING_EMAIL;
		UserEntity result = this.userService.find(email);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(email, result.getEmail(), () -> "Result must have " + email + " as email");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindEmail02() throws Exception {
		final String email = "nexistepas@gmail.com";
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.userService.find(email));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindEmail03() throws Exception {
		final String email = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.find(email));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindEmail04() throws Exception {
		final String email = "";
		Assertions.assertThrows(ParameterException.class, () -> this.userService.find(email));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testExist01() throws Exception {
		final String email = InitDataBase.USER_EXISTING_EMAIL;
		boolean result = this.userService.exist(email);
		Assertions.assertTrue(result, "Result must exist");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testExist02() throws Exception {
		final String email = "jjj@aol.com";
		boolean result = this.userService.exist(email);
		Assertions.assertFalse(result, "Result must NOT exist");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testExist03() throws Exception {
		final String email = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.exist(email));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind02() throws Exception {
		final Integer id = Integer.valueOf(1000000);
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.userService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind03() throws Exception {
		final Integer id = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind04() throws Exception {
		final Integer id = Integer.valueOf(-1);
		Assertions.assertThrows(ParameterException.class, () -> this.userService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister01() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setWallet(BigDecimal.valueOf(50D));
		user.setEmail("newtoto@gmail.com");
		user.setPassword("alpha");
		user.setIsLunchLady(Boolean.FALSE);
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setSex(Sex.MAN.getValue());

		UserEntity result = this.userService.register(user);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertNotNull(result.getRegistrationDate(), "Result must have an registration date");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister02() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setEmail(InitDataBase.USER_EXISTING_EMAIL);
		user.setPassword("alpha");
		user.setSex(Sex.MAN.getValue());
		Assertions.assertThrows(EntityAlreadySavedException.class, () -> this.userService.register(user));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister03() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setWallet(BigDecimal.valueOf(50D));
		user.setEmail("newtoto@gmail.com");
		user.setPassword("alpha");
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setSex(Sex.MAN.getValue());

		UserEntity result = this.userService.register(user);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertNotNull(result.getRegistrationDate(), "Result must have an registration date");
		Assertions.assertFalse(result.getIsLunchLady().booleanValue(), "Result must not be LunchLady");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister04() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setEmail("newtoto@gmail.com");
		user.setPassword("alpha");
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setSex(Sex.MAN.getValue());

		UserEntity result = this.userService.register(user);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertNotNull(result.getRegistrationDate(), "Result must have an registration date");
		Assertions.assertEquals(0D, result.getWallet().doubleValue(), 0.01D, "Result must not a wallet with 0");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister05() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setPassword("alpha");
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setSex(Sex.MAN.getValue());

		Assertions.assertThrows(ParameterException.class, () -> this.userService.register(user));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister06() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setEmail("newtoto@gmail.com");
		user.setPassword(null);
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setSex(Sex.MAN.getValue());

		Assertions.assertThrows(ParameterException.class, () -> this.userService.register(user));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister07() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.userService.register(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister08() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setEmail("newtoto@gmail.com");
		user.setPassword("toto");
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		// user.setSexe(Sex.MAN);

		Assertions.assertThrows(ParameterException.class, () -> this.userService.register(user));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister09() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setEmail("newtoto@gmail.com");
		user.setPassword("alpha");
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setWallet(null);
		user.setSex(Sex.MAN.getValue());

		UserEntity result = this.userService.register(user);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertNotNull(result.getRegistrationDate(), "Result must have an registration date");
		Assertions.assertEquals(0D, result.getWallet().doubleValue(), 0.01D, "Result must have a wallet with 0");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testRegister10() throws Exception {
		UserDtoIn user = new UserDtoIn();
		user.setAddress("Somewhere in spain");
		user.setEmail("newtoto@gmail.com");
		user.setPassword("alpha");
		user.setName("Durant");
		user.setFirstname("Albert");
		user.setPhone("0148567897");
		user.setTown("Paris");
		user.setPostalCode("75000");
		user.setWallet(BigDecimal.valueOf(-10D));
		user.setSex(Sex.MAN.getValue());

		UserEntity result = this.userService.register(user);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertNotNull(result.getRegistrationDate(), "Result must have an registration date");
		Assertions.assertEquals(0D, result.getWallet().doubleValue(), 0.01D, "Result must have a wallet with 0");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate01() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final String newName = "new name";
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setName(newName);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(newName, result.getName(), () -> "Result must have a name " + newName);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate02() throws Exception {
		final Integer id = Integer.valueOf(2);
		final String newEmail = InitDataBase.USER_EXISTING_EMAIL;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setEmail(newEmail);
		Assertions.assertThrows(EntityAlreadySavedException.class, () -> this.userService.update(id, dto, false));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate03() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final BigDecimal newCagnote = BigDecimal.valueOf(555D);
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		double currentCagnote = result.getWallet().doubleValue();
		UserDtoIn dto = new UserDtoIn(result);
		dto.setWallet(newCagnote);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(currentCagnote, result.getWallet().doubleValue(), 0.01D,
				"Result must have a wallet unchanged");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate04() throws Exception {
		final Integer id = Integer.valueOf(2);
		final String newEmail = null;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setEmail(newEmail);
		Assertions.assertThrows(ParameterException.class, () -> this.userService.update(id, dto, false));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate05() throws Exception {
		final Integer id = Integer.valueOf(2);
		final String newPwd = null;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setPassword(newPwd);
		Assertions.assertThrows(ParameterException.class, () -> this.userService.update(id, dto, false));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate06() throws Exception {
		final Integer id = Integer.valueOf(200000);
		UserDtoIn dto = new UserDtoIn();
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.userService.update(id, dto, false));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate07() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserDtoIn dto = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.update(id, dto, false));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate08() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertTrue(result.getIsLunchLady(), "Result must be a lunch lady");
		UserDtoIn din = new UserDtoIn(result);
		din.setIsLunchLady(Boolean.FALSE);
		Assertions.assertThrows(InconsistentRoleException.class, () -> this.userService.update(id, din, true));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate09() throws Exception {
		final Integer id1 = InitDataBase.USER_EXISTING_ID;
		final Integer id2 = Integer.valueOf(2);
		UserEntity result1 = this.userService.find(id1);
		Assertions.assertNotNull(result1, "Result must exist");
		Assertions.assertEquals(id1, result1.getId(), () -> "Result must have " + id1 + " as id");
		Assertions.assertTrue(result1.getIsLunchLady(), "Result must be a lunch lady");
		UserEntity result2 = this.userService.find(id2);
		Assertions.assertNotNull(result2, "Result must exist");
		Assertions.assertEquals(id2, result2.getId(), () -> "Result must have " + id2 + " as id");
		Assertions.assertFalse(result2.getIsLunchLady(), "Result must NOT be a lunch lady");
		UserDtoIn din = new UserDtoIn(result2);
		din.setIsLunchLady(Boolean.TRUE);
		result2 = this.userService.update(id2, din, true);
		Assertions.assertTrue(result2.getIsLunchLady(), "Result must be a lunch lady");
		din = new UserDtoIn(result1);
		din.setIsLunchLady(Boolean.FALSE);
		result1 = this.userService.update(id1, din, true);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate10() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final String newAddr = "new address";
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setAddress(newAddr);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(newAddr, result.getAddress(), () -> "Result must have an address " + newAddr);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate11() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final String newPostalCode = "new Postal Code";
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setPostalCode(newPostalCode);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(newPostalCode, result.getPostalCode(),
				() -> "Result must have a postal code " + newPostalCode);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate12() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final String newFirstName = "new first name";
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setFirstname(newFirstName);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(newFirstName, result.getFirstname(),
				() -> "Result must have a first name " + newFirstName);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate13() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final String newTown = "new town";
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setTown(newTown);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(newTown, result.getTown(), () -> "Result must have a town " + newTown);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate14() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		final String newPhone = "new phone";
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		dto.setPhone(newPhone);
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertEquals(newPhone, result.getPhone(), () -> "Result must have a phone " + newPhone);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate15() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		UserDtoIn dto = new UserDtoIn(result);
		result.getRoles().clear();
		result = this.userService.update(id, dto, false);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertNotNull(result.getRoles(), "Result must have role");
		Assertions.assertFalse(result.getRoles().isEmpty(), "Result must have role");
		Assertions.assertEquals(1, result.getRoles().size(), "Result must have one role");
		Assertions.assertEquals(RoleLabel.ROLE_USER, result.getRoles().get(0).getLabel(), "Result must have USER role");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDebit01() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		// Give money to the user if needed
		double currentCagnote = result.getWallet().doubleValue();
		if (currentCagnote <= 5D) {
			result = this.userService.credit(id, BigDecimal.valueOf(50D));
			Assertions.assertNotNull(result, "Result must exist");
			Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
			currentCagnote = result.getWallet().doubleValue();
		}

		result = this.userService.debit(id, BigDecimal.valueOf(5D));
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertNotEquals(currentCagnote, result.getWallet().doubleValue(), 0.01D,
				"Result must have a wallet changed");
		Assertions.assertEquals(currentCagnote - 5D, result.getWallet().doubleValue(), 0.01D,
				"Result must have a wallet changed");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDebit02() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertThrows(LackOfMoneyException.class, () -> this.userService.debit(id, BigDecimal.valueOf(500D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDebit03() throws Exception {
		final Integer id = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.debit(id, BigDecimal.valueOf(500D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDebit04() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.debit(id, BigDecimal.valueOf(-5D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDebit05() throws Exception {
		final Integer id = Integer.valueOf(100000);
		Assertions.assertThrows(EntityNotFoundException.class,
				() -> this.userService.debit(id, BigDecimal.valueOf(5D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDebit06() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.debit(id, null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCredit01() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		double currentCagnote = result.getWallet().doubleValue();
		result = this.userService.credit(id, BigDecimal.valueOf(50D));
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		Assertions.assertNotEquals(currentCagnote, result.getWallet().doubleValue(), 0.01D,
				"Result must have a wallet changed");
		Assertions.assertEquals(currentCagnote + 50D, result.getWallet().doubleValue(), 0.01D,
				"Result must have a wallet changed");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCredit02() throws Exception {
		final Integer id = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.credit(id, BigDecimal.valueOf(50D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCredit03() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.credit(id, BigDecimal.valueOf(-5D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCredit04() throws Exception {
		final Integer id = Integer.valueOf(10000);
		Assertions.assertThrows(EntityNotFoundException.class,
				() -> this.userService.credit(id, BigDecimal.valueOf(5D)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCredit05() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.credit(id, null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage01() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity result = this.userService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		result = this.userService.updateImage(id, dtoIn);
		ImageEntity ie = result.getImage();
		Assertions.assertNotNull(ie.getImagePath(), "Image must have a path");
		Assertions.assertFalse(ie.getIsDefault(), "Image should NOT be a default one");
		Assertions.assertNotNull(ie.getImageBin(), "Image should have a blob");
		Assertions.assertEquals(dtoIn.getImagePath(), ie.getImagePath(), "Image should have the same path");
		Assertions.assertEquals(dtoIn.getImage64(), ie.getImage64(), "Image should have the same base 64");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage02() throws Exception {
		final Integer id = null;
		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		Assertions.assertThrows(ParameterException.class, () -> this.userService.updateImage(id, dtoIn));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage03() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		ImageDtoIn dtoIn = null;
		Assertions.assertThrows(ParameterException.class, () -> this.userService.updateImage(id, dtoIn));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage04() throws Exception {
		final Integer id = InitDataBase.USER_EXISTING_ID;
		UserEntity ue = super.userService.find(id);
		ue.setStatus(EntityStatus.DELETED);
		super.userDao.save(ue);
		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		Assertions.assertThrows(InconsistentStatusException.class, () -> this.userService.updateImage(id, dtoIn));
	}
}
