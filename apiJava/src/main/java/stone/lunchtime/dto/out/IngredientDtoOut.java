// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.IngredientEntity;

/**
 * The dto class for the ingredient.
 */
@Schema(description = "Represents an ingredient. A meal is composed of ingredients.")
public class IngredientDtoOut extends AbstractLabeledDtoOut {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public IngredientDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pId id of the entity
	 */
	public IngredientDtoOut(Integer pId) {
		super(pId);
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity entity used for construction
	 */
	public IngredientDtoOut(IngredientEntity pEntity) {
		super(pEntity);
	}

}
