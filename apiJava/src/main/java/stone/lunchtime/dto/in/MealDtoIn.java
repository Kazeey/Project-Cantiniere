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
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.service.exception.ParameterException;

/**
 * The dto class for the meal.
 */
@Schema(description = "Represents a meal. Meal can be ordered or used with menu.")
public class MealDtoIn extends AbstractEatableDtoIn<MealEntity> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	@Schema(description = "An array of ingredients id.", required = false, nullable = true)
	private List<Integer> ingredientsId;

	/**
	 * Constructor of the object.
	 */
	public MealDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity entity used for construction
	 */
	public MealDtoIn(MealEntity pEntity) {
		super(pEntity);
		List<IngredientEntity> ingredients = pEntity.getIngredients();
		if (ingredients != null && !ingredients.isEmpty()) {
			this.ingredientsId = new ArrayList<>();
			for (IngredientEntity ing : ingredients) {
				this.ingredientsId.add(ing.getId());
			}
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the ingredientsId value.
	 */
	public List<Integer> getIngredientsId() {
		return this.ingredientsId;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pIngredientsId the new value for ingredientsId attribute
	 */
	public void setIngredientsId(List<Integer> pIngredientsId) {
		if (pIngredientsId == null || pIngredientsId.isEmpty()) {
			this.ingredientsId = null;
		} else {
			this.ingredientsId = pIngredientsId;
		}
	}

	@Override
	@JsonIgnore
	public MealEntity toEntity() {
		return super.toEntity(new MealEntity());
	}

	@Override
	@JsonIgnore
	public void validate() {
		super.validate();
		if (this.getIngredientsId() != null) {
			for (Integer elm : this.ingredientsId) {
				if (elm == null || elm.intValue() <= 0) {
					MealDtoIn.LOG.error("validate - ingredient id must be between ]0, +[, found {}", elm);
					throw new ParameterException("Id d'ingredient invalide ! (doit être entre ]0, +[)",
							"IngredientsId");
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
		if (this.getIngredientsId() != null) {
			sb.append(",ingredientsId=");
			sb.append(this.getIngredientsId());
		}
		sb.append("}");
		return sb.toString();
	}

}
