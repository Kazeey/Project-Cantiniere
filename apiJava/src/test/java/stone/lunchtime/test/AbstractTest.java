// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IConstraintDao;
import stone.lunchtime.dao.IImageDao;
import stone.lunchtime.dao.IIngredientDao;
import stone.lunchtime.dao.IMealDao;
import stone.lunchtime.dao.IMenuDao;
import stone.lunchtime.dao.IOrderDao;
import stone.lunchtime.dao.IQuantityDao;
import stone.lunchtime.dao.IRoleDao;
import stone.lunchtime.dao.IUserDao;
import stone.lunchtime.entity.AbstractEntity;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.ConstraintService;
import stone.lunchtime.service.EmailService;
import stone.lunchtime.service.IAuthenticationService;
import stone.lunchtime.service.ImageService;
import stone.lunchtime.service.IngredientService;
import stone.lunchtime.service.MealService;
import stone.lunchtime.service.MenuService;
import stone.lunchtime.service.OrderService;
import stone.lunchtime.service.UserService;
import stone.lunchtime.spring.SpringBootConfiguration;

/**
 * Mother class of all tests.
 */
@SpringBootTest(classes = SpringBootConfiguration.class)
@Transactional
@Rollback(true)
@AutoConfigureMockMvc // Keep here for spring loader ...
public abstract class AbstractTest {
	@Autowired
	protected Environment env;

	@Autowired
	protected IAuthenticationService authenticationService;
	@Autowired
	protected UserService userService;
	@Autowired
	protected IngredientService ingredientService;
	@Autowired
	protected MealService mealService;
	@Autowired
	protected MenuService menuService;
	@Autowired
	protected ConstraintService constraintService;
	@Autowired
	protected OrderService orderService;
	@Autowired
	protected EmailService emailService;
	@Autowired
	protected ImageService imageService;

	@Autowired
	protected IUserDao userDao;
	@Autowired
	protected IOrderDao orderDao;
	@Autowired
	protected IConstraintDao constraintDao;
	@Autowired
	protected IIngredientDao ingredientDao;
	@Autowired
	protected IMenuDao menuDao;
	@Autowired
	protected IMealDao mealDao;
	@Autowired
	protected IQuantityDao quantityMealDao;
	@Autowired
	protected IRoleDao roleDao;
	@Autowired
	protected IImageDao imageDao;

	/**
	 * Takes random values from the given list and return them.
	 *
	 * @param pHowMany how many elements to give back
	 * @param pFrom    where to take elements
	 * @return a list with elements taken from the given list
	 */
	@Disabled("Not a test, used for simplification.")
	protected <T> List<T> generateList(int pHowMany, List<T> pFrom) {
		if (pHowMany != 0 && pFrom != null && !pFrom.isEmpty()) {
			final int fromSize = pFrom.size();
			if (pHowMany > fromSize) {
				List<T> copy = new ArrayList<>();
				Collections.copy(copy, pFrom);
				Collections.shuffle(copy);
				return copy;
			}
			Set<T> resu = new HashSet<>();
			Random random = new Random();
			while (pHowMany > 0) {
				if (resu.add(pFrom.get(random.nextInt(fromSize)))) {
					pHowMany--;
				}
			}
			return new ArrayList<>(resu);
		}
		return Collections.emptyList();
	}

	/**
	 * Transforms an entity list into there id's.
	 *
	 * @param pFrom where to take elements
	 * @return a list with entity's id
	 */
	@Disabled("Not a test, used for simplification.")
	protected List<Integer> transformInIdsList(List<? extends AbstractEntity> pFrom) {
		if (pFrom != null && !pFrom.isEmpty()) {
			List<Integer> resu = new ArrayList<>();
			for (AbstractEntity lEntity : pFrom) {
				resu.add(lEntity.getId());
			}
			return resu;
		}
		return Collections.emptyList();
	}

	/**
	 * Finds a simple user in the data base.
	 *
	 * A user in an Enabled state and not a lunch lady (will do the asser).
	 * @param idsToAvoid some ids to avoid
	 * @return a simpler user
	 */
	protected UserEntity findASimpleUser(Integer... idsToAvoid) {
		List<UserEntity> allUsers = this.userService.findAll();
		Collections.shuffle(allUsers);
		allUser: for (UserEntity user : allUsers) {
			Assertions.assertNotNull(user, "User must exist");
			if (user.isEnabled() && Boolean.FALSE.equals(user.getIsLunchLady())) {
				Assertions.assertEquals(EntityStatus.ENABLED, user.getStatus(), "User status must be ok");
				Assertions.assertFalse(user.getIsLunchLady(), "User must not be a lunch lady");
				if (idsToAvoid != null && idsToAvoid.length > 0) {
					for (Integer idToAvoid : idsToAvoid) {
						if (user.getId().equals(idToAvoid)) {
							continue allUser;
						}
					}
				}
				return user;
			} // End if not lunch lady
		}// for all users
		throw new IllegalStateException("No simple user found !");
	}
}
