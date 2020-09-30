// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.dto.out.AbstractDtoOut;
import stone.lunchtime.entity.AbstractEatableEntity;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Dto with price and disponibilities.
 *
 * Package visibility
 *
 * @param <T> The targeted entity
 */
@Schema(description = "Represents a labeled element.", subTypes = { MealDtoIn.class, MenuDtoIn.class })
public abstract class AbstractEatableDtoIn<T extends AbstractEatableEntity> extends AbstractLabeledDtoIn<T> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	@Schema(description = "The price duty free for this element.", required = true, nullable = false, minimum = "0", maximum = "999")
	private BigDecimal priceDF;
	@Schema(description = "An array that represents the week number when this element is available.", required = false, nullable = true)
	private Set<Integer> availableForWeeks;

	/**
	 * Constructor of the object. <br>
	 */
	public AbstractEatableDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pEntity an entity
	 */
	public AbstractEatableDtoIn(T pEntity) {
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
		if (pAvailableForWeeks == null || pAvailableForWeeks.isEmpty()) {
			this.availableForWeeks = null;
		} else {
			this.availableForWeeks = pAvailableForWeeks;
		}
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
	@JsonIgnore
	public void validate() {
		if (this.getPriceDF() == null) {
			AbstractEatableDtoIn.LOG.error("validate - priceDF should not be null");
			throw new ParameterException("Il faut indiquer un prix HT", "priceDF");
		}
		if (this.getPriceDF() != null
				&& (this.getPriceDF().doubleValue() <= 0.001D || this.getPriceDF().doubleValue() > 999.99D)) {
			AbstractEatableDtoIn.LOG.error("validate - priceDF must be between ]0.001, 999.99]");
			throw new ParameterException("Prix HT invalid ! (doit être entre 0.001 et 999.99)", "priceDF");
		}

		if (this.getAvailableForWeeks() != null && !this.getAvailableForWeeks().isEmpty()) {
			for (Integer wId : this.getAvailableForWeeks()) {
				if (wId.intValue() < 1 || wId.intValue() > 52) {
					AbstractEatableDtoIn.LOG.error("validate - availableForWeeks value must be between [1, 52]");
					throw new ParameterException("Numero de semaine invalid ! (doit être entre 1 et 52)",
							"availableForWeeks");
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

	@Override
	@JsonIgnore
	protected T toEntity(T result) {
		super.toEntity(result);
		result.setAvailableForWeeksAsIntegerSet(this.getAvailableForWeeks());
		result.setPriceDF(this.getPriceDF());
		return result;
	}
}
