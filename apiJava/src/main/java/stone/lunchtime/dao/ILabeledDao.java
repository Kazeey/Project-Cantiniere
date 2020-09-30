// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import stone.lunchtime.entity.AbstractLabeledEntity;

/**
 * Repository for labeled entity. <br>
 *
 * Not a real repository.
 *
 * @param <T> The targeted entity
 */
@NoRepositoryBean
public interface ILabeledDao<T extends AbstractLabeledEntity> extends IDao<T> {

	/**
	 * Definition for a search method on labels.
	 *
	 * @param pLabel a label.
	 * @return all entity that look like this label
	 */
	@Query("FROM #{#entityName} where status=0 AND label LIKE CONCAT('%',:label,'%')")
	public Optional<List<T>> findLikeLabel(@Param("label") String pLabel);

	/**
	 * Finds all none deleted entity.
	 *
	 * @return all none deleted entity.
	 */
	@Query("FROM #{#entityName} where status!=2")
	public Optional<List<T>> findAllNotDeleted();

	/**
	 * Finds all enabled entity.
	 *
	 * @return all enabled entity.
	 */
	@Query("FROM #{#entityName} where status=0")
	public Optional<List<T>> findAllEnabled();
}
