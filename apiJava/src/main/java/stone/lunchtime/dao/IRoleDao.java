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
import org.springframework.stereotype.Repository;

import stone.lunchtime.entity.RoleEntity;

/**
 * Repository for role.
 */
@Repository
public interface IRoleDao extends IDao<RoleEntity> {
	/**
	 * Resets all sequences for MySQL.
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltrole AUTO_INCREMENT = 1")
	public void resetMySQLSequence();

	/**
	 * Counts how many lunch lady are in the data base.
	 *
	 * @return the number of lunch lady in the data base
	 */
	@Query("SELECT COUNT(id) FROM #{#entityName} WHERE label='ROLE_LUNCHLADY' and user.status = 0")
	public int countLunchLady();

	/**
	 * Find all lunch ladies role.
	 *
	 * @return all lunch ladies role.
	 */
	@Query("FROM #{#entityName} WHERE label='ROLE_LUNCHLADY' and user.status = 0")
	public Optional<List<RoleEntity>> findLunchLadyRoles();
}
