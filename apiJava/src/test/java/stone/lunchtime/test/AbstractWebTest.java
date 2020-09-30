// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.test;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.spring.security.filter.SecurityConstants;

/**
 * Mother class of all tests that uses Web (for controller so).
 */
public abstract class AbstractWebTest extends AbstractTest {

	@Autowired
	protected MockMvc mockMvc;

	/**
	 * Logs the user as a Lunch Lady. Creates a session.
	 *
	 * @return the ResultActions
	 * @throws Exception if an error occurred
	 */
	protected ResultActions logOut(ResultActions result) throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGOUT_URL)
				.header(SecurityConstants.TOKEN_HEADER, this.getJWT(result)));
	}

	/**
	 * Logs the user. Creates a session.
	 * Will assert the ok HttpStatus before returning the action.
	 *
	 * @param email    an email
	 * @param password a password
	 * @return the ResultActions
	 * @throws Exception if an error occurred
	 */
	protected ResultActions logMeIn(String email, String password) throws Exception {
		ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(SecurityConstants.AUTH_LOGIN_URL)
				.param("email", email).param("password", password));
		result.andExpect(MockMvcResultMatchers.status().isOk());
		return result;
	}

	/**
	 * Logs the user as a Lunch Lady. Creates a session.
	 * Will assert the ok HttpStatus before returning the action.
	 *
	 * @return the ResultActions
	 * @throws Exception if an error occurred
	 */
	protected ResultActions logMeInAsLunchLady() throws Exception {
		ResultActions result = this.logMeIn(InitDataBase.USER_EXISTING_EMAIL, InitDataBase.USER_DEFAULT_PWD);
		result.andExpect(MockMvcResultMatchers.status().isOk());
		return result;
	}

	/**
	 * Gets the JWT inside the result.
	 *
	 * @param result where to find the JWT
	 * @return the JWT
	 */
	protected String getJWT(ResultActions result) {
		return result.andReturn().getResponse().getHeader(SecurityConstants.TOKEN_HEADER);
	}

	/**
	 * Gets the user dto inside the JWT.
	 *
	 * @param result where to find the JWT.
	 * @return the user dto inside the JWT
	 */
	protected UserDtoOut getUserInToken(ResultActions result) {
		byte[] signingKey = this.env.getProperty("configuration.jwt.key",
				"-KaPdSgVkXp2s5v8y/B?E(H+MbQeThWmZq3t6w9z$C&F)J@NcRfUjXn2r5u7x!A%").getBytes();

		String token = this.getJWT(result);
		Jws<Claims> parsedToken = Jwts.parserBuilder().setSigningKey(signingKey).build()
				.parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));

		String username = parsedToken.getBody().getSubject();

		if (!StringUtils.isEmpty(username)) {
			@SuppressWarnings("unchecked")
			Map<String, ?> userDto = (Map<String, ?>) parsedToken.getBody().get(SecurityConstants.TOKEN_USER);
			UserDtoOut userDtoOut = new UserDtoOut(userDto);
			return userDtoOut;
		}
		return null;
	}

	/**
	 * Gets the user's id inside the JWT.
	 *
	 * @param result where to find the JWT.
	 * @return the user's id inside the JWT
	 */
	protected Integer getUserIdInToken(ResultActions result) {
		UserDtoOut userDtoOut = this.getUserInToken(result);
		return userDtoOut.getId();
	}

	/**
	 * Logs as a random user that is not a Lunch Lady. Creates a session.
	 *
	 * @param idsToAvoid an id to avoid
	 * @return the ResultActions
	 * @throws Exception if an error occurred
	 */
	protected ResultActions logMeInAsNormalRandomUser(Integer... idsToAvoid) throws Exception {
		UserEntity userNotLunchLady = super.findASimpleUser(idsToAvoid);
		ResultActions result = this.logMeIn(userNotLunchLady.getEmail(), userNotLunchLady.getPassword());
		result.andExpect(MockMvcResultMatchers.status().isOk());
		return result;
	}

	/**
	 * Gets this week id.
	 *
	 * @return this week id.
	 */
	protected String getThisWeekId() {
		return this.getThisWeekIdAsInteger().toString();
	}

	/**
	 * Gets this week id.
	 *
	 * @return this week id.
	 */
	protected Integer getThisWeekIdAsInteger() {
		return Integer.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
	}

	/**
	 * Gets a valid menu for this week
	 *
	 * @param forThisWeek if true menu will be for this week only
	 * @return a valid menu for this week
	 */
	protected MenuEntity getValidMenu(boolean forThisWeek) {
		Optional<List<MenuEntity>> resu = forThisWeek ? this.menuDao.findAllAvailableForWeek(this.getThisWeekId())
				: this.menuDao.findAllEnabled();
		if (resu.isPresent()) {
			List<MenuEntity> menus = resu.get();
			Random random = new Random();
			return menus.get(random.nextInt(menus.size()));
		}
		throw new RuntimeException("No valid menu found!");
	}

	/**
	 * Gets a valid meal for this week
	 *
	 * @param forThisWeek if true meal will be for this week only
	 * @return a valid meal for this week
	 */
	protected MealEntity getValidMeal(boolean forThisWeek) {
		Optional<List<MealEntity>> resu = forThisWeek ? this.mealDao.findAllAvailableForWeek(this.getThisWeekId())
				: this.mealDao.findAllEnabled();
		if (resu.isPresent()) {
			List<MealEntity> menus = resu.get();
			Random random = new Random();
			return menus.get(random.nextInt(menus.size()));
		}
		throw new RuntimeException("No valid menu found!");
	}

	/**
	 * Gets a valid ingredient
	 *
	 * @return a valid ingredient
	 */
	protected IngredientEntity getValidIngredient() {
		Optional<List<IngredientEntity>> resu = this.ingredientDao.findAllEnabled();
		if (resu.isPresent()) {
			List<IngredientEntity> menus = resu.get();
			Random random = new Random();
			return menus.get(random.nextInt(menus.size()));
		}
		throw new RuntimeException("No valid Ingredient found!");
	}

}
