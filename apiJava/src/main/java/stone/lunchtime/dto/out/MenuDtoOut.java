// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.entity.MenuEntity;

/**
 * The dto class for the menu.
 */
@Schema(description = "Represents a menu. Menu can be ordered.")
public class MenuDtoOut extends AbstractEatableDtoOut {
	private static final long serialVersionUID = 1L;

	@Schema(description = "An array of meals.")
	private List<MealDtoOut> meals;

	/**
	 * Constructor of the object.
	 */
	public MenuDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pId id of the entity
	 */
	public MenuDtoOut(Integer pId) {
		super(pId);
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity entity to use for dto construction
	 */
	public MenuDtoOut(MenuEntity pEntity) {
		super(pEntity);
		List<MealEntity> lMeals = pEntity.getMeals();
		if (lMeals != null && !lMeals.isEmpty()) {
			this.meals = new ArrayList<>();
			for (MealEntity meal : lMeals) {
				this.meals.add(new MealDtoOut(meal));
			}
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the meals value.
	 */
	public List<MealDtoOut> getMeals() {
		return this.meals;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pMeals the new value for meals attribute
	 */
	public void setMeals(List<MealDtoOut> pMeals) {
		this.meals = pMeals;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",mealsId=");
		if (this.getMeals() != null && !this.getMeals().isEmpty()) {
			sb.append('[');
			for (MealDtoOut elm : this.getMeals()) {
				sb.append(elm.getId()).append(',');
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(']');
		} else {
			sb.append("[]");
		}
		sb.append("}");
		return sb.toString();
	}
}
