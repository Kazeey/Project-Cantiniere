// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import stone.lunchtime.dto.in.OrderDtoIn;
import stone.lunchtime.dto.in.QuantityDtoIn;
import stone.lunchtime.dto.out.PriceDtoOut;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.entity.OrderEntity;
import stone.lunchtime.entity.OrderStatus;
import stone.lunchtime.entity.QuantityEntity;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.LackOfMoneyException;
import stone.lunchtime.service.exception.OrderCanceledException;
import stone.lunchtime.service.exception.OrderDelivredException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.service.exception.TimeOutException;
import stone.lunchtime.test.AbstractTest;

/**
 * Test class for order service.
 */
public class OrderServiceTest extends AbstractTest {

	private final static int THIS_WEEK = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR);

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testOrder01() throws Exception {
		UserEntity user = super.findASimpleUser();
		List<MenuEntity> allMenus = this.menuService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		// Remove constraint
		final Integer constraintId = Integer.valueOf(-1);
		OrderDtoIn dto = new OrderDtoIn();
		dto.setUserId(user.getId());
		dto.setConstraintId(constraintId);
		// Add a menu to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		QuantityDtoIn q = new QuantityDtoIn(1, null, allMenus.get(0).getId());
		qs.add(q);
		dto.setQuantity(qs);

		OrderEntity result = this.orderService.order(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isCreated(), "Result must have the correct status");
		Assertions.assertNotNull(result.getQuantityEntities(), "Result must have some quantity");
		Assertions.assertEquals(1, result.getQuantityEntities().size(), "Result must have one quantity");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testOrder02() throws Exception {
		List<MealEntity> allMeals = this.mealService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		UserEntity user = super.findASimpleUser();
		List<MenuEntity> allMenus = this.menuService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		// Remove constraint
		final Integer constraintId = Integer.valueOf(-1);
		OrderDtoIn dto = new OrderDtoIn();
		dto.setUserId(user.getId());
		dto.setConstraintId(constraintId);

		// Add a menu to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		QuantityDtoIn q = new QuantityDtoIn(1, null, allMenus.get(0).getId());
		qs.add(q);
		// Add some meals to the order
		List<MealEntity> someMeals = super.generateList(3, allMeals);
		for (MealEntity lMealEntity : someMeals) {
			QuantityDtoIn qp = new QuantityDtoIn(1, lMealEntity.getId(), null);
			qs.add(qp);
		}

		dto.setQuantity(qs);
		OrderEntity result = this.orderService.order(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isCreated(), "Result must have the correct status");
		Assertions.assertNotNull(result.getQuantityEntities(), "Result must have some quantity");
		Assertions.assertEquals(1 + someMeals.size(), result.getQuantityEntities().size(),
				"Result must have correct quantity");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testOrder03() throws Exception {
		List<MealEntity> allMeals = this.mealService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		UserEntity user = super.findASimpleUser();
		// Remove constraint
		final Integer constraintId = Integer.valueOf(-1);
		OrderDtoIn dto = new OrderDtoIn();
		dto.setUserId(user.getId());
		dto.setConstraintId(constraintId);
		// Add some meals to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		List<MealEntity> someMeals = super.generateList(3, allMeals);
		for (MealEntity lMealEntity : someMeals) {
			QuantityDtoIn qp = new QuantityDtoIn(1, lMealEntity.getId(), null);
			qs.add(qp);
		}
		dto.setQuantity(qs);

		OrderEntity result = this.orderService.order(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isCreated(), "Result must have the correct status");
		Assertions.assertNotNull(result.getQuantityEntities(), "Result must have some quantity");
		Assertions.assertEquals(someMeals.size(), result.getQuantityEntities().size(),
				"Result must have correct quantity");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testOrder04() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> this.orderService.order(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testOrder05() throws Exception {
		// update constraint for testing
		ConstraintEntity ce = super.constraintService.find(Integer.valueOf(1));
		Assertions.assertNotNull(ce, "Result must exist");
		ce.setOrderTimeLimit(LocalTime.now().plusMinutes(20));
		super.constraintDao.save(ce);

		List<MealEntity> allMeals = this.mealService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		UserEntity user = super.findASimpleUser();
		// Remove constraint
		OrderDtoIn dto = new OrderDtoIn();
		dto.setUserId(user.getId());
		dto.setConstraintId(null); // Set to null here <=> will be set to 1
		// Add some meals to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		List<MealEntity> someMeals = super.generateList(3, allMeals);
		for (MealEntity lMealEntity : someMeals) {
			QuantityDtoIn qp = new QuantityDtoIn(1, lMealEntity.getId(), null);
			qs.add(qp);
		}
		dto.setQuantity(qs);

		OrderEntity result = this.orderService.order(dto);
		Assertions.assertNotNull(result, "Result must exist");
		Assertions.assertNotNull(result.getId(), "Result must have an id");
		Assertions.assertTrue(result.isCreated(), "Result must have the correct status");
		Assertions.assertNotNull(result.getQuantityEntities(), "Result must have some quantity");
		Assertions.assertEquals(someMeals.size(), result.getQuantityEntities().size(),
				"Result must have correct quantity");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testOrder06() throws Exception {
		// update constraint for testing
		ConstraintEntity ce = super.constraintService.find(Integer.valueOf(1));
		Assertions.assertNotNull(ce, "Result must exist");
		ce.setOrderTimeLimit(LocalTime.now().minusMinutes(20));
		super.constraintDao.save(ce);

		List<MealEntity> allMeals = this.mealService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		UserEntity user = super.findASimpleUser();
		OrderDtoIn dto = new OrderDtoIn();
		dto.setUserId(user.getId());
		dto.setConstraintId(ce.getId());
		// Add some meals to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		List<MealEntity> someMeals = super.generateList(3, allMeals);
		for (MealEntity lMealEntity : someMeals) {
			QuantityDtoIn qp = new QuantityDtoIn(1, lMealEntity.getId(), null);
			qs.add(qp);
		}
		dto.setQuantity(qs);
		Assertions.assertThrows(TimeOutException.class, () -> this.orderService.order(dto));
	}

	private OrderEntity createAnOrder(Integer aConstraintId) throws Exception {
		UserEntity user = super.findASimpleUser();
		List<MenuEntity> allMenus = this.menuService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		OrderDtoIn dto = new OrderDtoIn();
		dto.setUserId(user.getId());
		dto.setConstraintId(aConstraintId);
		// Add a menu to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		QuantityDtoIn q = new QuantityDtoIn(1, null, allMenus.get(0).getId());
		qs.add(q);
		dto.setQuantity(qs);
		return this.orderService.order(dto);
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDeliverAndPay01() throws Exception {
		final Integer constraintId = Integer.valueOf(-1);
		// Give money to user first, in case
		OrderEntity order = this.createAnOrder(constraintId);
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		UserEntity user = order.getUser();
		user = this.userService.credit(user.getId(), BigDecimal.valueOf(500D));
		Assertions.assertTrue(user.getWallet().doubleValue() > 0, "User must have money");
		order = this.orderService.deliverAndPay(order.getId(), constraintId);
		Assertions.assertNotNull(order, "Result must exist");
		Assertions.assertTrue(order.isDelivered(), "Result must have the correct status");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDeliverAndPay02() throws Exception {
		final Integer constraintId = Integer.valueOf(-1);
		// Remove money to user first
		OrderEntity order = this.createAnOrder(constraintId);
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		UserEntity user = order.getUser();
		if (user.getWallet().doubleValue() > 0) {
			this.userService.debit(user.getId(), BigDecimal.valueOf(user.getWallet().doubleValue() - 0.01D));
		}
		Assertions.assertThrows(LackOfMoneyException.class,
				() -> this.orderService.deliverAndPay(order.getId(), constraintId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDeliverAndPay03() throws Exception {
		final Integer constraintId = Integer.valueOf(-1);
		// cancel command first
		OrderEntity order = this.createAnOrder(constraintId);
		OrderEntity canceledOrder = this.orderService.cancel(order.getId());
		Assertions.assertTrue(canceledOrder.isCanceled(), "Result must have the correct status");
		Assertions.assertThrows(OrderCanceledException.class,
				() -> this.orderService.deliverAndPay(canceledOrder.getId(), constraintId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDeliverAndPay04() throws Exception {
		final Integer constraintId = Integer.valueOf(-1);
		// Give money to user first, in case
		OrderEntity order = this.createAnOrder(constraintId);
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		UserEntity user = order.getUser();
		this.userService.credit(user.getId(), BigDecimal.valueOf(500D));
		OrderEntity deliveredOrder = this.orderService.deliverAndPay(order.getId(), constraintId);
		Assertions.assertNotNull(deliveredOrder, "Result must exist");
		Assertions.assertTrue(deliveredOrder.isDelivered(), "Result must have the correct status");
		// Try to cancel
		Assertions.assertThrows(OrderDelivredException.class, () -> this.orderService.cancel(deliveredOrder.getId()));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testDeliverAndPay05() throws Exception {
		final Integer constraintId = Integer.valueOf(-1);
		// Give money to user first, in case
		OrderEntity order = this.createAnOrder(constraintId);
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		UserEntity user = order.getUser();
		this.userService.credit(user.getId(), BigDecimal.valueOf(500D));
		OrderEntity deliveredOrder = this.orderService.deliverAndPay(order.getId(), constraintId);
		Assertions.assertNotNull(deliveredOrder, "Result must exist");
		Assertions.assertTrue(deliveredOrder.isDelivered(), "Result must have the correct status");
		// Try to deliver again
		Assertions.assertThrows(OrderDelivredException.class,
				() -> this.orderService.deliverAndPay(deliveredOrder.getId(), constraintId));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate01() throws Exception {
		List<OrderEntity> allOrders = this.orderService.findAll();
		final Integer constraintId = Integer.valueOf(-1);
		int index = 0;
		OrderEntity order = allOrders.get(index);
		while (order.getQuantityEntities() == null || order.getQuantityEntities().isEmpty()) {
			order = allOrders.get(index);
			index++;
		}
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertNotNull(order.getQuantityEntities(), "Result must be have quantities");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must be have quantities");
		final int initialSize = order.getQuantityEntities().size();
		OrderDtoIn dto = new OrderDtoIn(order, constraintId);
		List<MenuEntity> allMenus = this.menuService.findAll();
		// Add a menu to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		QuantityDtoIn q = new QuantityDtoIn(1, null, allMenus.get(0).getId());
		qs.add(q);
		// re-add all other quantities
		for (QuantityEntity qe : order.getQuantityEntities()) {
			qs.add(new QuantityDtoIn(qe));
		}
		dto.setQuantity(qs);

		order = this.orderService.update(order.getId(), dto);
		Assertions.assertNotNull(order, "Result must exist");
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertNotNull(order.getQuantityEntities(), "Result must have some quantity");
		Assertions.assertEquals(initialSize + 1, order.getQuantityEntities().size(),
				"Result must have correct quantity");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate02() throws Exception {
		List<OrderEntity> allOrders = this.orderService.findAll();
		final Integer constraintId = Integer.valueOf(-1);
		int index = 0;
		OrderEntity order = allOrders.get(index);
		while (order.getQuantityEntities().isEmpty()) {
			order = allOrders.get(index);
			index++;
		}
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must have meals");
		// Change meal - change quantity
		final BigDecimal oldPrice = this.orderService.computePrice(order, constraintId, new PriceDtoOut());
		OrderDtoIn dto = new OrderDtoIn(order, constraintId);
		List<QuantityDtoIn> qps = dto.getQuantity();
		QuantityDtoIn qpo = qps.get(0);
		qpo.setQuantity(Integer.valueOf(qpo.getQuantity().intValue() + 10));

		order = this.orderService.update(order.getId(), dto);
		Assertions.assertNotNull(order, "Result must exist");
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must have meals");
		final BigDecimal newPrice = this.orderService.computePrice(order, constraintId, new PriceDtoOut());
		Assertions.assertTrue(newPrice.doubleValue() > oldPrice.doubleValue(), "Price should be bigger");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate03() throws Exception {
		List<OrderEntity> allOrders = this.orderService.findAll();
		List<MealEntity> allMeals = this.mealService.findAll();
		final Integer constraintId = Integer.valueOf(-1);
		int index = 0;
		OrderEntity order = allOrders.get(index);
		while (order.getQuantityEntities().isEmpty()) {
			order = allOrders.get(index);
			index++;
		}
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must have meals");
		// Change meal - Add one
		final BigDecimal oldPrice = this.orderService.computePrice(order, constraintId, new PriceDtoOut());
		final int oldNbMeal = order.getQuantityEntities().size();
		OrderDtoIn dto = new OrderDtoIn(order, constraintId);
		List<QuantityDtoIn> qps = dto.getQuantity();
		qps.add(new QuantityDtoIn(1, allMeals.get(0).getId(), null));

		order = this.orderService.update(order.getId(), dto);
		Assertions.assertNotNull(order, "Result must exist");
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must have meals");
		final BigDecimal newPrice = this.orderService.computePrice(order, constraintId, new PriceDtoOut());
		Assertions.assertTrue(newPrice.doubleValue() > oldPrice.doubleValue(), "Price should be bigger");
		final int newNbMeal = order.getQuantityEntities().size();
		Assertions.assertTrue(newNbMeal > oldNbMeal, "Nb meal should be bigger");
		Assertions.assertTrue(newNbMeal == oldNbMeal + 1, () -> "Nb meal should +1 bigger");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate04() throws Exception {
		List<OrderEntity> allOrders = this.orderService.findAll();
		final Integer constraintId = Integer.valueOf(-1);
		int index = 0;
		OrderEntity order = allOrders.get(index);
		while (order.getQuantityEntities() == null || order.getQuantityEntities().size() < 2) {
			order = allOrders.get(index);
			index++;
		}
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must have meals");
		// Change meal - Remove one
		final BigDecimal oldPrice = this.orderService.computePrice(order, constraintId, new PriceDtoOut());
		final int oldNbMeal = order.getQuantityEntities().size();
		OrderDtoIn dto = new OrderDtoIn(order, constraintId);
		List<QuantityDtoIn> qps = dto.getQuantity();
		qps.remove(0);

		order = this.orderService.update(order.getId(), dto);
		Assertions.assertNotNull(order, "Result must exist");
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertFalse(order.getQuantityEntities().isEmpty(), "Result must have meals");
		final BigDecimal newPrice = this.orderService.computePrice(order, constraintId, new PriceDtoOut());
		Assertions.assertTrue(newPrice.doubleValue() < oldPrice.doubleValue(), "Price should be smaller");
		final int newNbMeal = order.getQuantityEntities().size();
		Assertions.assertTrue(newNbMeal < oldNbMeal, "Nb meal should be smaller");
		Assertions.assertTrue(newNbMeal == oldNbMeal - 1, "Nb meal should -1 smaller");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate07() throws Exception {
		List<OrderEntity> allOrders = this.orderService.findAll();
		final Integer constraintId = Integer.valueOf(-1);
		int index = 0;
		OrderEntity order = allOrders.get(index);
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		final Integer userId = order.getUser().getId();
		OrderDtoIn dto = new OrderDtoIn(order, constraintId);
		// Change user id
		dto.setUserId(super.findASimpleUser(userId).getId()); // Changing user id should have no effect (except a log)
																 // (must be a real one)
		order = this.orderService.update(order.getId(), dto);
		Assertions.assertNotNull(order, "Result must exist");
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		Assertions.assertEquals(userId, order.getUser().getId(), "Result must have the correct user id");
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void testUpdate08() throws Exception {
		ConstraintEntity ce = super.constraintService.find(Integer.valueOf(1));
		Assertions.assertNotNull(ce, "Result must exist");

		List<OrderEntity> allOrders = this.orderService.findAll();
		final Integer constraintId = ce.getId();
		int index = 0;
		OrderEntity order = allOrders.get(index);
		Assertions.assertTrue(order.isCreated(), "Result must have the correct status");
		// update constraint for testing
		ce.setOrderTimeLimit(order.getCreationTime().minusMinutes(20));
		super.constraintDao.save(ce);

		OrderDtoIn dto = new OrderDtoIn(order, constraintId);
		final Integer orderId = order.getId();
		List<MenuEntity> allMenus = this.menuService.findAllAvailableForWeek(OrderServiceTest.THIS_WEEK);
		// Add a menu to the order
		List<QuantityDtoIn> qs = new ArrayList<>();
		QuantityDtoIn q = new QuantityDtoIn(1, null, allMenus.get(0).getId());
		qs.add(q);
		dto.setQuantity(qs);

		Assertions.assertThrows(TimeOutException.class, () -> this.orderService.update(orderId, dto));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllByUserId01() throws Exception {
		Integer userId = super.findASimpleUser().getId();
		List<OrderEntity> orders = super.orderService.findAllByUserId(userId);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertNotNull(lOrderEntity.getUser(), "Orders must have a user");
			Assertions.assertEquals(userId, lOrderEntity.getUser().getId(), "Orders must have the correct user id");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllByUserId02() throws Exception {
		Assertions.assertThrows(ParameterException.class, () -> super.orderService.findAllByUserId(null));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateInStatus01() throws Exception {
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = beginDate.minusDays(1);
		final OrderStatus status = OrderStatus.CREATED;
		Assertions.assertThrows(ParameterException.class,
				() -> super.orderService.findAllBetweenDateInStatus(beginDate, endDate, status));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateInStatus02() throws Exception {
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = LocalDate.now().plusYears(1);
		final OrderStatus status = OrderStatus.CREATED;
		List<OrderEntity> orders = super.orderService.findAllBetweenDateInStatus(beginDate, endDate, status);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertEquals(status, lOrderEntity.getStatus(), "Orders must have the correct status");
			Assertions.assertTrue(beginDate.isBefore(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
			Assertions.assertTrue(endDate.isAfter(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateInStatus03() throws Exception {
		final LocalDate endDate = LocalDate.now().plusYears(1);
		final OrderStatus status = OrderStatus.CREATED;
		List<OrderEntity> orders = super.orderService.findAllBetweenDateInStatus(null, endDate, status);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertEquals(status, lOrderEntity.getStatus(), "Orders must have the correct status");
			Assertions.assertTrue(endDate.isAfter(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateInStatus04() throws Exception {
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final OrderStatus status = OrderStatus.CREATED;
		List<OrderEntity> orders = super.orderService.findAllBetweenDateInStatus(beginDate, null, status);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertEquals(status, lOrderEntity.getStatus(), "Orders must have the correct status");
			Assertions.assertTrue(beginDate.isBefore(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateInStatus05() throws Exception {
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = LocalDate.now().plusYears(1);
		List<OrderEntity> orders = super.orderService.findAllBetweenDateInStatus(beginDate, endDate, null);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertEquals(OrderStatus.CREATED, lOrderEntity.getStatus(),
					"Orders must have the correct status");
			Assertions.assertTrue(beginDate.isBefore(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
			Assertions.assertTrue(endDate.isAfter(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateForUser01() throws Exception {
		final Integer userId = super.findASimpleUser().getId();
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = LocalDate.now().plusYears(1);
		List<OrderEntity> orders = super.orderService.findAllBetweenDateForUser(userId, beginDate, endDate);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertEquals(userId, lOrderEntity.getUser().getId(), "Orders must have the correct status");
			Assertions.assertTrue(beginDate.isBefore(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
			Assertions.assertTrue(endDate.isAfter(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateForUser02() throws Exception {
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = LocalDate.now().plusYears(1);
		Assertions.assertThrows(ParameterException.class,
				() -> super.orderService.findAllBetweenDateForUser(null, beginDate, endDate));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateForUser03() throws Exception {
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = beginDate.minusDays(1);
		Assertions.assertThrows(ParameterException.class,
				() -> super.orderService.findAllBetweenDateForUser(null, beginDate, endDate));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateForUserInStatus01() throws Exception {
		final Integer userId = super.findASimpleUser().getId();
		final OrderStatus status = OrderStatus.CREATED;
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = LocalDate.now().plusYears(1);
		List<OrderEntity> orders = super.orderService.findAllBetweenDateForUserInStatus(userId, beginDate, endDate,
				status);
		Assertions.assertNotNull(orders, "Orders must exists");
		for (OrderEntity lOrderEntity : orders) {
			Assertions.assertEquals(status, lOrderEntity.getStatus(), "Orders must have the correct status");
			Assertions.assertEquals(userId, lOrderEntity.getUser().getId(), "Orders must have the correct status");
			Assertions.assertTrue(beginDate.isBefore(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
			Assertions.assertTrue(endDate.isAfter(lOrderEntity.getCreationDate()),
					"Orders must have the correct creation date");
		}
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateForUserInStatus02() throws Exception {
		final OrderStatus status = OrderStatus.CREATED;
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = LocalDate.now().plusYears(1);
		Assertions.assertThrows(ParameterException.class,
				() -> super.orderService.findAllBetweenDateForUserInStatus(null, beginDate, endDate, status));
	}

	/**
	 * Test
	 *
	 * @throws Exception if an error occurred
	 */
	@Test
	public void findAllBetweenDateForUserInStatus03() throws Exception {
		final Integer userId = super.findASimpleUser().getId();
		final OrderStatus status = OrderStatus.CREATED;
		final LocalDate beginDate = LocalDate.now().minusYears(20);
		final LocalDate endDate = beginDate.minusDays(1);
		Assertions.assertThrows(ParameterException.class,
				() -> super.orderService.findAllBetweenDateForUserInStatus(userId, beginDate, endDate, status));
	}
}
