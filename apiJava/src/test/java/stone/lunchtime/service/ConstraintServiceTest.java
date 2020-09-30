// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import stone.lunchtime.dto.in.ConstraintDtoIn;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.test.AbstractTest;

/**
 * Constraint service test class.
 */
public class ConstraintServiceTest extends AbstractTest {

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		final Integer cId = Integer.valueOf(1);
		ConstraintEntity result = this.constraintService.find(cId);
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
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.constraintService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind03() throws Exception {
		final Integer id = null;
		Assertions.assertThrows(ParameterException.class, () -> this.constraintService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind04() throws Exception {
		final Integer id = Integer.valueOf(-1);
		Assertions.assertThrows(ParameterException.class, () -> this.constraintService.find(id));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd01() throws Exception {
		ConstraintDtoIn dto = new ConstraintDtoIn();
		dto.setMaximumOrderPerDay(Integer.valueOf(20));
		dto.setRateVAT(BigDecimal.valueOf(20D));
		dto.setOrderTimeLimit("11:00:00");
		ConstraintEntity result = this.constraintService.add(dto);
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
		ConstraintDtoIn dto = new ConstraintDtoIn();
		ConstraintEntity result = this.constraintService.add(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertNotNull(result.getMaximumOrderPerDay(), "Result must have all constraint set");
		Assertions.assertNotNull(result.getOrderTimeLimit(), "Result must have all constraint set");
		Assertions.assertNotNull(result.getRateVAT(), "Result must have all constraint set");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd03() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.constraintService.add(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete01() throws Exception {
		final Integer cId = Integer.valueOf(1);
		ConstraintEntity result = this.constraintService.find(cId);
		Assertions.assertNotNull(result, "Result must exist");
		this.constraintService.delete(cId);
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.constraintService.find(cId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate01() throws Exception {
		final Integer cId = Integer.valueOf(1);
		ConstraintEntity result = this.constraintService.find(cId);
		Assertions.assertNotNull(result, "Result must exist");
		ConstraintDtoIn dto = new ConstraintDtoIn();
		dto.setMaximumOrderPerDay(Integer.valueOf(20));
		dto.setRateVAT(BigDecimal.valueOf(50D));
		dto.setOrderTimeLimit("11:00:00");
		result = this.constraintService.update(cId, dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertEquals(cId, result.getId(), "Result must have the same id");
		Assertions.assertEquals(BigDecimal.valueOf(50D), result.getRateVAT(), "Result must have the correct rate");
		// TODO FixMultiLine
		Assertions.assertEquals(result.getMaximumOrderPerDay(), dto.getMaximumOrderPerDay(),
				"Result must have the correct number of MaximumOrderPerDay");

	}
}
