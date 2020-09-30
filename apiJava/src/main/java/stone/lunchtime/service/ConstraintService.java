// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IConstraintDao;
import stone.lunchtime.dto.in.ConstraintDtoIn;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Constraint service.
 */
@Service
public class ConstraintService extends AbstractServiceForEntity<ConstraintEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IConstraintDao constraintDao;

	/**
	 * Will add a constraint into the data base.
	 *
	 * @param pDto information to be added.
	 * @return the entity added
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public ConstraintEntity add(ConstraintDtoIn pDto) {
		ConstraintService.LOG.debug("add - {}", pDto);
		if (pDto == null) {
			ConstraintService.LOG.error("add - null?");
			throw new ParameterException("Contrainte est null !", "pDto");
		}

		pDto.validate();

		ConstraintEntity resultSave = this.constraintDao.save(pDto.toEntity());
		ConstraintService.LOG.info("add - OK with new id={}", resultSave.getId());
		return resultSave;
	}

	/**
	 * Updates the entity.
	 *
	 * @param pIdToUpdate the id of the entity to update
	 * @param pNewDto     where to find new information
	 * @return the entity updated
	 * @throws EntityNotFoundException if an error occurred
	 */
	@Transactional(rollbackFor = Exception.class)
	public ConstraintEntity update(Integer pIdToUpdate, ConstraintDtoIn pNewDto) throws EntityNotFoundException {
		ConstraintService.LOG.debug("update - {} with {}", pIdToUpdate, pNewDto);

		ConstraintEntity entityInDataBase = super.getEntityFrom(pIdToUpdate);

		if (pNewDto == null) {
			ConstraintService.LOG.error("update - null?");
			throw new ParameterException("Contrainte est null !", "pNewDto");
		}
		pNewDto.validate();

		LocalTime newTime = pNewDto.getOrderTimeLimitAsTime();
		if (newTime != null && !newTime.equals(entityInDataBase.getOrderTimeLimit())) {
			ConstraintService.LOG.debug("update - Constraint OrderTimeLimit has changed");
			entityInDataBase.setOrderTimeLimit(newTime);
		}
		if (!pNewDto.getMaximumOrderPerDay().equals(entityInDataBase.getMaximumOrderPerDay())) {
			ConstraintService.LOG.debug("update - Constraint MaximumOrderPerDay has changed");
			entityInDataBase.setMaximumOrderPerDay(pNewDto.getMaximumOrderPerDay());
		}
		if (!pNewDto.getRateVAT().equals(entityInDataBase.getRateVAT())) {
			ConstraintService.LOG.debug("update - Constraint RateVAT has changed");
			entityInDataBase.setRateVAT(pNewDto.getRateVAT());
		}
		// entityInDataBase is updated with new values
		ConstraintEntity resultUpdate = this.constraintDao.save(entityInDataBase);
		ConstraintService.LOG.info("update - OK");
		return resultUpdate;
	}

	@Override
	protected CrudRepository<ConstraintEntity, Integer> getTargetedDao() {
		return this.constraintDao;
	}

	/**
	 * Deletes the constraint. <br>
	 *
	 * Caution: Data are completely removed from data base.
	 *
	 * @param pId a constraint id
	 * @throws EntityNotFoundException if entity not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer pId) throws EntityNotFoundException {
		ConstraintService.LOG.debug("delete - {}", pId);
		ConstraintEntity entity = super.getEntityFrom(pId);
		this.constraintDao.delete(entity);
	}
}
