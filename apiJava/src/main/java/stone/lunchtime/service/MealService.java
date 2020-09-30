// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IIngredientDao;
import stone.lunchtime.dao.IMealDao;
import stone.lunchtime.dto.in.MealDtoIn;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Handle meal.
 */
@Service
public class MealService extends AbstractServiceForEatableEntity<MealEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IMealDao mealDao;
	@Autowired
	private IIngredientDao ingredientDao;

	/**
	 * Will add a meal into the data base.
	 *
	 * @param pDto information to be added.
	 * @return the entity added
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public MealEntity add(MealDtoIn pDto) {
		MealService.LOG.debug("add - {}", pDto);

		if (pDto == null) {
			MealService.LOG.error("add  - DTO is null");
			throw new ParameterException("DTO est null !", "pDto");
		}
		pDto.toString();
		pDto.validate();

		MealEntity meal = pDto.toEntity();
		meal.setStatus(EntityStatus.ENABLED);
		this.handleIngredients(meal, pDto.getIngredientsId());

		super.handleImage(meal, pDto);

		MealEntity resultSave = this.mealDao.save(meal);
		MealService.LOG.info("add - OK with new id={}", resultSave.getId());
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
	public MealEntity update(Integer pIdToUpdate, MealDtoIn pNewDto) throws EntityNotFoundException {
		MealEntity entityInDateBase = super.beginUpdate(pIdToUpdate, pNewDto);
		this.handleIngredients(entityInDateBase, pNewDto.getIngredientsId());
		MealEntity resultUpdate = this.mealDao.save(entityInDateBase);
		MealService.LOG.info("update - OK in {}", this.getClass().getSimpleName());
		return resultUpdate;
	}

	/**
	 * Handles the join between meal and ingredients.
	 *
	 * @param pMealEntity    a meal. That will be changed during this method.
	 * @param pIngredientIds a list of ingredient's id
	 */
	private void handleIngredients(MealEntity pMealEntity, List<Integer> pIngredientIds) {
		if (pIngredientIds != null && !pIngredientIds.isEmpty()) {
			List<IngredientEntity> ingredients = new ArrayList<>();
			for (Integer ingredientId : pIngredientIds) {
				Optional<IngredientEntity> opIngredient = this.ingredientDao.findById(ingredientId);
				if (opIngredient.isPresent()) {
					IngredientEntity entityFound = opIngredient.get();
					if (entityFound.isDeleted()) {
						MealService.LOG.warn(
								"handleIngredients - cannot add ingredient with id {} because it is deleted",
								ingredientId);
					} else {
						MealService.LOG.trace("handleIngredients - adding ingredient with id {}", ingredientId);
						ingredients.add(entityFound);
					}
				} else {
					MealService.LOG.warn("handleIngredients - cannot add ingredient with id {} because not found",
							ingredientId);
				}
			}
			MealService.LOG.trace("handleIngredients - adding {} ingredients", ingredients.size());
			pMealEntity.setIngredients(ingredients);
		} else {
			pMealEntity.setIngredients(null);
		}
	}

	@Override
	protected CrudRepository<MealEntity, Integer> getTargetedDao() {
		return this.mealDao;
	}

	/**
	 * Finds all meal available for the given week.
	 *
	 * @param pWeek a week id [1, 52]
	 * @return all meal available for this week
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<MealEntity> findAllAvailableForWeek(Integer pWeek) {
		MealService.LOG.debug("findAllAvailableForWeek - {}", pWeek);
		if (pWeek == null || pWeek.intValue() < 1 || pWeek.intValue() > 52) {
			MealService.LOG.error("findAllAvailableForWeek  - pSemaine is null or not in [1, 52]");
			throw new ParameterException(
					"Le numero de semaine de peut pas être null et doit être compris entre [1, 52] !", "pWeek");
		}
		Optional<List<MealEntity>> opResult = this.mealDao.findAllAvailableForWeek(pWeek.toString());
		if (opResult.isPresent()) {
			List<MealEntity> result = opResult.get();
			MealService.LOG.debug("findAllAvailableForWeek - found {} values for week {}", result.size(), pWeek);
			return result;
		}
		MealService.LOG.debug("findAllAvailableForWeek - found NO value for week");
		return Collections.emptyList();
	}

	@Override
	protected ImageEntity getDefault() {
		return super.getImageService().saveIfNotInDataBase(DefaultImages.MEAL_DEFAULT_IMG);
	}

}
