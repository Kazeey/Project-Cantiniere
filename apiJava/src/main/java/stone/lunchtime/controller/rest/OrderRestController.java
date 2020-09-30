// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import stone.lunchtime.controller.AbstractController;
import stone.lunchtime.dto.in.OrderDtoIn;
import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.dto.out.OrderDtoOut;
import stone.lunchtime.dto.out.PriceDtoOut;
import stone.lunchtime.entity.OrderEntity;
import stone.lunchtime.entity.OrderStatus;
import stone.lunchtime.service.OrderService;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.LackOfMoneyException;
import stone.lunchtime.service.exception.NotAvailableForThisWeekException;
import stone.lunchtime.service.exception.OrderCanceledException;
import stone.lunchtime.service.exception.OrderDelivredException;
import stone.lunchtime.service.exception.TimeOutException;

/**
 * Order controller.
 */
@RestController
@RequestMapping("/order")
@Tag(name = "Order management API", description = "Order management API")
public class OrderRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private OrderService service;

	/**
	 * Passes an order. <br>
	 *
	 * You need to be connected.
	 *
	 * @param pOrder  the order to be added
	 * @param request the HttpServletRequest
	 * @return the order added
	 * @throws EntityNotFoundException             if an error occurred
	 * @throws NotAvailableForThisWeekException    if an error occurred
	 * @throws TimeOutException                    if an error occurred
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/add")
	@Operation(tags = {
			"Order management API" }, summary = "Adds an order.", description = "Will add an order into the data base. Will return it with its id when done. You must be connected in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your order was added and returned in the response body.", content = @Content(schema = @Schema(implementation = OrderDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your order is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The meal or menu in this order is not available for this week or it is too late regarding the constraint's maximum time or this order is referencing invalid elements.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<OrderDtoOut> add(
			@Parameter(description = "Order object that will be stored in database. Linked to a constraint's id that will be used for timeout here.", required = true) @RequestBody OrderDtoIn pOrder,
			HttpServletRequest request)
			throws TimeOutException, NotAvailableForThisWeekException, EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> add - {}", remoteIP, pOrder);
		if (pOrder.getUserId() == null) {
			OrderRestController.LOG.warn(
					"[{}] --- add - New order had no user id so will use the one who is connected {}", remoteIP,
					super.getConnectedUserId());
			pOrder.setUserId(super.getConnectedUserId());
		}
		OrderEntity result = this.service.order(pOrder);
		OrderDtoOut dtoOut = new OrderDtoOut(result);
		OrderRestController.LOG.info("[{}] <-- add - New order made by {} has id {}", remoteIP,
				super.getConnectedUserId(), dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deliver and pay the order. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * Order status will change and money will be removed from the user's wallet.
	 *
	 * @param pOrderId      the order id to be delivered and payed
	 * @param pConstraintId a constraint id. If null will use first one in data
	 *                      base, if -1 will not use any constraint
	 * @param request       the HttpServletRequest
	 * @return the order updated
	 * @throws OrderDelivredException              if an error occurred
	 * @throws OrderCanceledException              if an error occurred
	 * @throws LackOfMoneyException                if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/deliverandpay/{orderId}/{constraintId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Order management API" }, summary = "Pays an order.", description = "Will pays an order into the data base. Will take money from user who ordered it and change the order status to DELIVRED(1). Will return it with its id when done. You must be connected and have the Lunch Lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your order was added and returned in the response body.", content = @Content(schema = @Schema(implementation = OrderDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your orderId or constraintId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "Your order is not in CREATED(0) state or the user has not enought money in its wallet or this order is referencing invalid elements.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<OrderDtoOut> pay(
			@Parameter(description = "Order's id that need to be delivred.", required = true) @PathVariable("orderId") Integer pOrderId,
			@Parameter(description = "Constraint's id that will be used for computing prices. May be -1 for no constraint, means DF only.", required = true) @PathVariable(required = false, name = "constraintId") Integer pConstraintId,
			HttpServletRequest request)
			throws EntityNotFoundException, LackOfMoneyException, OrderCanceledException, OrderDelivredException {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> pay - {} with constraint {}", remoteIP, pOrderId, pConstraintId);
		OrderEntity result = this.service.deliverAndPay(pOrderId, pConstraintId);
		OrderDtoOut dtoOut = new OrderDtoOut(result);
		OrderRestController.LOG.info("[{}] <-- pay - order {} is payed by lunch lady {}", remoteIP, dtoOut.getId(),
				super.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Computes order price. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to compute price of
	 * order that you have made.
	 *
	 * @param pOrderId      the order targeted
	 * @param pConstraintId a constraint id. If null will use first one in data
	 *                      base, if -1 will not use any constraint
	 * @param request       the HttpServletRequest
	 * @return the order price
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/computeprice/{orderId}/{constraintId}")
	@Operation(tags = {
			"Order management API" }, summary = "Compute the prices (DF and VAT) of the order.", description = "Will compute the prices of an order (passed by you). Will not take money from user who ordered it nor change the order status. Will return it with its id when done. You must be connected, only Lunch Lady role can compute all orders, other wise you'll be able to compute your orders only.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Prices of the order was computed and are in the response body.", content = @Content(schema = @Schema(implementation = PriceDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your orderId or constraintId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot compute the specified order (because you are not a lunch lady or it is not your order).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "Your order was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<PriceDtoOut> computePrice(
			@Parameter(description = "Order's id.", required = true) @PathVariable("orderId") Integer pOrderId,
			@Parameter(description = "Constraint's id that will be used for computing prices. May be -1 for no constraint, means DF only.", required = true) @PathVariable(required = false, name = "constraintId") Integer pConstraintId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> computePrice - {} with constraint {}", remoteIP, pOrderId,
				pConstraintId);
		OrderEntity order = this.service.find(pOrderId);
		if (super.hasLunchLadyRole() || super.getConnectedUserId().equals(order.getUser().getId())) {
			PriceDtoOut dtoOut = new PriceDtoOut();
			this.service.computePrice(order, pConstraintId, dtoOut);
			OrderRestController.LOG.info("[{}] <-- computePrice - order {} has a price of {}", remoteIP, pOrderId,
					dtoOut);
			return new ResponseEntity<>(dtoOut, HttpStatus.OK);
		}
		OrderRestController.LOG.error("[{}] <-- computePrice - User {} not allowed to compute Price for order {}",
				remoteIP, super.getConnectedUserId(), pOrderId);
		throw new InsufficientAuthenticationException("Vous n'avez pas le droit de voir cette commande!");
	}

	/**
	 * Cancels an order. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to cancel order that
	 * you have made. <br>
	 * A canceled order cannot be change later.
	 *
	 * @param pOrderId id of the order to be deleted
	 * @param request  the HttpServletRequest
	 * @return the order canceled
	 * @throws OrderDelivredException              if an error occurred
	 * @throws OrderCanceledException              if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/cancel/{orderId}")
	@Operation(tags = {
			"Order management API" }, summary = "Cancels an order.", description = "Will cancel an order (passed by you). Will change the order status, it will pass into CANCELED(2). Will return it when done. You must be connected, only Lunch Lady role can cancel any orders, other wise you'll be able to cancel your orders only.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order was canceled and is in the response body.", content = @Content(schema = @Schema(implementation = OrderDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your orderId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot cancel the specified order (because you are not a lunch lady or it is not your order).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "Your order was not found or is not in the correct state.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<OrderDtoOut> cancel(
			@Parameter(description = "Order's id.", required = true) @PathVariable("orderId") Integer pOrderId,
			HttpServletRequest request) throws EntityNotFoundException, OrderCanceledException, OrderDelivredException {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> cancel - {}", remoteIP, pOrderId);
		OrderEntity orderToCancel = this.service.find(pOrderId);
		if (super.hasLunchLadyRole() || super.getConnectedUserId().equals(orderToCancel.getUser().getId())) {
			OrderEntity result = this.service.cancel(pOrderId);
			OrderDtoOut dtoOut = new OrderDtoOut(result);
			OrderRestController.LOG.info("[{}] <-- cancel - order {} is cancel by user {}", remoteIP, pOrderId,
					super.getConnectedUserId());
			return new ResponseEntity<>(dtoOut, HttpStatus.OK);
		}
		OrderRestController.LOG.error("[{}] <-- cancel - User {} not allowed to cancel command {}", remoteIP,
				super.getConnectedUserId(), pOrderId);
		throw new InsufficientAuthenticationException("Vous n'avez pas le droit d'annuler cette commande!");
	}

	/**
	 * Updates an order. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to update order that
	 * you have made. <br>
	 * You cannot change status or user with this method
	 *
	 * @param pOrderId id of the order to be updated
	 * @param pOrder   where to find the new order information
	 * @param request  the HttpServletRequest
	 * @return the order updated
	 * @throws NotAvailableForThisWeekException    if an error occurred
	 * @throws TimeOutException                    if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/update/{orderId}")
	@Operation(tags = {
			"Order management API" }, summary = "Updates an order.", description = "Will update an order (passed by you). Will change all available values except status and user responsible of the order. Will return it when done. You must be connected, only Lunch Lady role can update any orders, other wise you'll be able to update your orders only.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order was updated and is in the response body.", content = @Content(schema = @Schema(implementation = OrderDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your orderId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot cancel the specified order (because you are not a lunch lady or it is not your order).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The meal or menu in this order is not available for this week or it is too late regarding the constraint's maximum time or this order is referencing invalid elements.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<OrderDtoOut> update(
			@Parameter(description = "Order's id.", required = true) @PathVariable("orderId") Integer pOrderId,
			@Parameter(description = "Order object that will be updated in database. All present values will be updated. You cannot change status nor user responsible of the order.", required = true) @RequestBody OrderDtoIn pOrder,
			HttpServletRequest request)
			throws EntityNotFoundException, TimeOutException, NotAvailableForThisWeekException {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> update - {}", remoteIP, pOrder);
		if (pOrder.getUserId() == null) {
			OrderRestController.LOG.warn(
					"[{}] --- order - Order to update had no user id so will use the one who is connected {}", remoteIP,
					super.getConnectedUserId());
			pOrder.setUserId(super.getConnectedUserId());
		}
		OrderEntity orderToUpdate = this.service.find(pOrderId);
		if (super.hasLunchLadyRole() || super.getConnectedUserId().equals(orderToUpdate.getUser().getId())) {
			OrderEntity result = this.service.update(pOrderId, pOrder);
			OrderDtoOut dtoOut = new OrderDtoOut(result);
			OrderRestController.LOG.info("[{}] <-- update - order {} is updated by user {}", remoteIP, dtoOut.getId(),
					super.getConnectedUserId());
			return new ResponseEntity<>(dtoOut, HttpStatus.OK);
		}
		OrderRestController.LOG.error("[{}] <-- update - User {} not allowed to update order {}", remoteIP,
				super.getConnectedUserId(), pOrderId);
		throw new InsufficientAuthenticationException("Vous n'avez pas le droit de mettre à jour cette commande!");
	}

	/**
	 * Finds an order. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to find order that you
	 * have made. <br>
	 *
	 * @param pOrderId id of the order to be found
	 * @param request  the HttpServletRequest
	 * @return the order found
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find/{orderId}")
	@Operation(tags = {
			"Order management API" }, summary = "Finds one order.", description = "Will find an order already present in the data base (passed by you). Will return it when done. You must be connected, only Lunch Lady role can find any orders, other wise you'll be able to find your orders only.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your order was found and returned in the response body.", content = @Content(schema = @Schema(implementation = OrderDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your orderId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "Your are not connected or not allowed to see this order (because you are not a lunch lady or it is not your order).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<OrderDtoOut> find(
			@Parameter(description = "Order's id.", required = true) @PathVariable("orderId") Integer pOrderId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> find - {}", remoteIP, pOrderId);
		OrderEntity result = this.service.find(pOrderId);
		if (super.hasLunchLadyRole() || super.getConnectedUserId().equals(result.getUser().getId())) {
			OrderDtoOut dtoOut = new OrderDtoOut(result);
			OrderRestController.LOG.info("[{}] <-- find - Has found order {}", remoteIP, pOrderId);
			return new ResponseEntity<>(dtoOut, HttpStatus.OK);
		}
		OrderRestController.LOG.error("[{}] <-- find - User {} not allowed to see order {}", remoteIP,
				super.getConnectedUserId(), pOrderId);
		throw new InsufficientAuthenticationException("Vous n'avez pas le droit de voir cette commande!");
	}

	/**
	 * Gets all orders. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the orders found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findall")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Order management API" }, summary = "Finds all orders.", description = "Will find all orders already present in the data base. Will return them when done. You must be connected and have the Lunch Lady role.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All orders was found and returned in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<OrderDtoOut>> findAll(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> findAll", remoteIP);
		List<OrderEntity> result = this.service.findAll();
		List<OrderDtoOut> dtos = new ArrayList<>();
		for (OrderEntity elm : result) {
			dtos.add(new OrderDtoOut(elm));
		}
		OrderRestController.LOG.info("[{}] <-- findAll - Lunch Lady {} has found {} orders", remoteIP,
				super.getConnectedUserId(), dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all orders for a specific user and the given parameters. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to find order that you
	 * have made. <br>
	 *
	 * @param request    the HttpServletRequest
	 * @param pUserId    a user id. Cannot be null.
	 * @param pBeginDate a begin date. If null will use 1970
	 * @param pEndDate   a end date. If null will use now
	 * @param pStatus    an order status. If null will use CREATED.
	 * @return all the orders found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallforuser/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Order management API" }, summary = "Finds all orders made by a user and matching the criteria.", description = "Will find all orders already present in the data base made by a specific user and matching criteria. Will return them when done. You must be connected, you can retreive all your orders or if you have the Lunch Lady role any orders made by any users.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All orders was found and returned in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "Your are not connected or not allowed to see this order (because you are not a lunch lady or it is not your order).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<OrderDtoOut>> findAllForUser(
			@Parameter(description = "User's id.", required = true) @PathVariable("userId") Integer pUserId,
			@Parameter(description = "An order status. CREATED(0), DELIVERED(1), CANCELED(2)", required = false, allowEmptyValue = true) @RequestParam(required = false, name = "status") Byte pStatus,
			@Parameter(description = "A start date. Format is linked with option configuration.date.pattern in application.properties file.", required = false, allowEmptyValue = true) @RequestParam(required = false, name = "beginDate") String pBeginDate,
			@Parameter(description = "An end date. Format is linked with option configuration.date.pattern in application.properties file.", required = false, allowEmptyValue = true) @RequestParam(required = false, name = "endDate") String pEndDate,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> findAllForUser - {} {} {} {}", remoteIP, pUserId, pStatus, pBeginDate,
				pEndDate);
		List<OrderEntity> result = null;
		LocalDate beginDate = super.getDate(pBeginDate);
		LocalDate endDate = super.getDate(pEndDate);
		if (pStatus == null || !OrderStatus.inRange(pStatus)) {
			if (pBeginDate == null && pEndDate == null) {
				result = this.service.findAllByUserId(pUserId);
			} else {
				result = this.service.findAllBetweenDateForUser(pUserId, beginDate, endDate);
			}
		} else {
			if (pBeginDate == null && pEndDate == null) {
				result = this.service.findAllForUserInStatus(pUserId, OrderStatus.fromValue(pStatus));
			} else {
				result = this.service.findAllBetweenDateForUserInStatus(pUserId, beginDate, endDate,
						OrderStatus.fromValue(pStatus));
			}
		}

		List<OrderDtoOut> dtos = new ArrayList<>();
		for (OrderEntity elm : result) {
			dtos.add(new OrderDtoOut(elm));
		}
		OrderRestController.LOG.info("[{}] <-- findAllForUser - Has found {} orders", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all orders (not delivered nor canceled) for a specific user and today.
	 * <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to find order that you
	 * have made. <br>
	 *
	 * @param request the HttpServletRequest
	 * @param pUserId a user id. Cannot be null.
	 * @return all the orders found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallforusertoday/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Order management API" }, summary = "Finds all orders made by a user.", description = "Will find all orders already present in the data base made by a specific user. Will return them when done. You must be connected, you can retreive all your orders or if you have the Lunch Lady role any orders made by any users.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All orders was found and returned in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "Your are not connected or not allowed to see this order (because you are not a lunch lady or it is not your order).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<OrderDtoOut>> findAllForUserToday(
			@Parameter(description = "User's id.", required = true) @PathVariable("userId") Integer pUserId,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> findAllForUserToday - {}", remoteIP, pUserId);

		LocalDate todayBegin = LocalDate.now();

		List<OrderEntity> result = this.service.findAllBetweenDateForUserInStatus(pUserId, todayBegin, todayBegin,
				OrderStatus.CREATED);
		List<OrderDtoOut> dtos = new ArrayList<>();
		for (OrderEntity elm : result) {
			dtos.add(new OrderDtoOut(elm));
		}
		OrderRestController.LOG.info("[{}] <-- findAllForUserToday - Has found {} orders", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all orders for all users with the given parameters. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param request    the HttpServletRequest
	 * @param pBeginDate a begin date. If null will use 1970
	 * @param pEndDate   a end date. If null will use now
	 * @param pStatus    an order status. If null will use CREATED.
	 * @return all the orders found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallbetweendateinstatus")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Order management API" }, summary = "Finds all orders matching criteria.", description = "Will find all orders already present in the data base and matching criteria. Will return them when done. You must be connected and have the Lunch Lady role.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All orders was found and returned in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<OrderDtoOut>> findAllBetweenDateInStatus(
			@Parameter(description = "An order status. CREATED(0), DELIVERED(1), CANCELED(2)", required = false, allowEmptyValue = true) @RequestParam(required = false, name = "status") Byte pStatus,
			@Parameter(description = "A start date. Format is linked with option configuration.date.pattern in application.properties file.", required = false, allowEmptyValue = true) @RequestParam(required = false, name = "beginDate") String pBeginDate,
			@Parameter(description = "An end date. Format is linked with option configuration.date.pattern in application.properties file.", required = false, allowEmptyValue = true) @RequestParam(required = false, name = "endDate") String pEndDate,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		OrderRestController.LOG.info("[{}] --> findAllBetweenDateInStatus - {} {} {}", remoteIP, pStatus, pBeginDate,
				pEndDate);
		LocalDate beginDate = super.getDate(pBeginDate);
		LocalDate endDate = super.getDate(pEndDate);
		List<OrderEntity> result = this.service.findAllBetweenDateInStatus(beginDate, endDate,
				OrderStatus.fromValue(pStatus));
		List<OrderDtoOut> dtos = new ArrayList<>();
		for (OrderEntity elm : result) {
			dtos.add(new OrderDtoOut(elm));
		}
		OrderRestController.LOG.info("[{}] <-- findAllBetweenDateInStatus - Lunch lady {} has found {} orders",
				remoteIP, super.getConnectedUserId(), dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
}
