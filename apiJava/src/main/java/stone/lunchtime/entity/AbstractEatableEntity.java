// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.entity;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The persistent class for the eatable table.
 */
@MappedSuperclass
public abstract class AbstractEatableEntity extends AbstractLabeledEntity {
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Column(name = "price_df", precision = 5, scale = 2)
	private BigDecimal priceDF;

	@Column(name = "available_for_weeks", length = 300)
	private String availableForWeeks;

	/**
	 * Gets the attribute value.
	 *
	 * @return the availableForWeeks value.
	 */
	public String getAvailableForWeeks() {
		return this.availableForWeeks;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the availableForWeeks value as Integer Set.
	 */
	public Set<Integer> getAvailableForWeeksAsIntegerSet() {
		return super.stringToSet(this.availableForWeeks);
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pVal the availableForWeeks value as Integer Set.
	 */
	public void setAvailableForWeeksAsIntegerSet(Set<Integer> pVal) {
		if (pVal == null || pVal.isEmpty()) {
			this.availableForWeeks = null;
		} else {
			StringBuilder sb = new StringBuilder();
			for (Integer lInteger : pVal) {
				sb.append('S').append(lInteger).append("S,");
			}
			sb.delete(sb.length() - 1, sb.length());
			this.availableForWeeks = sb.toString();
		}
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pAvailableForWeeks the new value for availableForWeeks attribute
	 */
	public void setAvailableForWeeks(String pAvailableForWeeks) {
		this.availableForWeeks = super.checkAndClean(pAvailableForWeeks);
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
		if (pPriceDF == null || pPriceDF.doubleValue() < 0 || pPriceDF.doubleValue() > 999) {
			AbstractEatableEntity.LOG.warn("Will use default price for entity {}", this.getClass().getSimpleName());
			this.priceDF = BigDecimal.valueOf(0.01D);
		} else {
			this.priceDF = pPriceDF;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",priceDF=");
		sb.append(this.getPriceDF());
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
