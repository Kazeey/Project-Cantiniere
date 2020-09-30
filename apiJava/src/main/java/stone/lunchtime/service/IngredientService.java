// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IIngredientDao;
import stone.lunchtime.dto.in.IngredientDtoIn;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Handle ingredient.
 */
@Service
public class IngredientService extends AbstractServiceForLabeledEntity<IngredientEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IIngredientDao ingredientDao;

	/**
	 * Will add an ingredient into the data base.
	 *
	 * @param pDto information to be added.
	 * @return the entity added
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public IngredientEntity add(IngredientDtoIn pDto) {
		IngredientService.LOG.debug("add - {}", pDto);
		if (pDto == null) {
			IngredientService.LOG.error("add - null?");
			throw new ParameterException("DTO est null !", "pDto");
		}

		pDto.validate();
		IngredientEntity ingredient = pDto.toEntity();
		ingredient.setStatus(EntityStatus.ENABLED);

		super.handleImage(ingredient, pDto);

		IngredientEntity resultSave = this.ingredientDao.save(ingredient);
		IngredientService.LOG.info("add - OK with new id={}", resultSave.getId());
		return resultSave;
	}

	/**
	 * Updates entity. <br>
	 *
	 * This method does not change status.
	 *
	 * @param pIdToUpdate an entity id. The one that needs update.
	 * @param pNewDto     the new values for this entity
	 * @return the updated entity
	 * @throws EntityNotFoundException if entity not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public IngredientEntity update(Integer pIdToUpdate, IngredientDtoIn pNewDto) throws EntityNotFoundException {
		IngredientEntity entityInDataBase = super.beginUpdate(pIdToUpdate, pNewDto);
		IngredientEntity resultUpdate = this.ingredientDao.save(entityInDataBase);
		IngredientService.LOG.info("update - OK in {}", this.getClass().getSimpleName());
		return resultUpdate;
	}

	@Override
	protected CrudRepository<IngredientEntity, Integer> getTargetedDao() {
		return this.ingredientDao;
	}

	@Override
	protected ImageEntity getDefault() {
		return super.getImageService().saveIfNotInDataBase(DefaultImages.INGREDIENT_DEFAULT_IMG);
	}
}
