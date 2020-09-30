// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.ConstraintEntity;

/**
 * The dto class for the constraint database table.
 */
@Schema(description = "Constraint element used for global operations.")
public class ConstraintDtoIn extends AbstractDtoIn<ConstraintEntity> {
	public static final String PATTERN = "HH:mm:ss";
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Schema(description = "The global time limit for ordering.", required = false, nullable = true, example = "10:30:00")
	private String orderTimeLimit = "10:30:00";
	@Schema(description = "The global maximum number of order for a day. Not used in current version.", required = false, nullable = true, minimum = "1")
	private Integer maximumOrderPerDay = Integer.valueOf(500);
	@Schema(description = "The global VAT % value.", required = false, nullable = true, example = "20", minimum = "20", maximum = "100")
	private BigDecimal rateVAT = BigDecimal.valueOf(20D);

	/**
	 * Constructor of the object.
	 */
	public ConstraintDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity an entity
	 */
	public ConstraintDtoIn(ConstraintEntity pEntity) {
		super();
		this.setOrderTimeLimit(
				pEntity.getOrderTimeLimit().format(DateTimeFormatter.ofPattern(ConstraintDtoIn.PATTERN)));
		this.setMaximumOrderPerDay(pEntity.getMaximumOrderPerDay());
		this.setRateVAT(pEntity.getRateVAT());
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the orderTimeLimit value.
	 */
	public String getOrderTimeLimit() {
		return this.orderTimeLimit;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the orderTimeLimit value.
	 */
	@JsonIgnore
	public LocalTime getOrderTimeLimitAsTime() {
		try {
			return LocalTime.parse(this.getOrderTimeLimit(), DateTimeFormatter.ofPattern(ConstraintDtoIn.PATTERN));
		} catch (Exception lExp) {
			ConstraintDtoIn.LOG.warn("Error with date", lExp);
		}
		return null;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pOrderTimeLimit the new value for orderTimeLimit attribute
	 */
	public void setOrderTimeLimit(String pOrderTimeLimit) {
		this.orderTimeLimit = super.checkAndClean(pOrderTimeLimit);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the maximumOrderPerDay value.
	 */
	public Integer getMaximumOrderPerDay() {
		return this.maximumOrderPerDay;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pMaximumOrderPerDay the new value for maximumOrderPerDay attribute
	 */
	public void setMaximumOrderPerDay(Integer pMaximumOrderPerDay) {
		if (pMaximumOrderPerDay == null || pMaximumOrderPerDay.intValue() <= 0) {
			this.maximumOrderPerDay = Integer.valueOf(500);
		} else {
			this.maximumOrderPerDay = pMaximumOrderPerDay;
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the rateVAT value.
	 */
	public BigDecimal getRateVAT() {
		return this.rateVAT;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pRateVAT the new value for rateVAT attribute
	 */
	public void setRateVAT(BigDecimal pRateVAT) {
		if (pRateVAT == null || pRateVAT.doubleValue() < 0) {
			this.rateVAT = BigDecimal.valueOf(20D);
		} else {
			this.rateVAT = pRateVAT;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",orderTimeLimit=");
		sb.append(this.getOrderTimeLimit());
		sb.append(",maximumOrderPerDay=");
		sb.append(this.getMaximumOrderPerDay());
		sb.append(",rateVAT=");
		sb.append(this.getRateVAT());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Validate DTO
	 *
	 * @throws ParameterException if an error occurred
	 */
	@Override
	@JsonIgnore
	public void validate() {
		// Reset default values if value are null
		if (this.getMaximumOrderPerDay() == null || this.getMaximumOrderPerDay().intValue() <= 0) {
			this.setMaximumOrderPerDay(null);
		}
		if (this.getRateVAT() == null || this.getRateVAT().doubleValue() < 0) {
			this.setRateVAT(null);
		}
		if (this.getOrderTimeLimit() == null) {
			this.setOrderTimeLimit(null);
		}
	}

	@Override
	public ConstraintEntity toEntity() {
		ConstraintEntity result = new ConstraintEntity();
		result.setOrderTimeLimit(this.getOrderTimeLimitAsTime());
		result.setMaximumOrderPerDay(this.getMaximumOrderPerDay());
		result.setRateVAT(this.getRateVAT());
		return result;
	}
}
