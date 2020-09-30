// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import stone.lunchtime.dto.in.ConstraintDtoIn;
import stone.lunchtime.dto.out.ConstraintDtoOut;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.spring.security.filter.SecurityConstants;
import stone.lunchtime.test.AbstractWebTest;

/**
 * Test for constraint controller, using Mock.
 */
public class ConstraintRestControllerTest extends AbstractWebTest {
	private static final String URL_ROOT = "/constraint";
	private static final String URL_ADD = ConstraintRestControllerTest.URL_ROOT + "/add";
	private static final String URL_DELETE = ConstraintRestControllerTest.URL_ROOT + "/delete/";
	private static final String URL_UPDATE = ConstraintRestControllerTest.URL_ROOT + "/update/";
	private static final String URL_FIND = ConstraintRestControllerTest.URL_ROOT + "/find/";
	private static final String URL_FINDALL = ConstraintRestControllerTest.URL_ROOT + "/findall";

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		Integer elmId = Integer.valueOf(1);

		ResultActions result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(ConstraintRestControllerTest.URL_FIND + elmId));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(elmId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind02() throws Exception {
		Integer elmId = Integer.valueOf(10000);
		ResultActions result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(ConstraintRestControllerTest.URL_FIND + elmId));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isPreconditionFailed());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind03() throws Exception {
		ResultActions result = super.mockMvc.perform(MockMvcRequestBuilders.get(ConstraintRestControllerTest.URL_FIND));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind04() throws Exception {
		Integer elmId = Integer.valueOf(-1);
		ResultActions result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(ConstraintRestControllerTest.URL_FIND + elmId));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAll01() throws Exception {
		ResultActions result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(ConstraintRestControllerTest.URL_FINDALL));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(Integer.valueOf(1)));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete01() throws Exception {
		Integer elmId = Integer.valueOf(1);

		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.delete(ConstraintRestControllerTest.URL_DELETE + elmId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		// No status for constraint, element is no more in data base
		Assertions.assertThrows(EntityNotFoundException.class, () -> this.constraintService.find(elmId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete02() throws Exception {
		// Connect a user
		ResultActions result = super.logMeInAsNormalRandomUser();

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.delete(ConstraintRestControllerTest.URL_DELETE + String.valueOf(1))
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testDelete03() throws Exception {
		Integer elmId = Integer.valueOf(10000);

		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.delete(ConstraintRestControllerTest.URL_DELETE + elmId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isPreconditionFailed());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd01() throws Exception {
		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		ObjectMapper mapper = new ObjectMapper();
		ConstraintDtoIn dto = new ConstraintDtoIn();
		dto.setMaximumOrderPerDay(Integer.valueOf(20));
		dto.setRateVAT(BigDecimal.valueOf(20D));
		dto.setOrderTimeLimit("11:00:00");
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(ConstraintRestControllerTest.URL_ADD)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
		ConstraintDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(),
				ConstraintDtoOut.class);

		ConstraintEntity entity = this.constraintService.find(dtoOut.getId());
		Assertions.assertNotNull(entity, "Result must exist");
		Assertions.assertNotNull(entity.getId(), "Result must have an id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd02() throws Exception {
		// Connect as lambda
		ResultActions result = super.logMeInAsNormalRandomUser();

		ObjectMapper mapper = new ObjectMapper();

		ConstraintDtoIn dto = new ConstraintDtoIn();
		dto.setMaximumOrderPerDay(Integer.valueOf(20));
		dto.setRateVAT(BigDecimal.valueOf(20D));
		dto.setOrderTimeLimit("11:00:00");
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(ConstraintRestControllerTest.URL_ADD)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testUpdate01() throws Exception {
		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();
		Integer elmId = Integer.valueOf(1);
		ConstraintDtoIn dto = new ConstraintDtoIn(super.constraintService.find(elmId));
		// Change mop
		dto.setMaximumOrderPerDay(Integer.valueOf(199));

		ObjectMapper mapper = new ObjectMapper();
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.patch(ConstraintRestControllerTest.URL_UPDATE + elmId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
		ConstraintDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(),
				ConstraintDtoOut.class);

		ConstraintEntity entity = this.constraintService.find(dtoOut.getId());
		Assertions.assertNotNull(entity, "Result must exist");
		Assertions.assertEquals(elmId, entity.getId(), "Result must have same id");
		Assertions.assertEquals(199, entity.getMaximumOrderPerDay().intValue(), "Result must have same changed value");
		Assertions.assertEquals(dto.getRateVAT(), entity.getRateVAT(), "Result must have same unchanged value");
		Assertions.assertEquals(dto.getOrderTimeLimitAsTime(), entity.getOrderTimeLimit(),
				"Result must have same unchanged value");
	}

	@Test
	public void testUpdate02() throws Exception {
		// Connect as lambda
		ResultActions result = super.logMeInAsNormalRandomUser();
		Integer elmId = Integer.valueOf(1);
		ConstraintDtoIn dto = new ConstraintDtoIn(super.constraintService.find(elmId));
		// Change mop
		dto.setMaximumOrderPerDay(Integer.valueOf(199));

		ObjectMapper mapper = new ObjectMapper();
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.patch(ConstraintRestControllerTest.URL_UPDATE + elmId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
}
