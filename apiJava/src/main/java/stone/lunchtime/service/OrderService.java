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
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IConstraintDao;
import stone.lunchtime.dao.IMealDao;
import stone.lunchtime.dao.IMenuDao;
import stone.lunchtime.dao.IOrderDao;
import stone.lunchtime.dto.in.OrderDtoIn;
import stone.lunchtime.dto.in.QuantityDtoIn;
import stone.lunchtime.dto.out.PriceDtoOut;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.entity.OrderEntity;
import stone.lunchtime.entity.OrderStatus;
import stone.lunchtime.entity.QuantityEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.LackOfMoneyException;
import stone.lunchtime.service.exception.NotAvailableForThisWeekException;
import stone.lunchtime.service.exception.OrderCanceledException;
import stone.lunchtime.service.exception.OrderDelivredException;
import stone.lunchtime.service.exception.ParameterException;
import stone.lunchtime.service.exception.TimeOutException;

/**
 * Handle orders.
 */
@Service
public class OrderService extends AbstractServiceForEntity<OrderEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IOrderDao orderDao;
	@Autowired
	private IMealDao mealDao;
	@Autowired
	private IMenuDao menuDao;
	@Autowired
	private IConstraintDao constraintDao;

	@Autowired
	private UserService userSevice;

	/**
	 * Passes an order.
	 *
	 * @param pDtoIn Information that will be used for the order
	 * @return the order
	 * @throws TimeOutException                 if time is too late for passing the
	 *                                          order. Will depends on constraint
	 *                                          given.
	 * @throws NotAvailableForThisWeekException if meal or menu is not available for
	 *                                          this week. Will depends on
	 *                                          constraint given.
	 * @throws EntityNotFoundException          if entity was not found
	 * @throws ParameterException               if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public OrderEntity order(OrderDtoIn pDtoIn)
			throws TimeOutException, NotAvailableForThisWeekException, EntityNotFoundException {
		OrderService.LOG.debug("order - {}", pDtoIn);
		if (pDtoIn == null) {
			OrderService.LOG.error("order - Dto is null");
			throw new ParameterException("DTO null !", "pDtoIn");
		}
		pDtoIn.validate();

		OrderEntity insertOrder = pDtoIn.toEntity();

		// Handle join for transaction reason
		Integer constraintId = pDtoIn.getConstraintId();
		if (constraintId == null) {
			OrderService.LOG.warn("order - id constraint is null, will use first one");
			constraintId = Integer.valueOf(1);
		}

		insertOrder.setUser(this.userSevice.find(pDtoIn.getUserId()));

		if (pDtoIn.hasQuantity()) {
			this.handleOrderQuantity(insertOrder, pDtoIn.getQuantity(), constraintId);
		}

		boolean doInsert = this.handleTime(insertOrder, constraintId);
		if (doInsert) {
			OrderEntity resultSave = this.orderDao.save(insertOrder);
			OrderService.LOG.info("order - OK with new id={}", resultSave.getId());
			return resultSave;
		}
		OrderService.LOG.error("order - KO It is too late for ordering");
		throw new TimeOutException("L'heure authorisée pour passer une commande est dépassée");
	}

	/**
	 * Updates entity. <br>
	 *
	 * This method does not change status nor user that made the order.
	 *
	 * @param pIdToUpdate an entity id. The one that needs update.
	 * @param pNewDto     the new values for this entity
	 * @return the updated entity
	 * @throws EntityNotFoundException          if entity not found
	 * @throws TimeOutException                 If time is not right
	 * @throws NotAvailableForThisWeekException if some meals or menu are not
	 *                                          available for this week
	 * @throws ParameterException               if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public OrderEntity update(Integer pIdToUpdate, OrderDtoIn pNewDto)
			throws EntityNotFoundException, TimeOutException, NotAvailableForThisWeekException {
		OrderService.LOG.debug("update - {} with {}", pIdToUpdate, pNewDto);

		if (pIdToUpdate == null) {
			OrderService.LOG.error("update - id is null");
			throw new ParameterException("Commande n'a pas d'id !", "pIdToUpdate");
		}

		pNewDto.validate();

		OrderEntity entityInDataBase = this.getEntityFrom(pIdToUpdate);
		// Handle join for transaction reason
		Integer constraintId = pNewDto.getConstraintId();
		if (constraintId == null) {
			OrderService.LOG.warn("update - id constraint is null, will use first one");
			constraintId = Integer.valueOf(1);
		}

		if (pNewDto.getUserId() != null && !pNewDto.getUserId().equals(entityInDataBase.getUser().getId())) {
			OrderService.LOG.error("update - Cannont change user");
		}

		if (pNewDto.hasQuantity()) {
			this.handleOrderQuantity(entityInDataBase, pNewDto.getQuantity(), constraintId);
		} else {
			List<QuantityEntity> values = entityInDataBase.getQuantityEntities();
			if (values != null && !values.isEmpty()) {
				OrderService.LOG.debug("update - clear quantities");
				values.clear();
			}
		}

		boolean doInsert = this.handleTime(entityInDataBase, constraintId);
		if (doInsert) {
			OrderEntity resultUpdate = this.orderDao.save(entityInDataBase);
			OrderService.LOG.info("update - OK");
			return resultUpdate;
		}
		OrderService.LOG.error("update - KO It is too late for ordering or updating an order");
		throw new TimeOutException("L'heure authorisée pour passer une commande est dépassée");
	}

	/**
	 * Cancel an order. <br>
	 *
	 * A canceled order cannot be un-canceled.
	 *
	 * @param pOrderId an order id
	 * @return the order canceled
	 * @throws EntityNotFoundException if entity not found
	 * @throws OrderCanceledException  if entity state is not valid
	 * @throws OrderDelivredException  if entity state is not valid
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public OrderEntity cancel(Integer pOrderId)
			throws EntityNotFoundException, OrderCanceledException, OrderDelivredException {
		OrderService.LOG.debug("cancel - {}", pOrderId);
		return this.updateStatus(pOrderId, OrderStatus.CANCELED);
	}

	/**
	 * Will deliver order. <br>
	 *
	 * This will remove money from user and change the order status.
	 *
	 * @param pOrderId      an order id
	 * @param pConstraintId the constraint id. Can be null or -1.
	 * @return the order delivered
	 * @throws LackOfMoneyException    if user has not enough money
	 * @throws EntityNotFoundException if entity not found
	 * @throws OrderCanceledException  if entity state is not valid
	 * @throws OrderDelivredException  if entity state is not valid
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public OrderEntity deliverAndPay(Integer pOrderId, Integer pConstraintId)
			throws EntityNotFoundException, LackOfMoneyException, OrderCanceledException, OrderDelivredException {
		OrderService.LOG.debug("deliverAndPay - {}", pOrderId);
		OrderEntity result = this.updateStatus(pOrderId, OrderStatus.DELIVERED);
		result.setUser(this.userSevice.debit(result.getUser().getId(),
				this.computePrice(pOrderId, pConstraintId, new PriceDtoOut())));
		return result;
	}

	/**
	 * Will compute the order price. <br>
	 *
	 * This will NOT remove money from user NOR change the order status.
	 *
	 * @param pOrderId      the order id
	 * @param pConstraintId the constraint to use, can be
	 *                      <ul>
	 *                      <li>null: will use constraint with id 1 in data
	 *                      base</li>
	 *                      <li>-1: will not use constraint at all</li>
	 *                      </ul>
	 * @param pOut          the result of the computation
	 * @return the VAT price
	 * @throws EntityNotFoundException if entity was not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public BigDecimal computePrice(Integer pOrderId, Integer pConstraintId, PriceDtoOut pOut)
			throws EntityNotFoundException {
		OrderService.LOG.debug("computePrice - {}, {}", pOrderId, pConstraintId);
		if (pOrderId == null) {
			OrderService.LOG.error("computePrice - id is null");
			throw new ParameterException("La commande a un id null !");
		}

		OrderEntity order = this.getEntityFrom(pOrderId);

		return this.computePrice(order, pConstraintId, pOut);
	}

	/**
	 * Will compute the order price. <br>
	 *
	 * This will NOT remove money from user NOR change the order status.
	 *
	 * @param pOrder        the order
	 * @param pConstraintId the constraint to use, can be
	 *                      <ul>
	 *                      <li>null: will use constraint with id 1 in data
	 *                      base</li>
	 *                      <li>-1: will not use constraint at all</li>
	 *                      </ul>
	 * @param pOut          the result of the computation
	 * @return the VAT price
	 * @throws EntityNotFoundException if entity was not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public BigDecimal computePrice(OrderEntity pOrder, Integer pConstraintId, PriceDtoOut pOut)
			throws EntityNotFoundException {
		OrderService.LOG.debug("computePrice - {}, {}", pOrder, pConstraintId);
		if (pOrder == null) {
			OrderService.LOG.error("computePrice - order is null");
			throw new ParameterException("La commande est null !", "pOrder");
		}

		if (pConstraintId == null) {
			OrderService.LOG.warn("computePrice - id constraint is null, will use 1");
			pConstraintId = Integer.valueOf(1);
		}
		double tva = 0D;
		if (pConstraintId.intValue() == -1) {
			OrderService.LOG.warn("computePrice - id constraint is -1, will not use constraint");
		} else {
			Optional<ConstraintEntity> opResultConstraint = this.constraintDao.findById(pConstraintId);
			if (opResultConstraint.isPresent()) {
				ConstraintEntity result = opResultConstraint.get();
				OrderService.LOG.debug("computePrice - OK found for id={}", pConstraintId);
				tva = result.getRateVAT().doubleValue();
			} else {
				OrderService.LOG.error("computePrice - KO constraint not found for id={}", pConstraintId);
				throw new EntityNotFoundException("Contrainte introuvable", pConstraintId);
			}
		}
		pOut.setRateVAT(BigDecimal.valueOf(tva));
		double total = 0;
		List<QuantityEntity> quantities = pOrder.getQuantityEntities();
		if (quantities != null && !quantities.isEmpty()) {
			for (QuantityEntity qme : quantities) {
				if (qme.getMeal() != null) {
					total += qme.getMeal().getPriceDF().doubleValue() * qme.getQuantity();
				}
				if (qme.getMenu() != null) {
					total += qme.getMenu().getPriceDF().doubleValue() * qme.getQuantity();
				}
			}
		}
		pOut.setPriceDF(BigDecimal.valueOf(total));
		total += total * (tva / 100D);
		pOut.setPriceVAT(BigDecimal.valueOf(total));
		return BigDecimal.valueOf(total);
	}

	/**
	 * Changes the order status. <br>
	 *
	 * @param pOrderId   a user id
	 * @param pNewStatus the new status
	 * @return the order updated
	 * @throws EntityNotFoundException if entity not found
	 * @throws OrderCanceledException  if entity state is not valid
	 * @throws OrderDelivredException  if entity state is not valid
	 * @throws ParameterException      if parameter is invalid
	 */
	private OrderEntity updateStatus(Integer pOrderId, OrderStatus pNewStatus)
			throws EntityNotFoundException, OrderCanceledException, OrderDelivredException {
		if (pOrderId == null) {
			OrderService.LOG.error("changeStatus - id is null");
			throw new ParameterException("Commande n'a pas d'id !", "pOrderId");
		}

		OrderEntity result = this.getEntityFrom(pOrderId);
		boolean doUpdate = false;
		if (pNewStatus.equals(OrderStatus.CANCELED)) {
			if (result.isCreated()) {
				result.setStatus(OrderStatus.CANCELED);
				doUpdate = true;
			} else if (result.isCanceled()) {
				OrderService.LOG.warn("changeStatus - KO order already canceled for id={}", pOrderId);
				throw new OrderCanceledException("La commande est déjà annulée, elle ne peut pas être re-annulée!");
			} else if (result.isDelivered()) {
				OrderService.LOG.warn("changeStatus - KO order already delivred for id={}", pOrderId);
				throw new OrderDelivredException("La commande est déjà délivrée, elle ne peut pas être annulée!");
			}
		}
		if (pNewStatus.equals(OrderStatus.DELIVERED)) {
			if (result.isCreated()) {
				result.setStatus(OrderStatus.DELIVERED);
				doUpdate = true;
			} else if (result.isCanceled()) {
				OrderService.LOG.warn("changeStatus - KO order already canceled for id={}", pOrderId);
				throw new OrderCanceledException("La commande est déjà annulée, elle ne peut pas être livrée!");
			} else if (result.isDelivered()) {
				OrderService.LOG.warn("changeStatus - KO order already delivred for id={}", pOrderId);
				throw new OrderDelivredException("La commande est déjà délivrée, elle ne peut pas être re-délivrée!");
			}
		}
		OrderEntity resultUpdate = null;
		if (doUpdate) {
			resultUpdate = this.orderDao.save(result);
			OrderService.LOG.info("changeStatus - OK");
		} else {
			OrderService.LOG.error("changeStatus - KO order is in strange state id={}", pOrderId);
			resultUpdate = this.getEntityFrom(pOrderId);
		}
		return resultUpdate;
	}

	/**
	 * Handles the join between order and quantity.
	 *
	 * @param pOrder         an order. That will be changed during this method.
	 * @param pQuantity a list of QuantityDtoIn
	 * @param pConstraintId  a constraint id. Can be null or -1 for no constraint
	 * @throws EntityNotFoundException          if entity was not found
	 * @throws NotAvailableForThisWeekException if this menu is not available for
	 *                                          this week. Depending on the
	 *                                          constraint.
	 */
	private void handleOrderQuantity(OrderEntity pOrder, List<QuantityDtoIn> pQuantity, Integer pConstraintId)
			throws EntityNotFoundException, NotAvailableForThisWeekException {
		List<QuantityEntity> quantities = new ArrayList<>();
		for (QuantityDtoIn qmd : pQuantity) {
			Integer mealId = qmd.getMealId();
			Integer menuId = qmd.getMenuId();
			Integer mealQuantity = qmd.getQuantity();
			if (mealQuantity.intValue() == 0) {
				OrderService.LOG.debug("handleOrderQuantity - Found 0 quantity for MealId={}", mealId);
				continue;
			}

			if (mealId != null) {
				Optional<MealEntity> opResult = this.mealDao.findById(mealId);
				if (opResult.isPresent()) {
					MealEntity result = opResult.get();
					OrderService.LOG.debug("handleOrderQuantity - OK found for id={}  Meal={}", mealId, result);
					if (pConstraintId.intValue() == -1) {
						OrderService.LOG.debug("handleOrderHasMeals - constraint is disabled");
						QuantityEntity chp = new QuantityEntity();
						chp.setMeal(result);
						chp.setQuantity(mealQuantity);
						quantities.add(chp);
					} else {
						Set<Integer> dispo = result.getAvailableForWeeksAsIntegerSet();
						final Integer thisWeek = this.getCurrentWeekId();
						if (dispo == null || dispo.contains(thisWeek)) {
							// Ok
							OrderService.LOG.debug("handleOrderQuantity - OK meal {} is available for this week {}",
									mealId, thisWeek);
							QuantityEntity chp = new QuantityEntity();
							chp.setMeal(result);
							chp.setQuantity(mealQuantity);
							quantities.add(chp);
						} else {
							// KO
							OrderService.LOG.error("handleOrderQuantity - KO meal {} is NOT available for this week {}",
									mealId, thisWeek);
							throw new NotAvailableForThisWeekException(
									"Plat " + mealId + " indisponible pour la semaine " + thisWeek);
						}
					}
				} else {
					OrderService.LOG.error("handleOrderQuantity - KO meal not found for id={}", mealId);
					throw new EntityNotFoundException("Plat introuvable", mealId);
				}
			} // This was a meal link
			else if (menuId != null) {
				Optional<MenuEntity> opResult = this.menuDao.findById(menuId);
				if (opResult.isPresent()) {
					MenuEntity result = opResult.get();
					OrderService.LOG.debug("handleOrderQuantity - OK found for id={}  Menu={}", menuId, result);
					if (pConstraintId.intValue() == -1) {
						OrderService.LOG.debug("handleOrderQuantity - constraint is disabled");
						QuantityEntity chp = new QuantityEntity();
						chp.setMenu(result);
						chp.setQuantity(mealQuantity);
						quantities.add(chp);
					} else {
						Set<Integer> dispo = result.getAvailableForWeeksAsIntegerSet();
						final Integer thisWeek = this.getCurrentWeekId();
						if (dispo == null || dispo.contains(thisWeek)) {
							// Ok
							OrderService.LOG.debug("handleOrderQuantity - OK menu {} is available for this week {}",
									menuId, thisWeek);
							QuantityEntity chp = new QuantityEntity();
							chp.setMenu(result);
							chp.setQuantity(mealQuantity);
							quantities.add(chp);
						} else {
							// KO
							OrderService.LOG.error("handleOrderQuantity - KO menu {} is NOT available for this week {}",
									menuId, thisWeek);
							throw new NotAvailableForThisWeekException(
									"Menu " + menuId + " indisponible pour la semaine " + thisWeek);
						}
					}
				} else {
					OrderService.LOG.error("handleOrderQuantity - KO menu not found for id={}", menuId);
					throw new EntityNotFoundException("Menu introuvable", menuId);
				}
			} // this was a menu link
		}
		OrderService.LOG.debug("handleOrderQuantity - nb element for quantities={}", quantities.size());
		pOrder.setQuantityEntities(quantities);
	}

	/**
	 * Handles the time constraint on order method.
	 *
	 * @param pOrder        an order. That will be changed during this method.
	 * @param pConstraintId a constraint id. Can be null or -1 for no constraint
	 * @throws EntityNotFoundException if entity was not found
	 * @return true if constraint is ok (means in the accepted hours), false
	 *         otherwise
	 */
	private boolean handleTime(OrderEntity pOrder, Integer pConstraintId) throws EntityNotFoundException {
		if (pConstraintId == null) {
			OrderService.LOG.warn("handleTime - id constraint is null, will user");
			pConstraintId = Integer.valueOf(1);
		}

		if (pConstraintId.intValue() == -1) {
			OrderService.LOG.warn("handleTime - id constraint is -1, will not use constraint");
			return true;
		}
		// Handle time limit!
		Optional<ConstraintEntity> opResult = this.constraintDao.findById(pConstraintId);
		LocalTime heureLimit = null;
		if (opResult.isPresent()) {
			ConstraintEntity result = opResult.get();
			OrderService.LOG.debug("handleTime - OK found for id={}", pConstraintId);
			heureLimit = result.getOrderTimeLimit();
		} else {
			OrderService.LOG.error("handleTime - KO constraint not found for id={}", pConstraintId);
			throw new EntityNotFoundException("Contrainte introuvable", pConstraintId);
		}
		LocalTime orderTime = pOrder.getCreationTime();
		boolean doInsert = orderTime.isBefore(heureLimit);
		OrderService.LOG.debug("handleTime - Time order={}  Time limit={} ==> {})", orderTime, heureLimit,
				Boolean.valueOf(doInsert));

		return doInsert;
	}

	/**
	 * Selects all orders made by the given user.
	 *
	 * @param pUserId a user id
	 * @return all orders found for this user (all status) ordered by creation date.
	 *         Empty list if none.
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<OrderEntity> findAllByUserId(Integer pUserId) {
		OrderService.LOG.debug("findAllByUserId - {}", pUserId);
		if (pUserId == null) {
			OrderService.LOG.error("findAllByUserId  - pUserId is null");
			throw new ParameterException("Le numero d'utilisateur ne peut pas être null!", "pUserId");
		}
		Optional<List<OrderEntity>> opResult = this.orderDao.findByUserIdOrderByCreationDateAsc(pUserId);
		if (opResult.isPresent()) {
			List<OrderEntity> result = opResult.get();
			OrderService.LOG.debug("findAllByUserId - found {} values for user {}", result.size(), pUserId);
			return result;
		}
		OrderService.LOG.debug("findAllByUserId - found NO value for user {}", pUserId);
		return Collections.emptyList();
	}

	/**
	 * Selects all orders made between two dates and having the given status.
	 *
	 * @param pBeginDate a begin date. Can be null, will use now-20years.
	 * @param pEndDate   an end date. Can be null, will use now.
	 * @param pStatus    a status. Can be null will use OrderStatus.CREATED
	 * @return all orders found ordered by creation date. Empty list if none.
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<OrderEntity> findAllBetweenDateInStatus(LocalDate pBeginDate, LocalDate pEndDate, OrderStatus pStatus) {
		OrderService.LOG.debug("findAllByBetweenDate - {} and {} for state {}", pBeginDate, pEndDate, pStatus);
		if (pBeginDate != null && pEndDate != null && pBeginDate.isAfter(pEndDate)) {
			OrderService.LOG.error("findAllBetweenDateInStatus  - Begin date is after end date");
			throw new ParameterException("Les dates ne sont pas valides");
		}
		if (pStatus == null) {
			pStatus = OrderStatus.CREATED;
		}
		if (pBeginDate == null) {
			pBeginDate = LocalDate.now().minusYears(20);
		}
		if (pEndDate == null) {
			pEndDate = LocalDate.now();
		}

		Optional<List<OrderEntity>> opResult = this.orderDao
				.findByCreationDateBetweenAndStatusOrderByCreationDateAsc(pBeginDate, pEndDate, pStatus);
		if (opResult.isPresent()) {
			List<OrderEntity> result = opResult.get();
			OrderService.LOG.debug("findAllBetweenDateInStatus - found {} values", result.size());
			return result;
		}
		OrderService.LOG.debug("findAllBetweenDateInStatus - found NO value");
		return Collections.emptyList();
	}

	/**
	 * Selects all orders made by a given user between two dates whatever status.
	 *
	 * @param pUserId    a user id
	 * @param pBeginDate a begin date. Can be null, will use now-20years.
	 * @param pEndDate   an end date. Can be null, will use now.
	 * @return all orders found ordered by creation date. Empty list if none.
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<OrderEntity> findAllBetweenDateForUser(Integer pUserId, LocalDate pBeginDate, LocalDate pEndDate) {
		OrderService.LOG.debug("findAllBetweenDateForUser - {} and {} for user {}", pBeginDate, pEndDate, pUserId);
		if (pBeginDate != null && pEndDate != null && pBeginDate.isAfter(pEndDate)) {
			OrderService.LOG.error("findAllBetweenDateForUser - Begin date is after end date");
			throw new ParameterException("Les dates ne sont pas valides");
		}
		if (pUserId == null) {
			OrderService.LOG.error("findAllBetweenDateForUser  - pUserId is null");
			throw new ParameterException("Le numero d'utilisateur ne peut pas être null!");
		}
		if (pBeginDate == null) {
			pBeginDate = LocalDate.now().minusYears(20);
		}
		if (pEndDate == null) {
			pEndDate = LocalDate.now();
		}

		Optional<List<OrderEntity>> opResult = this.orderDao
				.findByCreationDateBetweenAndUserIdOrderByCreationDateAsc(pBeginDate, pEndDate, pUserId);
		if (opResult.isPresent()) {
			List<OrderEntity> result = opResult.get();
			OrderService.LOG.debug("findAllBetweenDateForUser - found {} values", result.size());
			return result;
		}
		OrderService.LOG.debug("findAllBetweenDateForUser - found NO value");
		return Collections.emptyList();
	}

	/**
	 * Selects all orders made by a given user between two dates and respecting the
	 * given status.
	 *
	 * @param pUserId    a user id
	 * @param pBeginDate a begin date. Can be null, will use now-20years.
	 * @param pEndDate   an end date. Can be null, will use now.
	 * @param pStatus    a status. Can be null will use OrderStatus.CREATED
	 * @return all orders found ordered by creation date. Empty list if none.
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<OrderEntity> findAllBetweenDateForUserInStatus(Integer pUserId, LocalDate pBeginDate,
			LocalDate pEndDate, OrderStatus pStatus) {
		OrderService.LOG.debug("findAllBetweenDateForUserInStatus - {} and {} for user {} with state {}", pBeginDate,
				pEndDate, pUserId, pStatus);
		if (pBeginDate != null && pEndDate != null && pBeginDate.isAfter(pEndDate)) {
			OrderService.LOG.error("findAllBetweenDateForUserInStatus  - Begin date is after end date");
			throw new ParameterException("Les dates ne sont pas valides");
		}
		if (pUserId == null) {
			OrderService.LOG.error("findAllBetweenDateForUserInStatus  - pUserId is null");
			throw new ParameterException("Le numero d'utilisateur ne peut pas être null!");
		}
		if (pStatus == null) {
			pStatus = OrderStatus.CREATED;
		}

		if (pBeginDate == null) {
			pBeginDate = LocalDate.now().minusYears(20);
		}
		if (pEndDate == null) {
			pEndDate = LocalDate.now();
		}

		Optional<List<OrderEntity>> opResult = this.orderDao
				.findByCreationDateBetweenAndUserIdAndStatusOrderByCreationDateAsc(pBeginDate, pEndDate, pUserId,
						pStatus);
		if (opResult.isPresent()) {
			List<OrderEntity> result = opResult.get();
			OrderService.LOG.debug("findAllBetweenDateForUserInStatus - found {} values", result.size());
			return result;
		}
		OrderService.LOG.debug("findAllBetweenDateForUserInStatus - found NO value");
		return Collections.emptyList();
	}

	/**
	 * Selects all orders made by a given user with the given status.
	 *
	 * @param pUserId a user id
	 * @param pStatus a status. Can be null will use OrderStatus.CREATED
	 * @return all orders found ordered by creation date. Empty list if none.
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<OrderEntity> findAllForUserInStatus(Integer pUserId, OrderStatus pStatus) {
		OrderService.LOG.debug("findAllForUserInStatus - for user {} with state {}", pUserId, pStatus);

		if (pUserId == null) {
			OrderService.LOG.error("findAllForUserInStatus  - pUserId is null");
			throw new ParameterException("Le numero d'utilisateur ne peut pas être null!");
		}
		if (pStatus == null) {
			pStatus = OrderStatus.CREATED;
		}

		Optional<List<OrderEntity>> opResult = this.orderDao.findByUserIdAndStatusOrderByCreationDateAsc(pUserId,
				pStatus);
		if (opResult.isPresent()) {
			List<OrderEntity> result = opResult.get();
			OrderService.LOG.debug("findAllForUserInStatus - found {} values", result.size());
			return result;
		}
		OrderService.LOG.debug("findAllForUserInStatus - found NO value");
		return Collections.emptyList();
	}

	@Override
	protected CrudRepository<OrderEntity, Integer> getTargetedDao() {
		return this.orderDao;
	}

	/**
	 * Gets the id of the current week.
	 *
	 * @return the id of the current week.
	 */
	protected Integer getCurrentWeekId() {
		return this.getWeekId(LocalDate.now());
	}

	/**
	 * Gets the id of the week from the given date.
	 *
	 * @param pDate a date
	 * @return the id of the week linked with the given date.
	 */
	private Integer getWeekId(TemporalAccessor pDate) {
		return pDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
	}
}
