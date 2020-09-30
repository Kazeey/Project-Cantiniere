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

import stone.lunchtime.entity.ImageEntity;

/**
 * Repository for images.
 */
@Repository
public interface IImageDao extends IDao<ImageEntity> {
	/**
	 * Resets all sequences for MySQL. <br>
	 *
	 * Used for testing only.
	 */
	@Override
	@Modifying
	@Query(nativeQuery = true, value = "ALTER TABLE ltimage AUTO_INCREMENT = 1")
	public void resetMySQLSequence();

	/**
	 * Searches an image with its path.
	 *
	 * @param pPath a path to find
	 * @return the image found or none.
	 */
	public Optional<ImageEntity> findOneByImagePath(String pPath);
}
