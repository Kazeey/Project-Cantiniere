// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * The persistent class for the meal database table.
 */
@Entity
@Table(name = "ltmeal")
public class MealEntity extends AbstractEatableEntity {
	private static final long serialVersionUID = 1L;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ltmeal_has_ingredient", joinColumns = {
			@JoinColumn(name = "meal_id", nullable = false, referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "ingredient_id", nullable = false, referencedColumnName = "id") })
	private List<IngredientEntity> ingredients;

	/**
	 * Gets the attribute value.
	 *
	 * @return the ingredients value.
	 */
	public List<IngredientEntity> getIngredients() {
		return this.ingredients;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pIngredients the new value for ingredients attribute
	 */
	public void setIngredients(List<IngredientEntity> pIngredients) {
		this.ingredients = pIngredients;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		if (this.getIngredients() != null && !this.getIngredients().isEmpty()) {
			sb.append(",ingredientIds=[");
			for (IngredientEntity lIngredient : this.getIngredients()) {
				sb.append(lIngredient.getId()).append(',');
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(']');
		}

		sb.append("}");
		return sb.toString();
	}

}
