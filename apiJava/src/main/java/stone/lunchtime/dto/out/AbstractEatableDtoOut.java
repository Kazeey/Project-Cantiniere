// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import java.math.BigDecimal;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.AbstractEatableEntity;

/**
 * Dto with price and availableForWeeks.
 */
@Schema(description = "Represents a labeled element.", subTypes = { MealDtoOut.class, MenuDtoOut.class })
public abstract class AbstractEatableDtoOut extends AbstractLabeledDtoOut {
	private static final long serialVersionUID = 1L;

	@Schema(description = "The price duty free for this element.")
	private BigDecimal priceDF;
	@Schema(description = "An array that represents the week number when this element is available.")
	private Set<Integer> availableForWeeks;

	/**
	 * Constructor of the object. <br>
	 */
	public AbstractEatableDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pId a value for PK
	 */
	public AbstractEatableDtoOut(Integer pId) {
		super(pId);
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pEntity an entity
	 */
	public AbstractEatableDtoOut(AbstractEatableEntity pEntity) {
		super(pEntity);
		this.setPriceDF(pEntity.getPriceDF());
		this.setAvailableForWeeks(pEntity.getAvailableForWeeksAsIntegerSet());
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the availableForWeeks value.
	 */
	public Set<Integer> getAvailableForWeeks() {
		return this.availableForWeeks;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pAvailableForWeeks the new value for availableForWeeks attribute
	 */
	public void setAvailableForWeeks(Set<Integer> pAvailableForWeeks) {
		this.availableForWeeks = pAvailableForWeeks;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the priceDF value.
	 */
	public BigDecimal getPriceDF() {
		return this.priceDF;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pPriceDF the new value for priceDF attribute
	 */
	public void setPriceDF(BigDecimal pPriceDF) {
		this.priceDF = pPriceDF;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",priceDF=");
		sb.append(AbstractDtoOut.formatNumber(this.getPriceDF()));
		sb.append(",availableForWeeks=");
		if (this.getAvailableForWeeks() != null) {
			sb.append(this.getAvailableForWeeks());
		} else {
			sb.append("all");
		}
		sb.append("}");
		return sb.toString();
	}
}
