// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stone.lunchtime.entity.IngredientEntity;

/**
 * Repository for Ingredient.
 */
@Repository
public interface IIngredientDao extends ILabeledDao<IngredientEntity> {
	/**
	 * Resets all sequences for MySQL. <br>
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltingredient AUTO_INCREMENT = 1")
	public void resetMySQLSequence();
}
