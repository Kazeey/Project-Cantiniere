// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.service.exception.ParameterException;

/**
 * The dto class for the menu.
 */
@Schema(description = "Represents a menu. Menu can be ordered.")
public class MenuDtoIn extends AbstractEatableDtoIn<MenuEntity> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	@Schema(description = "An array of meals id.", required = false, nullable = true)
	private List<Integer> mealIds;

	/**
	 * Constructor of the object.
	 */
	public MenuDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity entity used for construction
	 */
	public MenuDtoIn(MenuEntity pEntity) {
		super(pEntity);
		List<MealEntity> meals = pEntity.getMeals();
		if (meals != null && !meals.isEmpty()) {
			this.mealIds = new ArrayList<>();
			for (MealEntity mea : meals) {
				this.mealIds.add(mea.getId());
			}
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the mealIds value.
	 */
	public List<Integer> getMealIds() {
		return this.mealIds;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pMealIds the new value for mealIds attribute
	 */
	public void setMealIds(List<Integer> pMealIds) {
		if (pMealIds == null || pMealIds.isEmpty()) {
			this.mealIds = null;
		} else {
			this.mealIds = pMealIds;
		}
	}

	@Override
	@JsonIgnore
	public MenuEntity toEntity() {
		return super.toEntity(new MenuEntity());
	}

	@Override
	@JsonIgnore
	public void validate() {
		super.validate();
		if (this.getMealIds() != null) {
			for (Integer elm : this.getMealIds()) {
				if (elm == null || elm.intValue() <= 0) {
					MenuDtoIn.LOG.error("validate - meal id must be between ]0, +[, found {}", elm);
					throw new ParameterException("Id du plat invalide ! (doit être entre ]0, +[)", "MealIds");
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		if (this.getMealIds() != null) {
			sb.append(",mealIds=");
			sb.append(this.getMealIds());
		}
		sb.append("}");
		return sb.toString();
	}
}
