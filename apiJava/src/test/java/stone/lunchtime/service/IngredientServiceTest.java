// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.IngredientDtoIn;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.test.AbstractTest;

/**
 * Ingredient service test class.
 */
public class IngredientServiceTest extends AbstractTest {

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		final Integer cId = Integer.valueOf(1);
		IngredientEntity result = this.ingredientService.find(cId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(cId, result.getId(), () -> "Result must have " + cId + " as id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind02() throws Exception {
		final Integer id = Integer.valueOf(1000000);
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.ingredientService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind03() throws Exception {
		final Integer id = null;
		Assertions.assertThrows(ParameterException.class, () -> this.ingredientService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind04() throws Exception {
		final Integer id = Integer.valueOf(-1);
		Assertions.assertThrows(ParameterException.class, () -> this.ingredientService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd01() throws Exception {
		IngredientDtoIn dto = new IngredientDtoIn();
		dto.setLabel("New Ingredient");
		IngredientEntity result = this.ingredientService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd02() throws Exception {
		IngredientDtoIn dto = new IngredientDtoIn();
		Assertions.assertThrows(ParameterException.class, () -> this.ingredientService.add(dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd03() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.ingredientService.add(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete01() throws Exception {
		final Integer cId = Integer.valueOf(1);
		IngredientEntity result = this.ingredientService.find(cId);
		Assertions.assertFalse(result.isDeleted(), "Result must not be deleted");
		Assertions.assertNotNull(result, "Result must exist");
		result = this.ingredientService.delete(cId);
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
		final Integer cId = Integer.valueOf(1);
		IngredientEntity result = this.ingredientService.find(cId);
		Assertions.assertFalse(result.isDeleted(), "Result must not be deleted");
		Assertions.assertNotNull(result, "Result must exist");
		result = this.ingredientService.delete(cId);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertTrue(result.isDeleted(), "Result must be deleted");
		Assertions.assertThrows(InconsistentStatusException.class, () -> this.ingredientService.delete(cId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate01() throws Exception {
		final Integer cId = Integer.valueOf(1);
		IngredientEntity result = this.ingredientService.find(cId);
		Assertions.assertNotNull(result, "Result must exist");
		IngredientDtoIn dto = new IngredientDtoIn();
		dto.setLabel("a new label");
		result = this.ingredientService.update(cId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(cId, result.getId(), "Result must have the same id");
		Assertions.assertEquals("a new label", result.getLabel(), "Result must have the correct label");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage01() throws Exception {
		final Integer id = Integer.valueOf(1);
		IngredientEntity result = this.ingredientService.find(id);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(id, result.getId(), () -> "Result must have " + id + " as id");
		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		result = this.ingredientService.updateImage(id, dtoIn);
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
		Assertions.assertThrows(ParameterException.class, () -> this.ingredientService.updateImage(id, dtoIn));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage03() throws Exception {
		final Integer id = Integer.valueOf(1);
		ImageDtoIn dtoIn = null;
		Assertions.assertThrows(ParameterException.class, () -> this.ingredientService.updateImage(id, dtoIn));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImage04() throws Exception {
		final Integer id = Integer.valueOf(1);
		IngredientEntity ie = super.ingredientService.find(id);
		ie.setStatus(EntityStatus.DELETED);
		super.ingredientDao.save(ie);
		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		Assertions.assertThrows(InconsistentStatusException.class, () -> this.ingredientService.updateImage(id, dtoIn));
	}

}
