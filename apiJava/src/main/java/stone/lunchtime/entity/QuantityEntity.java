// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the QuantityEntity database table.
 */
@Entity
@Table(name = "ltquantity")
public class QuantityEntity extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "quantity")
	private Integer quantity = Integer.valueOf(0);

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "meal_id", nullable = true)
	private MealEntity meal;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "menu_id", nullable = true)
	private MenuEntity menu;

	/**
	 * Gets the attribute value.
	 *
	 * @return the quantity value.
	 */
	public Integer getQuantity() {
		return this.quantity;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pQuantity the new value for quantity attribute
	 */
	public void setQuantity(Integer pQuantity) {
		if (pQuantity == null || pQuantity.intValue() < 0) {
			this.quantity = Integer.valueOf(0);
		} else {
			this.quantity = pQuantity;
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the meal value.
	 */
	public MealEntity getMeal() {
		return this.meal;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pMeal the new value for meal attribute
	 */
	public void setMeal(MealEntity pMeal) {
		this.meal = pMeal;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the menu value.
	 */
	public MenuEntity getMenu() {
		return this.menu;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pMenu the new value for menu attribute
	 */
	public void setMenu(MenuEntity pMenu) {
		this.menu = pMenu;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",quantity=");
		sb.append(this.getQuantity());
		if (this.getMeal() != null) {
			sb.append(",mealId=");
			sb.append(this.getMeal().getId());
		} else {
			sb.append(",no meal");
		}
		if (this.getMenu() != null) {
			sb.append(",menuId=");
			sb.append(this.getMenu().getId());
		} else {
			sb.append(",no menu");
		}
		sb.append("}");
		return sb.toString();
	}
}
