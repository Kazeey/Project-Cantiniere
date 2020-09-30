// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stone.lunchtime.entity.UserEntity;

/**
 * Repository for user.
 */
@Repository
public interface IUserDao extends IDao<UserEntity> {
	/**
	 * Resets all sequences for MySQL.
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltuser AUTO_INCREMENT = 1")
	public void resetMySQLSequence();

	/**
	 * Find a user with its email and password.
	 *
	 * @param pEmail    an email
	 * @param pPassword a password
	 * @return the user found if any
	 */
	public Optional<UserEntity> findOneByEmailAndPassword(String pEmail, String pPassword);

	/**
	 * Find a user with its email and password.
	 *
	 * @param pEmail an email
	 * @return the user found if any
	 */
	public Optional<UserEntity> findOneByEmail(String pEmail);

}
