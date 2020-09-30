// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stone.lunchtime.entity.ConstraintEntity;

/**
 * Repository for constraint.
 */
@Repository
public interface IConstraintDao extends IDao<ConstraintEntity> {
	/**
	 * Resets all sequences for MySQL. <br>
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltconstraint AUTO_INCREMENT = 1")
	public void resetMySQLSequence();
}
