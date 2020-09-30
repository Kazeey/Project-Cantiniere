// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.IngredientDtoIn;
import stone.lunchtime.dto.out.ImageDtoOut;
import stone.lunchtime.dto.out.IngredientDtoOut;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.spring.security.filter.SecurityConstants;
import stone.lunchtime.test.AbstractWebTest;

/**
 * Test for ingredient controller, using Mock.
 */
public class IngredientRestControllerTest extends AbstractWebTest {
	private static final String URL_ROOT = "/ingredient";
	private static final String URL_ADD = IngredientRestControllerTest.URL_ROOT + "/add";
	private static final String URL_DELETE = IngredientRestControllerTest.URL_ROOT + "/delete/";
	private static final String URL_UPDATE = IngredientRestControllerTest.URL_ROOT + "/update/";
	private static final String URL_FIND = IngredientRestControllerTest.URL_ROOT + "/find/";
	private static final String URL_FINDALL = IngredientRestControllerTest.URL_ROOT + "/findall";
	private static final String URL_FIND_IMG = IngredientRestControllerTest.URL_ROOT + "/findimg/";
	private static final String URL_UPDATE_IMG = IngredientRestControllerTest.URL_ROOT + "/updateimg/";

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		Integer elmId = Integer.valueOf(1);

		ResultActions result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FIND + elmId));
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
				.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FIND + elmId));
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
		ResultActions result = super.mockMvc.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FIND));
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
				.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FIND + elmId));
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
		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		result = super.mockMvc.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FINDALL)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = IngredientDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<IngredientDtoOut> elements = mapper.readValue(content, type);

		Assertions.assertNotNull(elements, "List cannot be null");
		Assertions.assertFalse(elements.isEmpty(), "List cannot be empty");
		Assertions.assertEquals(28, elements.size(), "List size is 28");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAll02() throws Exception {
		ResultActions result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FINDALL));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	public void testFindAll03() throws Exception {
		// Connect as lambda
		ResultActions result = super.logMeInAsNormalRandomUser();

		result = super.mockMvc.perform(MockMvcRequestBuilders.get(IngredientRestControllerTest.URL_FINDALL)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));
		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete01() throws Exception {
		Integer elmId = Integer.valueOf(1);
		IngredientEntity entity = this.ingredientService.find(elmId);
		Assertions.assertNotNull(entity, "Entity is still in data base");
		Assertions.assertNotEquals(EntityStatus.DELETED, entity.getStatus(), "Status is not deleted");

		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.delete(IngredientRestControllerTest.URL_DELETE + elmId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		entity = this.ingredientService.find(elmId);
		Assertions.assertNotNull(entity, "Entity is still in data base");
		Assertions.assertNotNull(entity.getId(), "Entity still have an id");
		Assertions.assertEquals(EntityStatus.DELETED, entity.getStatus(), "Status is deleted");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDelete02() throws Exception {
		Integer elmId = Integer.valueOf(1);
		// Connect a user
		ResultActions result = super.logMeInAsNormalRandomUser();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.delete(IngredientRestControllerTest.URL_DELETE + elmId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testDelete03() throws Exception {
		Integer elmId = Integer.valueOf(1);
		IngredientEntity entity = this.ingredientService.find(elmId);
		Assertions.assertNotNull(entity, "Entity is still in data base");
		Assertions.assertNotEquals(EntityStatus.DELETED, entity.getStatus(), "Status is not deleted");
		entity = super.ingredientService.delete(elmId);
		Assertions.assertNotNull(entity, "Entity is still in data base");
		Assertions.assertEquals(EntityStatus.DELETED, entity.getStatus(), "Status is not deleted");

		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.delete(IngredientRestControllerTest.URL_DELETE + elmId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isPreconditionFailed());
	}

	@Test
	public void testDelete04() throws Exception {
		Integer elmId = Integer.valueOf(10000);

		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.delete(IngredientRestControllerTest.URL_DELETE + elmId)
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

		IngredientDtoIn dto = new IngredientDtoIn();
		dto.setDescription("test a description");
		dto.setLabel("A nice label");

		ObjectMapper mapper = new ObjectMapper();
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(IngredientRestControllerTest.URL_ADD)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
		IngredientDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(),
				IngredientDtoOut.class);

		IngredientEntity entity = this.ingredientService.find(dtoOut.getId());
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

		IngredientDtoIn dto = new IngredientDtoIn();
		dto.setDescription("test a description");
		dto.setLabel("A nice label");
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(IngredientRestControllerTest.URL_ADD)
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
		IngredientDtoIn dto = new IngredientDtoIn(super.ingredientService.find(elmId));
		// Change label
		dto.setLabel("test new label");

		ObjectMapper mapper = new ObjectMapper();
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.patch(IngredientRestControllerTest.URL_UPDATE + elmId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
		IngredientDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(),
				IngredientDtoOut.class);

		IngredientEntity entity = this.ingredientService.find(dtoOut.getId());
		Assertions.assertNotNull(entity, "Result must exist");
		Assertions.assertEquals(elmId, entity.getId(), "Result must have same id");
		Assertions.assertEquals("test new label", entity.getLabel(), "Result must have same changed value");
		Assertions.assertEquals(dto.getDescription(), entity.getDescription(), "Result must have same unchanged value");
	}

	@Test
	public void testUpdate02() throws Exception {
		// Connect as lambda
		ResultActions result = super.logMeInAsNormalRandomUser();
		Integer elmId = Integer.valueOf(1);
		IngredientDtoIn dto = new IngredientDtoIn(super.ingredientService.find(elmId));
		// Change label
		dto.setLabel("test new label");

		ObjectMapper mapper = new ObjectMapper();
		String dtoAsJsonString = mapper.writeValueAsString(dto);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.patch(IngredientRestControllerTest.URL_UPDATE + elmId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoAsJsonString)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindImg00() throws Exception {
		Integer elmId = super.getValidIngredient().getId();

		// The call
		ResultActions result = super.mockMvc.perform(MockMvcRequestBuilders
				.get(IngredientRestControllerTest.URL_FIND_IMG + elmId).contentType(MediaType.APPLICATION_JSON_VALUE));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		ObjectMapper mapper = new ObjectMapper();
		ImageDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), ImageDtoOut.class);
		Assertions.assertNotNull(dtoOut.getId(), "Image must have an id");
		Assertions.assertNotNull(dtoOut.getImagePath(), "Image must have a path");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImg00() throws Exception {
		// Connect as lunch lady
		ResultActions result = super.logMeInAsLunchLady();

		IngredientEntity elm = super.getValidIngredient();
		Integer oldImgId = elm.getImage().getId();

		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(IngredientRestControllerTest.URL_UPDATE_IMG + elm.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		IngredientDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(),
				IngredientDtoOut.class);
		Assertions.assertNotNull(dtoOut.getImageId(), "Image must have an id");

		ImageEntity ie = super.imageService.find(dtoOut.getImageId());

		Assertions.assertNotNull(ie.getImagePath(), "Image must have a path");
		Assertions.assertFalse(ie.getIsDefault(), "Image should NOT be a default one");
		Assertions.assertNotNull(ie.getImageBin(), "Image should have a blob");
		// We updated a non default image, so id is the same
		Assertions.assertEquals(oldImgId, ie.getId(), "Image id must be the same");
		Assertions.assertEquals(dtoIn.getImagePath(), ie.getImagePath(), "Image should have the same path");
		Assertions.assertEquals(dtoIn.getImage64(), ie.getImage64(), "Image should have the same base 64");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdateImg01() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		IngredientEntity elm = super.getValidIngredient();

		ImageDtoIn dtoIn = new ImageDtoIn();
		dtoIn.setImagePath("img/test.png");
		dtoIn.setImage64(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAB9SURBVBhXAXIAjf8Bsry+/fz7z7yx8/LxNEVNERUaAgMEBMSxpRXy1xr/6uLDsAIDAgQDAgIiEQc3HSwaICXa6fP7AwQCAQEBGwkF9vf97ebpFBMUBAIBAv/27sPMyeTj6urr9t3h4QEBAgNLLQv/9u/g6O319/Ts6uMMHyvQyzf6YLHUTAAAAABJRU5ErkJggg==");
		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(IngredientRestControllerTest.URL_UPDATE_IMG + elm.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

}
