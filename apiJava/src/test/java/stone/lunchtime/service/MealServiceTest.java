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

import stone.lunchtime.dto.in.MealDtoIn;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.test.AbstractTest;

/**
 * Meal service test class.
 */
public class MealServiceTest extends AbstractTest {

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek01() {
		List<MealEntity> result = this.mealService.findAllAvailableForWeek(Integer.valueOf(1));
		Assertions.assertNotNull(result, "Result must exists");
		Assertions.assertFalse(result.isEmpty(), "Result must not be empty");
	}

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek02() {
		Assertions.assertThrows(ParameterException.class,
				() -> this.mealService.findAllAvailableForWeek(Integer.valueOf(60)));
	}

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek03() {
		Assertions.assertThrows(ParameterException.class,
				() -> this.mealService.findAllAvailableForWeek(Integer.valueOf(0)));
	}

	/**
	 * Test
	 */
	@Test
	public void testFindAllAvailableForWeek04() {
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.findAllAvailableForWeek(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd01() throws Exception {
		List<IngredientEntity> allIngredients = super.ingredientService.findAll();
		List<Integer> allIngredientsId = super.transformInIdsList(allIngredients);
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		// dto.setIngredientsId(super.generateList(3, allIngredientsId));
		dto.setIngredientsId(super.generateList(3, allIngredientsId));
		MealEntity result = this.mealService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals(result.getIngredients().size(), dto.getIngredientsId().size(),
				"Result must have the correct number of ingredients");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd02() throws Exception {
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setIngredientsId(null);
		MealEntity result = this.mealService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertNull(result.getIngredients(), "Result must have the correct number of ingredients");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd03() throws Exception {
		List<IngredientEntity> allIngredients = super.ingredientService.findAll();
		List<Integer> allIngredientsId = super.transformInIdsList(allIngredients);
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setIngredientsId(super.generateList(3, allIngredientsId));
		Set<Integer> weeks = new HashSet<>();
		weeks.add(-1);
		dto.setAvailableForWeeks(weeks);
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.add(dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd04() throws Exception {
		List<IngredientEntity> allIngredients = super.ingredientService.findAll();
		List<Integer> allIngredientsId = super.transformInIdsList(allIngredients);
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(-2D));
		dto.setIngredientsId(super.generateList(3, allIngredientsId));
		Set<Integer> weeks = new HashSet<>();
		weeks.add(5);
		dto.setAvailableForWeeks(weeks);
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.add(dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd05() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.add(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate01() throws Exception {
		List<IngredientEntity> allIngredients = super.ingredientService.findAll();
		List<Integer> allIngredientsId = super.transformInIdsList(allIngredients);
		final Integer mealId = Integer.valueOf(1);
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("New Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setIngredientsId(super.generateList(3, allIngredientsId));
		result = this.mealService.update(mealId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), "Result must have the same id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals("New Test Meal", result.getLabel(), "Result must have the correct label");
		Assertions.assertEquals(result.getIngredients().size(), dto.getIngredientsId().size(),
				"Result must have the correct number of ingredients");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate02() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("New Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		result = this.mealService.update(mealId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), "Result must have the same id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals("New Test Meal", result.getLabel(), "Result must have the correct label");
		Assertions.assertNull(result.getIngredients(), "Result must have the correct number of ingredients");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate03() throws Exception {
		final Integer mealId = Integer.valueOf(1);
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("New Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(-2D));
		Assertions.assertThrows(ParameterException.class, () -> this.mealService.update(mealId, dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate04() throws Exception {
		List<IngredientEntity> allIngredients = super.ingredientService.findAll();
		List<Integer> allIngredientsId = super.transformInIdsList(allIngredients);
		final Integer mealId = Integer.valueOf(1);
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		MealDtoIn dto = new MealDtoIn();
		dto.setLabel("New Test Meal");
		dto.setPriceDF(BigDecimal.valueOf(2D));
		dto.setIngredientsId(super.generateList(3, allIngredientsId));
		Set<Integer> weeks = new HashSet<>();
		weeks.add(5);
		dto.setAvailableForWeeks(weeks);
		result = this.mealService.update(mealId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(mealId, result.getId(), "Result must have the same id");
		Assertions.assertTrue(result.isEnabled(), "Result must have the correct status");
		Assertions.assertEquals("New Test Meal", result.getLabel(), "Result must have the correct label");
		Assertions.assertEquals(result.getIngredients().size(), dto.getIngredientsId().size(),
				"Result must have the correct number of ingredients");
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
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertFalse(result.isDeleted(), "Result must not be deleted");
		result = this.mealService.delete(mealId);
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
		MealEntity result = this.mealService.find(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertFalse(result.isDeleted(), "Result must not be deleted");
		result = this.mealService.delete(mealId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertTrue(result.isDeleted(), "Result must be deleted");
		Assertions.assertThrows(InconsistentStatusException.class, () -> this.mealService.delete(mealId));
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
