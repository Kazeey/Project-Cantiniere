// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import stone.lunchtime.entity.AbstractEntity;

/**
 * Default DAO parent.
 *
 * @param <T> The targeted entity
 */
@NoRepositoryBean
public interface IDao<T extends AbstractEntity> extends PagingAndSortingRepository<T, Integer> {

	/**
	 * Resets all sequences for MySQL. <br>
	 *
	 * Used for testing only.
	 */
	@Modifying
	public abstract void resetMySQLSequence();
}
