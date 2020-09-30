// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.controller.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import stone.lunchtime.dto.in.OrderDtoIn;
import stone.lunchtime.dto.in.QuantityDtoIn;
import stone.lunchtime.dto.out.OrderDtoOut;
import stone.lunchtime.dto.out.PriceDtoOut;
import stone.lunchtime.dto.out.QuantityDtoOut;
import stone.lunchtime.entity.OrderEntity;
import stone.lunchtime.entity.OrderStatus;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.spring.security.filter.SecurityConstants;
import stone.lunchtime.test.AbstractWebTest;

/**
 * Test for order controller, using Mock.
 */
public class OrderRestControllerTest extends AbstractWebTest {
	private static final String URL_ROOT = "/order";
	private static final String URL_FINDALLBETWEENDATEINSTATUS = OrderRestControllerTest.URL_ROOT
			+ "/findallbetweendateinstatus";
	private static final String URL_FINDALLFORUSER = OrderRestControllerTest.URL_ROOT + "/findallforuser/";
	private static final String URL_FINDALLFORUSERTODAY = OrderRestControllerTest.URL_ROOT + "/findallforusertoday/";
	private static final String URL_ADD = OrderRestControllerTest.URL_ROOT + "/add";
	private static final String URL_CANCEL = OrderRestControllerTest.URL_ROOT + "/cancel/";
	private static final String URL_COMPUTEPRICE = OrderRestControllerTest.URL_ROOT + "/computeprice/";
	private static final String URL_FIND = OrderRestControllerTest.URL_ROOT + "/find/";
	private static final String URL_FINDALL = OrderRestControllerTest.URL_ROOT + "/findall";
	private static final String URL_PAY = OrderRestControllerTest.URL_ROOT + "/deliverandpay/";
	private static final String URL_UPDATE = OrderRestControllerTest.URL_ROOT + "/update/";

	@Value("${configuration.date.pattern}")
	private String datePattern;

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAllBetweenDateInStatus01() throws Exception {
		final Byte status = OrderStatus.CREATED.getValue();

		LocalDate begin = LocalDate.of(2019, Month.JANUARY, 1);
		LocalDate end = LocalDate.now();

		final String beginDate = begin.format(DateTimeFormatter.ofPattern(this.datePattern));
		final String endDate = end.format(DateTimeFormatter.ofPattern(this.datePattern));

		// Connect as Lunch Lady
		ResultActions result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders
				.get(OrderRestControllerTest.URL_FINDALLBETWEENDATEINSTATUS)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)).param("status", String.valueOf(status))
				.param("beginDate", beginDate).param("endDate", endDate));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(status, dto.getStatus(), "Order status is as searched");
			LocalDate creationDate = dto.getCreationDate();
			Assertions.assertTrue(creationDate.isEqual(end) || creationDate.isBefore(end),
					"Order date is before the specified end date");
			Assertions.assertTrue(creationDate.isEqual(begin) || creationDate.isAfter(begin),
					"Order date is after the specified begin date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAllBetweenDateInStatus02() throws Exception {
		final Byte status = OrderStatus.CREATED.getValue();
		final String beginDate = "2019-01-01";
		final String endDate = "2019-05-01";

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders
				.get(OrderRestControllerTest.URL_FINDALLBETWEENDATEINSTATUS)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)).param("status", String.valueOf(status))
				.param("beginDate", beginDate).param("endDate", endDate));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd01() throws Exception {

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(OrderRestControllerTest.URL_ADD)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CREATED.getValue(), dtoOut.getStatus(), "Order status must created");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(1, dtoOut.getQuantity().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd02() throws Exception {

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with quantity meal
		List<QuantityDtoIn> dtoInQuantity = new ArrayList<>();
		QuantityDtoIn q1 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q1);
		QuantityDtoIn q2 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q2);
		QuantityDtoIn q3 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q3);
		dtoIn.setQuantity(dtoInQuantity);
		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(OrderRestControllerTest.URL_ADD)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CREATED.getValue(), dtoOut.getStatus(), "Order status must created");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");
		List<QuantityDtoOut> dtoOutQuantityMeals = dtoOut.getQuantity();
		Assertions.assertNotNull(dtoOutQuantityMeals, "No quantity meals is not null");
		Assertions.assertEquals(dtoInQuantity.size(), dtoOutQuantityMeals.size(), "Quantity meals has the good size");
		for (int i = 0; i < dtoInQuantity.size(); i++) {
			QuantityDtoOut qmdout = dtoOutQuantityMeals.get(i);
			QuantityDtoIn qmdin = dtoInQuantity.get(i);
			Assertions.assertEquals(qmdin.getQuantity(), qmdout.getQuantity(), "Quantity meals has the good quantity");
			Assertions.assertEquals(qmdin.getMealId(), qmdout.getMeal().getId(), "Quantity meals has the good meal");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd03() throws Exception {

		// Not connected

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(Integer.valueOf(1));
		// Order with quantity meal
		List<QuantityDtoIn> dtoInQuantity = new ArrayList<>();
		QuantityDtoIn q1 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q1);
		QuantityDtoIn q2 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q2);
		QuantityDtoIn q3 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q3);
		dtoIn.setQuantity(dtoInQuantity);
		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		ResultActions result = super.mockMvc.perform(MockMvcRequestBuilders.put(OrderRestControllerTest.URL_ADD)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testAdd04() throws Exception {

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();
		Integer userId = super.getUserIdInToken(result);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Do not set the user id
		// dtoIn.setUserId(userId);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.put(OrderRestControllerTest.URL_ADD)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CREATED.getValue(), dtoOut.getStatus(), "Order status must created");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(1, dtoOut.getQuantity().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		// User id should be the one of the connected user
		Assertions.assertEquals(userId, dtoOut.getUser().getId(), "User id must be the one selected");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCancel01() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(1, orderCreated.getQuantityEntities().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// The call, cancel the order passed
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_CANCEL + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		ObjectMapper mapper = new ObjectMapper();
		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CANCELED.getValue(), dtoOut.getStatus(), "Order status must canceled");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(1, dtoOut.getQuantity().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCancel02() throws Exception {

		final Integer userIdPassingOrder = Integer.valueOf(1);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Pass the order as user 1
		dtoIn.setUserId(userIdPassingOrder);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(1, orderCreated.getQuantityEntities().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(userIdPassingOrder, orderCreated.getUser().getId(), "User id must be the one selected");

		// Now, Connect as simple user that is not the one passing the order
		ResultActions result = super.logMeInAsNormalRandomUser();
		// The asserts
		Assertions.assertNotEquals(userIdPassingOrder, super.getUserIdInToken(result),
				"Connected user is not the one that ordered");

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_CANCEL + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testCancel03() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		final Integer userIdPassingOrder = super.getUserIdInToken(result);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Pass the order as a simple user
		dtoIn.setUserId(userIdPassingOrder);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(1, orderCreated.getQuantityEntities().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(userIdPassingOrder, orderCreated.getUser().getId(), "User id must be the one selected");

		// Now, Connect as lunch lady
		result = super.logMeInAsLunchLady();
		// The asserts
		Assertions.assertFalse(super.getUserIdInToken(result).equals(userIdPassingOrder),
				"Connected user is not the one that ordered");

		// Call a cancel as a lunch lady
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_CANCEL + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		ObjectMapper mapper = new ObjectMapper();
		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CANCELED.getValue(), dtoOut.getStatus(), "Order status must canceled");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(1, dtoOut.getQuantity().size(), "Quantity muste have the right size");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testComputePrice01() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// The call
		result = super.mockMvc.perform(
				MockMvcRequestBuilders.get(OrderRestControllerTest.URL_COMPUTEPRICE + orderCreated.getId() + "/-1")
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		ObjectMapper mapper = new ObjectMapper();
		PriceDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), PriceDtoOut.class);
		Assertions.assertNotNull(dtoOut.getPriceDF(), "Price have value");
		Assertions.assertTrue(dtoOut.getPriceDF().doubleValue() > 0, "Price must be > 0");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testComputePrice02() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// The call, with constraint
		result = super.mockMvc.perform(
				MockMvcRequestBuilders.get(OrderRestControllerTest.URL_COMPUTEPRICE + orderCreated.getId() + "/1")
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		ObjectMapper mapper = new ObjectMapper();
		PriceDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), PriceDtoOut.class);
		Assertions.assertNotNull(dtoOut.getPriceDF(), "Price have value");
		Assertions.assertTrue(dtoOut.getPriceDF().doubleValue() > 0, "Price must be > 0");
		Assertions.assertNotNull(dtoOut.getPriceVAT(), "Price have value");
		Assertions.assertTrue(dtoOut.getPriceVAT().doubleValue() > 0, "Price must be > 0");
		Assertions.assertTrue(dtoOut.getPriceDF().doubleValue() < dtoOut.getPriceVAT().doubleValue(),
				"Price must respect DF<VAT");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testComputePrice03() throws Exception {

		final Integer userIdPassingOrder = Integer.valueOf(1);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Pass the order as user 1
		dtoIn.setUserId(userIdPassingOrder);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Menu cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(userIdPassingOrder, orderCreated.getUser().getId(), "User id must be the one selected");

		// Now, Connect as simple user that is not the one passing the order
		ResultActions result = super.logMeInAsNormalRandomUser();
		// The asserts
		Assertions.assertFalse(super.getUserIdInToken(result).equals(userIdPassingOrder),
				"Connected user is not the one that ordered");

		// The call
		result = super.mockMvc.perform(
				MockMvcRequestBuilders.get(OrderRestControllerTest.URL_COMPUTEPRICE + orderCreated.getId() + "/1")
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testComputePrice04() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		final Integer userIdPassingOrder = super.getUserIdInToken(result);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Pass the order as a simple user
		dtoIn.setUserId(userIdPassingOrder);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(userIdPassingOrder, orderCreated.getUser().getId(), "User id must be the one selected");

		// Now, Connect as lunch lady
		result = super.logMeInAsLunchLady();
		// The asserts
		Assertions.assertFalse(super.getUserIdInToken(result).equals(userIdPassingOrder),
				"Connected user is not the one that ordered");

		// Call a cancel as a lunch lady
		result = super.mockMvc.perform(
				MockMvcRequestBuilders.get(OrderRestControllerTest.URL_COMPUTEPRICE + orderCreated.getId() + "/1")
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		ObjectMapper mapper = new ObjectMapper();
		PriceDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), PriceDtoOut.class);
		Assertions.assertNotNull(dtoOut.getPriceDF(), "Price have value");
		Assertions.assertTrue(dtoOut.getPriceDF().doubleValue() > 0, "Price must be > 0");
		Assertions.assertNotNull(dtoOut.getPriceVAT(), "Price have value");
		Assertions.assertTrue(dtoOut.getPriceVAT().doubleValue() > 0, "Price must be > 0");
		Assertions.assertTrue(dtoOut.getPriceDF().doubleValue() < dtoOut.getPriceVAT().doubleValue(),
				"Price must respect DF<VAT");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind01() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// The call, cancel the order passed
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FIND + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		ObjectMapper mapper = new ObjectMapper();
		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind02() throws Exception {

		final Integer userIdPassingOrder = Integer.valueOf(1);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Pass the order as user 1
		dtoIn.setUserId(userIdPassingOrder);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(userIdPassingOrder, orderCreated.getUser().getId(), "User id must be the one selected");

		// Now, Connect as simple user that is not the one passing the order
		ResultActions result = super.logMeInAsNormalRandomUser();
		// The asserts
		Assertions.assertFalse(super.getUserIdInToken(result).equals(userIdPassingOrder),
				"Connected user is not the one that ordered");

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FIND + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFind03() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		final Integer userIdPassingOrder = super.getUserIdInToken(result);
		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		// Pass the order as a simple user
		dtoIn.setUserId(userIdPassingOrder);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(userIdPassingOrder, orderCreated.getUser().getId(), "User id must be the one selected");

		// Now, Connect as lunch lady
		result = super.logMeInAsLunchLady();
		// The asserts
		Assertions.assertFalse(super.getUserIdInToken(result).equals(userIdPassingOrder),
				"Connected user is not the one that ordered");

		// Call a cancel as a lunch lady
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FIND + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		ObjectMapper mapper = new ObjectMapper();
		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");

	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testPay01() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_PAY + orderCreated.getId() + "/-1")
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
	public void testPay02() throws Exception {
		// Remove all money to a user
		UserEntity user = super.findASimpleUser();
		user.setWallet(BigDecimal.ZERO);
		super.userDao.save(user);

		// Connect as the broke user
		ResultActions result = super.logMeIn(user.getEmail(), user.getPassword());

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with quantity meal
		List<QuantityDtoIn> dtoInQuantity = new ArrayList<>();
		QuantityDtoIn q1 = new QuantityDtoIn(Integer.valueOf(10), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q1);
		QuantityDtoIn q2 = new QuantityDtoIn(Integer.valueOf(10), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q2);
		QuantityDtoIn q3 = new QuantityDtoIn(Integer.valueOf(10), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q3);
		dtoIn.setQuantity(dtoInQuantity);

		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");

		// Connect as lunch lady
		result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_PAY + orderCreated.getId() + "/-1")
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));
		// Not enough money
		result.andExpect(MockMvcResultMatchers.status().isPreconditionFailed());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testPay03() throws Exception {
		// Give lot of money to a user
		UserEntity user = super.findASimpleUser();
		// Decimal(5,2) = 3 dig max
		user.setWallet(BigDecimal.valueOf(999D));
		super.userDao.save(user);

		// Connect as simple user
		ResultActions result = super.logMeIn(user.getEmail(), user.getPassword());
		Integer userId = super.getUserIdInToken(result);
		BigDecimal walletBefore = super.getUserInToken(result).getWallet();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// Connect as lunch lady
		result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_PAY + orderCreated.getId() + "/-1")
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		ObjectMapper mapper = new ObjectMapper();
		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.DELIVERED.getValue(), dtoOut.getStatus(), "Order status must delivered");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");

		user = super.userService.find(userId);

		BigDecimal walletAfter = user.getWallet();
		Assertions.assertTrue(walletBefore.doubleValue() >= walletAfter.doubleValue(),
				"Wallet must be less than before");
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

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALL)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");
		Assertions.assertEquals(100, orders.size(), "Order list size is 100");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAll02() throws Exception {
		// Connect as standard user
		ResultActions result = super.logMeInAsNormalRandomUser();

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALL)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testUpdate01() throws Exception {

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		dtoIn = new OrderDtoIn(orderCreated, dtoIn.getConstraintId());
		// Add meal
		List<QuantityDtoIn> dtoInQuantity = dtoIn.getQuantity();
		QuantityDtoIn q1 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q1);
		QuantityDtoIn q2 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q2);

		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_UPDATE + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CREATED.getValue(), dtoOut.getStatus(), "Order status must created");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");
		List<QuantityDtoOut> dtoOutQuantityMeals = dtoOut.getQuantity();
		Assertions.assertNotNull(dtoOutQuantityMeals, "No quantity meals is not null");
		Assertions.assertEquals(dtoInQuantity.size(), dtoOutQuantityMeals.size(), "Quantity meals has the good size");
		// Start at one in order to jump the menu
		for (int i = 1; i < dtoInQuantity.size(); i++) {
			Assertions.assertEquals(dtoInQuantity.get(i).getQuantity(), dtoOutQuantityMeals.get(i).getQuantity(),
					"Quantity meals has the good quantity");
			Assertions.assertEquals(dtoInQuantity.get(i).getMealId(), dtoOutQuantityMeals.get(i).getMeal().getId(),
					"Quantity meals has the good meal");
		}
	}

	@Test
	public void testUpdate02() throws Exception {

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(super.getUserIdInToken(result));
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		dtoIn = new OrderDtoIn(orderCreated, dtoIn.getConstraintId());
		// Add meal
		List<QuantityDtoIn> dtoInQuantity = dtoIn.getQuantity();
		QuantityDtoIn q1 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q1);
		QuantityDtoIn q2 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q2);

		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// Connect as lunch lady
		result = super.logMeInAsLunchLady();

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_UPDATE + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

		OrderDtoOut dtoOut = mapper.readValue(result.andReturn().getResponse().getContentAsString(), OrderDtoOut.class);
		Assertions.assertEquals(OrderStatus.CREATED.getValue(), dtoOut.getStatus(), "Order status must created");
		Assertions.assertNotNull(dtoOut.getQuantity(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, dtoOut.getQuantity().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), dtoOut.getUser().getId(), "User id must be the one selected");
		List<QuantityDtoOut> dtoOutQuantityMeals = dtoOut.getQuantity();
		Assertions.assertNotNull(dtoOutQuantityMeals, "No quantity meals is not null");
		Assertions.assertEquals(dtoInQuantity.size(), dtoOutQuantityMeals.size(), "Quantity meals has the good size");
		// Start at one in order to jump the menu
		for (int i = 1; i < dtoInQuantity.size(); i++) {
			Assertions.assertEquals(dtoInQuantity.get(i).getQuantity(), dtoOutQuantityMeals.get(i).getQuantity(),
					"Quantity meals has the good quantity");
			Assertions.assertEquals(dtoInQuantity.get(i).getMealId(), dtoOutQuantityMeals.get(i).getMeal().getId(),
					"Quantity meals has the good meal");
		}
	}

	@Test
	public void testUpdate03() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();
		Integer userId = super.getUserIdInToken(result);

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(userId);
		final Integer menuId = super.getValidMenu(false).getId();
		// Order with a menu
		dtoIn.addMenu(1, menuId);
		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Quantity cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		dtoIn = new OrderDtoIn(orderCreated, dtoIn.getConstraintId());
		// Add meal
		List<QuantityDtoIn> dtoInQuantity = new ArrayList<>();
		QuantityDtoIn q1 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q1);
		QuantityDtoIn q2 = new QuantityDtoIn(Integer.valueOf(1), super.getValidMeal(false).getId(), null);
		dtoInQuantity.add(q2);
		dtoIn.setQuantity(dtoInQuantity);

		ObjectMapper mapper = new ObjectMapper();
		String dtoInAsJsonString = mapper.writeValueAsString(dtoIn);

		// Connect as an other simple user
		result = super.logMeInAsNormalRandomUser(userId);
		Assertions.assertNotEquals(userId, super.getUserIdInToken(result), "Not the same user");

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.patch(OrderRestControllerTest.URL_UPDATE + orderCreated.getId())
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result))
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(dtoInAsJsonString));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAllForUser01() throws Exception {
		UserEntity user = super.findASimpleUser();
		while (super.orderService.findAllByUserId(user.getId()).isEmpty()) {
			user = super.findASimpleUser();
		}

		// Connect as simple user
		ResultActions result = super.logMeIn(user.getEmail(), user.getPassword());

		Integer userId = super.getUserIdInToken(result);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSER + userId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(userId, dto.getUser().getId(), "Order has the specified user id");
		}
	}

	@Test
	public void testFindAllForUser02() throws Exception {
		final Byte status = OrderStatus.CREATED.getValue();
		UserEntity user = super.findASimpleUser();
		while (super.orderService.findAllByUserId(user.getId()).isEmpty()) {
			user = super.findASimpleUser();
		}

		// Connect as simple user
		ResultActions result = super.logMeIn(user.getEmail(), user.getPassword());
		Integer userId = super.getUserIdInToken(result);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSER + userId)
				.param("status", String.valueOf(status)).header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(userId, dto.getUser().getId(), "Order has the specified user id");
			Assertions.assertEquals(status, dto.getStatus(), "Order status is as searched");
		}
	}

	@Test
	public void testFindAllForUser03() throws Exception {
		final Byte status = OrderStatus.CREATED.getValue();

		LocalDate begin = LocalDate.of(2019, Month.JANUARY, 1);
		LocalDate end = LocalDate.now();

		final String beginDate = begin.format(DateTimeFormatter.ofPattern(this.datePattern));
		final String endDate = end.format(DateTimeFormatter.ofPattern(this.datePattern));

		UserEntity user = super.findASimpleUser();
		while (super.orderService.findAllByUserId(user.getId()).isEmpty()) {
			user = super.findASimpleUser();
		}

		// Connect as simple user
		ResultActions result = super.logMeIn(user.getEmail(), user.getPassword());
		Integer userId = super.getUserIdInToken(result);

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSER + userId)
				.param("status", String.valueOf(status)).param("beginDate", beginDate).param("endDate", endDate)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(status, dto.getStatus(), "Order status is as searched");
			LocalDate creationDate = dto.getCreationDate();
			Assertions.assertTrue(creationDate.isEqual(end) || creationDate.isBefore(end),
					"Order date is before the specified end date");
			Assertions.assertTrue(creationDate.isEqual(begin) || creationDate.isAfter(begin),
					"Order date is after the specified begin date");
		}
	}

	@Test
	public void testFindAllForUser04() throws Exception {
		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();

		// The call with no user id
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSER)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts, userid is mandatory
		result.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testFindAllForUser05() throws Exception {
		// Connect as simple user that had some orders
		ResultActions result = super.logMeInAsLunchLady();
		Integer userId = super.findASimpleUser().getId();
		List<OrderEntity> ordersEnt = super.orderService.findAllByUserId(userId);
		while (ordersEnt == null || ordersEnt.isEmpty()) {
			userId = super.findASimpleUser().getId();
			ordersEnt = super.orderService.findAllByUserId(userId);
		}

		// The call
		result = super.mockMvc.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSER + userId)
				.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(userId, dto.getUser().getId(), "Order has the specified user id");
		}
	}

	@Test
	public void testFindAllForUserToday01() throws Exception {
		final Byte status = OrderStatus.CREATED.getValue();

		// Connect as simple user
		ResultActions result = super.logMeInAsNormalRandomUser();
		Integer userId = super.getUserIdInToken(result);

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(userId);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);

		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Menu cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSERTODAY + userId)
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(status, dto.getStatus(), "Order status is as searched");
			LocalDate creationDate = dto.getCreationDate();
			Assertions.assertEquals(LocalDate.now(), creationDate, "Order date is today the specified date");
		}
	}

	@Test
	public void testFindAllForUserToday02() throws Exception {
		final Byte status = OrderStatus.CREATED.getValue();

		Integer userId = Integer.valueOf(50);

		OrderDtoIn dtoIn = new OrderDtoIn();
		dtoIn.setConstraintId(Integer.valueOf(-1));
		dtoIn.setUserId(userId);
		// Order with a menu
		final Integer menuId = super.getValidMenu(false).getId();
		dtoIn.addMenu(1, menuId);

		OrderEntity orderCreated = super.orderService.order(dtoIn);
		Assertions.assertEquals(OrderStatus.CREATED, orderCreated.getStatus(), "Order status must created");
		Assertions.assertNotNull(orderCreated.getQuantityEntities(), "Menu cannot be null");
		Assertions.assertEquals(menuId, orderCreated.getQuantityEntities().get(0).getMenu().getId(),
				"Menu id must be the one selected");
		Assertions.assertEquals(dtoIn.getUserId(), orderCreated.getUser().getId(), "User id must be the one selected");

		// Connect as lunch lady
		ResultActions result = super.logMeInAsLunchLady();
		// The call
		result = super.mockMvc
				.perform(MockMvcRequestBuilders.get(OrderRestControllerTest.URL_FINDALLFORUSERTODAY + userId)
						.header(SecurityConstants.TOKEN_HEADER, super.getJWT(result)));

		// The asserts
		result.andExpect(MockMvcResultMatchers.status().isOk());

		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Class<?> clz = OrderDtoOut.class;
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		List<OrderDtoOut> orders = mapper.readValue(content, type);

		Assertions.assertNotNull(orders, "Order list cannot be null");
		Assertions.assertFalse(orders.isEmpty(), "Order list cannot be empty");

		for (OrderDtoOut dto : orders) {
			Assertions.assertNotNull(dto, "Order cannot be null");
			Assertions.assertEquals(status, dto.getStatus(), "Order status is as searched");
			LocalDate creationDate = dto.getCreationDate();
			Assertions.assertEquals(LocalDate.now(), creationDate, "Order date is today the specified date");
		}
	}

}
