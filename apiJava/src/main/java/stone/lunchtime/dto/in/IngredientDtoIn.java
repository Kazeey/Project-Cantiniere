// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.IngredientEntity;

/**
 * The dto class for the ingredient.
 */
@Schema(description = "Represents an ingredient. A meal is composed of ingredients.")
public class IngredientDtoIn extends AbstractLabeledDtoIn<IngredientEntity> {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public IngredientDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pIngredientEntity entity used for construction
	 */
	public IngredientDtoIn(IngredientEntity pIngredientEntity) {
		super(pIngredientEntity);
	}

	@Override
	@JsonIgnore
	public IngredientEntity toEntity() {
		return super.toEntity(new IngredientEntity());
	}

}
