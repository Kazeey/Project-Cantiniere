// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import stone.lunchtime.dto.in.AbstractEatableDtoIn;
import stone.lunchtime.entity.AbstractEatableEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;

/**
 * Mother class of all services that handle eatable entity.
 *
 * @param <T> Entity targeted by this service
 */
@Service
abstract class AbstractServiceForEatableEntity<T extends AbstractEatableEntity>
		extends AbstractServiceForLabeledEntity<T> {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Will begin the update process. <br>
	 *
	 * This method does not call save method.
	 *
	 * @param pIdToUpdate an entity id. The one that needs update.
	 * @param pNewDto     the new values for this entity
	 * @return the partially updated entity
	 * @throws EntityNotFoundException if entity not found
	 * @throws ParameterException      if parameter is invalid
	 */
	protected T beginUpdate(Integer pIdToUpdate, AbstractEatableDtoIn<T> pNewDto) throws EntityNotFoundException {

		T entityInDateBase = super.beginUpdate(pIdToUpdate, pNewDto);

		if (!pNewDto.getPriceDF().equals(entityInDateBase.getPriceDF())) {
			AbstractServiceForEatableEntity.LOG.debug("beginUpdate - Entity PrixHT has changed");
			entityInDateBase.setPriceDF(pNewDto.getPriceDF());
		}

		if (pNewDto.getAvailableForWeeks() != null) {
			if (!pNewDto.getAvailableForWeeks().equals(entityInDateBase.getAvailableForWeeksAsIntegerSet())) {
				AbstractServiceForEatableEntity.LOG.debug("beginUpdate - Entity AvailableForWeeks has changed");
				entityInDateBase.setAvailableForWeeksAsIntegerSet(pNewDto.getAvailableForWeeks());
			}
		} else {
			entityInDateBase.setAvailableForWeeks(null);
		}

		return entityInDateBase;
	}

}
