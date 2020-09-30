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

import stone.lunchtime.dao.IMealDao;
import stone.lunchtime.dao.IMenuDao;
import stone.lunchtime.dto.in.MenuDtoIn;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Handle menu.
 */
@Service
public class MenuService extends AbstractServiceForEatableEntity<MenuEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IMealDao mealDao;
	@Autowired
	private IMenuDao menuDao;

	/**
	 * Will add a menu into the data base.
	 *
	 * @param pDto information to be added.
	 * @return the entity added
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public MenuEntity add(MenuDtoIn pDto) {
		MenuService.LOG.debug("add - {}", pDto);
		if (pDto == null) {
			MenuService.LOG.error("add  - DTO is null");
			throw new ParameterException("DTO est null !", "pDto");
		}
		pDto.validate();
		MenuEntity menuInsert = pDto.toEntity();
		menuInsert.setStatus(EntityStatus.ENABLED);

		this.handleMeals(menuInsert, pDto.getMealIds());

		super.handleImage(menuInsert, pDto);

		MenuEntity resultSave = this.menuDao.save(menuInsert);
		MenuService.LOG.info("add - OK with new id={}", resultSave.getId());
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
	public MenuEntity update(Integer pIdToUpdate, MenuDtoIn pNewDto) throws EntityNotFoundException {
		MenuEntity entityInDateBase = super.beginUpdate(pIdToUpdate, pNewDto);

		this.handleMeals(entityInDateBase, pNewDto.getMealIds());

		MenuEntity resultUpdate = this.menuDao.save(entityInDateBase);
		MenuService.LOG.info("update - OK in {}", this.getClass().getSimpleName());
		return resultUpdate;

	}

	/**
	 * Finds all menu available for the given week.
	 *
	 * @param pWeek a week id [1, 52]
	 * @return all meal available for this week
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public List<MenuEntity> findAllAvailableForWeek(Integer pWeek) {
		MenuService.LOG.debug("findAllAvailableForWeek - {}", pWeek);
		if (pWeek == null || pWeek.intValue() < 1 || pWeek.intValue() > 52) {
			MenuService.LOG.error("findAllAvailableForWeek  - pWeek is null or not in [1, 52]");
			throw new ParameterException(
					"Le numero de semaine de peut pas être null et doit être compris entre [1, 52] !");
		}
		Optional<List<MenuEntity>> opResult = this.menuDao.findAllAvailableForWeek(pWeek.toString());
		if (opResult.isPresent()) {
			List<MenuEntity> result = opResult.get();
			MenuService.LOG.debug("findAllAvailableForWeek - found {} values for week {}", result.size(), pWeek);
			return result;
		}
		MenuService.LOG.debug("findAllAvailableForWeek - found NO value for week");
		return Collections.emptyList();
	}

	/**
	 * Handles the join between menu and meal.
	 *
	 * @param pMenuEntity a menu
	 * @param pMealIds    a list of meal's id
	 */
	private void handleMeals(MenuEntity pMenuEntity, List<Integer> pMealIds) {
		if (pMealIds != null && !pMealIds.isEmpty()) {
			List<MealEntity> meals = new ArrayList<>();
			for (Integer mealId : pMealIds) {
				Optional<MealEntity> oldMeal = this.mealDao.findById(mealId);
				if (oldMeal.isPresent()) {
					MenuService.LOG.trace("handleMeals - adding meal with id {}", mealId);
					meals.add(oldMeal.get());
				} else {
					MenuService.LOG.warn("handleMeals - cannot add meal with id {} because not found", mealId);
				}
			}
			pMenuEntity.setMeals(meals);
		} else {
			MenuService.LOG.warn("handleMeals (no meal)");
			pMenuEntity.setMeals(null);
		}
	}

	@Override
	protected CrudRepository<MenuEntity, Integer> getTargetedDao() {
		return this.menuDao;
	}

	@Override
	protected ImageEntity getDefault() {
		return super.getImageService().saveIfNotInDataBase(DefaultImages.MENU_DEFAULT_IMG);
	}
}
