// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.entity.AbstractEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Mother class of all services that handle Entity.
 *
 * @param <T> Entity targeted by this service
 */
@Service
abstract class AbstractServiceForEntity<T extends AbstractEntity> {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Gets an entity from the data base.
	 *
	 * @param pEntityPrimaryKey an entity primary key value
	 * @return the entity found, throws an exception if not found
	 * @throws EntityNotFoundException if entity was not found
	 * @throws ParameterException      if parameter is invalid
	 */
	protected T getEntityFrom(Integer pEntityPrimaryKey) throws EntityNotFoundException {
		if (pEntityPrimaryKey == null) {
			AbstractServiceForEntity.LOG.error("find - null?");
			throw new ParameterException("id est null !", "pEntityPrimaryKey");
		}
		Optional<T> optionalResult = this.getTargetedDao().findById(pEntityPrimaryKey);
		if (!optionalResult.isPresent()) {
			AbstractServiceForEntity.LOG.error("Entity with id {} was not found for service {}", pEntityPrimaryKey,
					this.getClass().getSimpleName());
			throw new EntityNotFoundException("Entite introuvable.", pEntityPrimaryKey);
		}
		return optionalResult.get();
	}

	/**
	 * Finds an entity giving its pk.
	 *
	 * @param pEntityPrimaryKey an entity primary key value
	 * @return the entity found, throws an exception if not found
	 * @throws EntityNotFoundException if entity was not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public T find(Integer pEntityPrimaryKey) throws EntityNotFoundException {
		AbstractServiceForEntity.LOG.debug("find - {} in {}", pEntityPrimaryKey, this.getClass().getSimpleName());
		if (pEntityPrimaryKey == null) {
			AbstractServiceForEntity.LOG.error("find - null?");
			throw new ParameterException("id est null !", "pEntityPrimaryKey");
		}
		if (pEntityPrimaryKey.intValue() <= 0) {
			AbstractServiceForEntity.LOG.error("find - 0?");
			throw new ParameterException("id est inferieur ou egal 0 !", "pEntityPrimaryKey");
		}
		T result = this.getEntityFrom(pEntityPrimaryKey);
		AbstractServiceForEntity.LOG.info("find - OK found entity for id={} for service {}", pEntityPrimaryKey,
				this.getClass().getSimpleName());
		return result;
	}

	/**
	 * Finds all entities.
	 *
	 * @return the entities found, an empty list if none
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<T> findAll() {
		AbstractServiceForEntity.LOG.debug("findAll - for service {}", this.getClass().getSimpleName());
		Iterable<T> iterable = this.getTargetedDao().findAll();
		List<T> result = new ArrayList<>();
		iterable.forEach(result::add);
		AbstractServiceForEntity.LOG.info("findAll - Found {} values for service {}", result.size(),
				this.getClass().getSimpleName());
		return result;
	}

	/**
	 * Gets the repository linked with this service.
	 *
	 * @return the repository linked with this service.
	 */
	protected abstract CrudRepository<T, Integer> getTargetedDao();
}
