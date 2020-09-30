// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stone.lunchtime.entity.OrderEntity;
import stone.lunchtime.entity.OrderStatus;

/**
 * Repository for order.
 */
@Repository
public interface IOrderDao extends IDao<OrderEntity> {
	/**
	 * Resets all sequences for MySQL. <br>
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltorder AUTO_INCREMENT=1")
	public void resetMySQLSequence();

	/**
	 * Selects all orders made by the given user.
	 *
	 * @param pUserId a user id
	 * @return all orders found for this user (all status) ordered by creation date
	 */
	// @Query("FROM #{#entityName} WHERE user.id=:userId ORDER BY creationDate")
	// findByUserIdOrderByCreationDateAsc
	public Optional<List<OrderEntity>> findByUserIdOrderByCreationDateAsc(Integer pUserId);

	/**
	 * Selects all orders made between two dates and having the given status.
	 *
	 * @param pBeginDate a begin date.
	 * @param pEndDate   an end date.
	 * @param pStatus    a status.
	 * @return all orders found ordered by creation date.
	 */
	// @Query("FROM #{#entityName} WHERE status=:status AND creationDate BETWEEN :beginDate AND :endDate ORDER BY
	// creationDate")
	// findByStatusAndCreationDateBetweenOrderByCreationDateAsc
	public Optional<List<OrderEntity>> findByCreationDateBetweenAndStatusOrderByCreationDateAsc(LocalDate pBeginDate,
			LocalDate pEndDate, OrderStatus pStatus);

	/**
	 * Selects all orders made by a given user between two dates whatever status.
	 *
	 * @param pUserId    a user id
	 * @param pBeginDate a begin date.
	 * @param pEndDate   an end date.
	 * @return all orders found ordered by creation date.
	 */
	// @Query("FROM #{#entityName} WHERE user.id=:userId AND creationDate BETWEEN :beginDate AND :endDate ORDER BY
	// creationDate")
	// findByCreationDateBetweenAndUserIdOrderByCreationDateAsc
	public Optional<List<OrderEntity>> findByCreationDateBetweenAndUserIdOrderByCreationDateAsc(LocalDate pBeginDate,
			LocalDate pEndDate, Integer pUserId);

	/**
	 * Selects all orders made by a given user between two dates and respecting the
	 * given status.
	 *
	 * @param pUserId    a user id
	 * @param pBeginDate a begin date.
	 * @param pEndDate   an end date.
	 * @param pStatus    a status.
	 * @return all orders found ordered by creation
	 */
	// @Query("FROM #{#entityName} WHERE user.id=:userId AND status=:status AND creationDate BETWEEN :beginDate AND
	// :endDate ORDER BY creationDate")
	// findByCreationDateBetweenAndUserIdAndStatusOrderByCreationDateAsc
	public Optional<List<OrderEntity>> findByCreationDateBetweenAndUserIdAndStatusOrderByCreationDateAsc(
			LocalDate pBeginDate, LocalDate pEndDate, Integer pUserId, OrderStatus pStatus);

	/**
	 * Selects all orders made by a given user with the given status.
	 *
	 * @param pUserId a user id
	 * @param pStatus a status.
	 * @return all orders found ordered by creation date.
	 */
	// @Query("FROM #{#entityName} WHERE user.id=:userId AND status=:status ORDER BY creationDate")
	// findByUserIdAndStatusOrderByCreationDateAsc
	public Optional<List<OrderEntity>> findByUserIdAndStatusOrderByCreationDateAsc(Integer pUserId,
			OrderStatus pStatus);
}
