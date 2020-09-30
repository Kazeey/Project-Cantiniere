// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import stone.lunchtime.dto.in.MenuDtoIn;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.test.AbstractTest;

/**
 * Menu service test class.
 */
public class MenuServiceTest extends AbstractTest {

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek01() {
		List<MenuEntity> result = this.menuService.findAllAvailableForWeek(Integer.valueOf(1));
		Assertions.assertNotNull(result, "Result must exists");
		Assertions.assertFalse(result.isEmpty(), "Result must not be empty");
	}

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek02() {
		Assertions.assertThrows(ParameterException.class,
				() -> this.menuService.findAllAvailableForWeek(Integer.valueOf(60)));
	}

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek03() {
		Assertions.assertThrows(ParameterException.class,
				() -> this.menuService.findAllAvailableForWeek(Integer.valueOf(0)));
	}

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek04() {
		Assertions.assertThrows(ParameterException.class, () -> this.menuService.findAllAvailableForWeek(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd01() throws Exception {
		List<MealEntity> allMeals = super.mealService.findAll();
		List<Integer> allMealsId = super.transformInIdsList(allMeals);
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setMealIds(super.generateList(3, allMealsId));
		MenuEntity result = this.menuService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals(result.getMeals().size(), dto.getMealIds().size(),
				"Result must have the correct number of meal");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd02() throws Exception {
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setMealIds(null);
		MenuEntity result = this.menuService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertNull(result.getMeals(), "Result must have the correct number of meal");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd03() throws Exception {
		List<MealEntity> allMeals = super.mealService.findAll();
		List<Integer> allMealsId = super.transformInIdsList(allMeals);
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setMealIds(super.generateList(3, allMealsId));
		Set<Integer> weeks = new HashSet<>();
		weeks.add(-1);
		dto.setAvailableForWeeks(weeks);
		Assertions.assertThrows(ParameterException.class, () -> this.menuService.add(dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd04() throws Exception {
		List<MealEntity> allMeals = super.mealService.findAll();
		List<Integer> allMealsId = super.transformInIdsList(allMeals);
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(-2D));
		dto.setMealIds(super.generateList(3, allMealsId));
		Set<Integer> weeks = new HashSet<>();
		weeks.add(5);
		dto.setAvailableForWeeks(weeks);
		Assertions.assertThrows(ParameterException.class, () -> this.menuService.add(dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd05() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.menuService.add(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate01() throws Exception {
		List<MealEntity> allMeals = super.mealService.findAll();
		List<Integer> allMealsId = super.transformInIdsList(allMeals);
		final Integer mealId = Integer.valueOf(1);
		MenuEntity result = this.menuService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("New Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setMealIds(super.generateList(3, allMealsId));
		result = this.menuService.update(mealId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), "Result must have the same id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals("New Test Menu", result.getLabel(), "Result must have the correct label");
		Assertions.assertEquals(result.getMeals().size(), dto.getMealIds().size(),
				"Result must have the correct number of meal");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate02() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MenuEntity result = this.menuService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("New Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		result = this.menuService.update(mealId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), "Result must have the same id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals("New Test Menu", result.getLabel(), "Result must have the correct label");
		Assertions.assertNull(result.getMeals(), "Result must have the correct number of meal");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate03() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MenuEntity result = this.menuService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("New Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(-2D));
		Assertions.assertThrows(ParameterException.class, () -> this.menuService.update(mealId, dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate04() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MenuEntity result = this.menuService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MenuDtoIn dto = new MenuDtoIn();
		dto.setLabel("New Test Menu");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		Set<Integer> weeks = new HashSet<>();
		weeks.add(5);
		dto.setAvailableForWeeks(weeks);
		result = this.menuService.update(mealId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), "Result must have the same id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals("New Test Menu", result.getLabel(), "Result must have the correct label");
		Assertions.assertNull(result.getMeals(), "Result must have the correct number of meal");
		Assertions.assertEquals(result.getAvailableForWeeksAsIntegerSet().size(), weeks.size(),
				"Result must have the correct number of week");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete01() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MenuEntity result = this.menuService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertFalse(result.isDeleted(), "Result must not be deleted");
		result = this.menuService.delete(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertTrue(result.isDeleted(), "Result must be deleted");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete02() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MenuEntity result = this.menuService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertFalse(result.isDeleted(), "Result must not be deleted");
		result = this.menuService.delete(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertTrue(result.isDeleted(), "Result must be deleted");
		Assertions.assertThrows(InconsistentStatusException.class, () -> this.menuService.delete(mealId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), () -> "Result must have " + mealId + " as id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind02() throws Exception {
		final Integer id = Integer.valueOf(1000000);
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.mealService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind03() throws Exception {
		final Integer id = null;
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind04() throws Exception {
		final Integer id = Integer.valueOf(-1);
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.find(id));
	}
}
