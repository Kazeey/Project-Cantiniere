// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.entity.MealEntity;

/**
 * The dto class for the meal.
 */
@Schema(description = "Represents a meal. Meal can be ordered or used with menu.")
public class MealDtoOut extends AbstractEatableDtoOut {
	private static final long serialVersionUID = 1L;

	@Schema(description = "An array of ingredients.")
	private List<IngredientDtoOut> ingredients;

	/**
	 * Constructor of the object.
	 */
	public MealDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pId id of the entity
	 */
	public MealDtoOut(Integer pId) {
		super(pId);
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity entity to use for dto construction
	 */
	public MealDtoOut(MealEntity pEntity) {
		super(pEntity);
		List<IngredientEntity> ingredientsEntity = pEntity.getIngredients();
		if (ingredientsEntity != null && !ingredientsEntity.isEmpty()) {
			this.ingredients = new ArrayList<>();
			for (IngredientEntity lIngredientEntity : ingredientsEntity) {
				this.ingredients.add(new IngredientDtoOut(lIngredientEntity));
			}
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the ingredients value.
	 */
	public List<IngredientDtoOut> getIngredients() {
		return this.ingredients;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pIngredients the new value for ingredients attribute
	 */
	public void setIngredients(List<IngredientDtoOut> pIngredients) {
		this.ingredients = pIngredients;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",ingredientsId=");
		if (this.getIngredients() != null && !this.getIngredients().isEmpty()) {
			sb.append('[');
			for (IngredientDtoOut elm : this.ingredients) {
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
