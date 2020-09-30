// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stone.lunchtime.entity.MealEntity;

/**
 * Repository for meal.
 */
@Repository
public interface IMealDao extends ILabeledDao<MealEntity> {
	/**
	 * Resets all sequences for MySQL. <br>
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltmeal AUTO_INCREMENT = 1")
	public void resetMySQLSequence();

	/**
	 * Finds all meal, created, and in the given week.
	 *
	 * @param pWeek a week id [1,52]
	 * @return all meal found in an Option object.
	 */
	@Query("FROM #{#entityName} where status=0 AND (availableForWeeks IS NULL OR availableForWeeks LIKE CONCAT('%S',:week,'S%'))")
	public Optional<List<MealEntity>> findAllAvailableForWeek(@Param("week") String pWeek);
}
