// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;

import stone.lunchtime.dto.in.ConstraintDtoIn;
import stone.lunchtime.dto.in.IngredientDtoIn;
import stone.lunchtime.dto.in.MealDtoIn;
import stone.lunchtime.dto.in.MenuDtoIn;
import stone.lunchtime.dto.in.OrderDtoIn;
import stone.lunchtime.dto.in.QuantityDtoIn;
import stone.lunchtime.dto.in.UserDtoIn;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.entity.OrderEntity;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.spring.SpringBootConfiguration;
import stone.lunchtime.test.utils.IngredientGenerator;
import stone.lunchtime.test.utils.MealGenerator;
import stone.lunchtime.test.utils.MenuGenerator;
import stone.lunchtime.test.utils.UserGenerator;

/**
 * This is not a real test. <br>
 * Code used in order to initialize data base with data. <br>
 * Cannot be used with H2, but works with MySQL, Postgres and SQLServer.
 */
@Rollback(false)
public class InitDataBase extends AbstractTest {
	private static final Logger LOG = LogManager.getLogger();
	/** Nb user to generate. */
	public static final int USER_NB = 100;
	/** Lunch lady email. */
	public static final String USER_EXISTING_EMAIL = "toto@gmail.com";
	/** Default password. */
	public static final String USER_DEFAULT_PWD = "bonjour";
	/** Default id for first user. */
	public static final Integer USER_EXISTING_ID = Integer.valueOf(1);

	/** Nb ingredient to generate. */
	public static final int INGREDIENT_NB = 50;
	/** Nb meal to generate. */
	public static final int MEAL_NB = 50;
	/** Nb menu to generate. */
	public static final int MENU_NB = 60;

	@Autowired
	private Environment env;

	/**
	 * Removes all values from data base and reset sequences.
	 */
	private void init() {
		InitDataBase.LOG.info("Delete All");
		this.constraintDao.deleteAll();
		this.quantityMealDao.deleteAll();
		this.orderDao.deleteAll();
		this.menuDao.deleteAll();
		this.mealDao.deleteAll();
		this.ingredientDao.deleteAll();
		this.userDao.deleteAll();
		this.roleDao.deleteAll();
		this.imageDao.deleteAll();

		if (SpringBootConfiguration.usingMySQL(this.env)) {
			InitDataBase.LOG.info("Reset Sequence");
			this.constraintDao.resetMySQLSequence();
			this.orderDao.resetMySQLSequence();
			this.menuDao.resetMySQLSequence();
			this.mealDao.resetMySQLSequence();
			this.ingredientDao.resetMySQLSequence();
			this.userDao.resetMySQLSequence();
			this.quantityMealDao.resetMySQLSequence();
			this.roleDao.resetMySQLSequence();
			this.imageDao.resetMySQLSequence();
		} else {
			InitDataBase.LOG.warn("init test is only for MySQL, but using {}",
					this.env.getProperty("spring.datasource.driver-class-name"));
		}
	}

	/**
	 * Inserts users in data base.
	 *
	 * @throws Exception if an error occurred
	 */
	private void initUser() throws Exception {
		InitDataBase.LOG.info("initUser");

		List<UserDtoIn> users = UserGenerator.generate(InitDataBase.USER_NB, InitDataBase.USER_DEFAULT_PWD);

		// First user is LL
		UserDtoIn ll = users.get(0);
		ll.setIsLunchLady(Boolean.TRUE);
		ll.setEmail(InitDataBase.USER_EXISTING_EMAIL);
		int nb = 0;
		for (UserDtoIn dto : users) {
			UserEntity result = this.userService.register(dto);
			Assertions.assertNotNull(result, "Result must exist");
			Assertions.assertNotNull(result.getId(), "Result must have an id");
			Assertions.assertNotNull(result.getRegistrationDate(), "Result must have a registration date");
			if (nb == 0) {
				Assertions.assertTrue(result.getIsLunchLady().booleanValue(), "Result must be LunchLady");
			} else {
				Assertions.assertFalse(result.getIsLunchLady().booleanValue(), "Result must not be LunchLady");
			}
			nb++;
		}
	}

	/**
	 * Inserts ingredients in data base.
	 */
	private void initIngredient() {
		InitDataBase.LOG.info("initIngredient");
		List<IngredientDtoIn> ingredients = IngredientGenerator.generate(InitDataBase.INGREDIENT_NB);
		for (IngredientDtoIn dto : ingredients) {
			IngredientEntity result = this.ingredientService.add(dto);
			Assertions.assertNotNull(result, "Result must exist");
			Assertions.assertNotNull(result.getId(), "Result must have an id");
		}
	}

	/**
	 * Inserts meals in data base.
	 */
	private void initMeal() {
		InitDataBase.LOG.info("initMeal");
		List<MealDtoIn> meals = MealGenerator.generate(InitDataBase.MEAL_NB);
		List<IngredientEntity> allIngredients = this.ingredientService.findAll();
		Random random = new Random();
		for (MealDtoIn dto : meals) {
			List<IngredientEntity> somIng = this.generateList(random.nextInt(5), allIngredients);
			dto.setIngredientsId(this.transformInIdsList(somIng));
			dto.setAvailableForWeeks(this.dispoWeek(7, random));
			MealEntity result = this.mealService.add(dto);
			Assertions.assertNotNull(result, "Result must exist");
			Assertions.assertNotNull(result.getId(), "Result must have an id");
		}
	}

	/**
	 * Inserts menus in data base.
	 */
	private void initMenu() {
		InitDataBase.LOG.info("initMenu");
		List<MenuDtoIn> menus = MenuGenerator.generate(InitDataBase.MENU_NB);
		List<MealEntity> allMeals = this.mealService.findAll();
		Random random = new Random();
		int index = 1;
		for (MenuDtoIn dto : menus) {
			List<MealEntity> someMeals = this.generateList(random.nextInt(5), allMeals);
			dto.setMealIds(this.transformInIdsList(someMeals));
			dto.setAvailableForWeeks(this.dispoWeek(7, random));
			dto.setLabel("Menu - " + index);
			MenuEntity result = this.menuService.add(dto);
			Assertions.assertNotNull(result, "Result must exist");
			Assertions.assertNotNull(result.getId(), "Result must have an id");
			index++;
		}
	}

	private Set<Integer> dispoWeek(int howMany, Random random) {
		Set<Integer> dispo = new HashSet<>();
		for (int i = 0; i < howMany; i++) {
			if (!dispo.add(random.nextInt(52) + 1)) {
				i--;
			}
		}
		return dispo;
	}

	/**
	 * Inserts constraint in data base.
	 */
	private void initConstraint() {
		InitDataBase.LOG.info("initConstraint");
		ConstraintDtoIn dto = new ConstraintDtoIn();
		ConstraintEntity result = this.constraintService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");

	}

	/**
	 * Inserts orders in data base.
	 *
	 * @throws Exception if an error occurred
	 */
	private void initOrder() throws Exception {
		InitDataBase.LOG.info("initOrder");
		List<MealEntity> allMeals = this.mealService.findAll();
		List<MenuEntity> allMenus = this.menuService.findAll();
		List<UserEntity> allUsers = this.userService.findAll();
		// Remove constraint
		final Integer constraintId = Integer.valueOf(-1);
		final int nbOrder = 100;
		Random random = new Random();
		for (int i = 0; i < nbOrder; i++) {
			OrderDtoIn dto = new OrderDtoIn();
			UserEntity user = allUsers.get(random.nextInt(allUsers.size()));
			dto.setUserId(user.getId());
			dto.setConstraintId(constraintId);

			List<QuantityDtoIn> qps = new ArrayList<>();
			QuantityDtoIn qp = new QuantityDtoIn();
			if (random.nextBoolean()) {
				MenuEntity menu = allMenus.get(random.nextInt(allMenus.size()));
				qp.setQuantity(random.nextInt(2) + 1);
				qp.setMenuId(menu.getId());
			} else {
				List<MealEntity> someMeals = this.generateList(random.nextInt(3), allMeals);
				for (MealEntity meal : someMeals) {
					qp.setMealId(meal.getId());
					qp.setQuantity(random.nextInt(2) + 1);
					qps.add(qp);
				}
			}
			dto.setQuantity(qps);

			OrderEntity result = this.orderService.order(dto);
			Assertions.assertNotNull(result, "Result must exist");
			Assertions.assertNotNull(result.getId(), "Result must have an id");
		}
	}

	/**
	 * Initializes all data in data base.
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	@Disabled("Use only when you want to randomly reset a real data base (and not H2).")
	public void initDb() throws Exception {
		InitDataBase.LOG.warn("-- WILL INIT DATA BASE .....");
		if (!SpringBootConfiguration.usingH2(this.env)) {
			InitDataBase.LOG.warn("-- Write 'y' if you are sure and press enter");
			try (Scanner scanner = new Scanner(System.in);) {
				String line = scanner.next();
				if (line != null && ("y".equalsIgnoreCase(line.trim()) || "yes".equalsIgnoreCase(line.trim()))) {
					InitDataBase.LOG.warn("-- Let's do it then");
					this.init();
					this.initConstraint();
					this.initUser();
					this.initIngredient();
					this.initMeal();
					this.initMenu();
					this.initOrder();
				} else {
					InitDataBase.LOG.warn("-- It will be for an other time then ...");
				}
			} finally {
				// Do nothing, this is for scanner auto close
			}
		} else {
			InitDataBase.LOG.warn("initDb test is not for H2");
		}
	}

}
