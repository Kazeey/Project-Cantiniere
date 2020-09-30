// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.dto.in.LoginDtoIn;
import stone.lunchtime.entity.AbstractEntity;

/**
 * Mother of simple dto class used for JSon when replying.
 */
@JsonInclude(Include.NON_NULL)
@Schema(description = "Default DTO out", subTypes = { AbstractLabeledDtoOut.class, ConstraintDtoOut.class,
		LoginDtoIn.class, OrderDtoOut.class, QuantityDtoOut.class, UserDtoOut.class })
public abstract class AbstractDtoOut implements Serializable {

	private static final long serialVersionUID = 1L;
	@Schema(description = "Id of the element.")
	private Integer id;

	/**
	 * Constructor of the object. <br>
	 */
	public AbstractDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pId a value for PK
	 */
	public AbstractDtoOut(Integer pId) {
		super();
		this.setId(pId);
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pEntity an entity
	 */
	public AbstractDtoOut(AbstractEntity pEntity) {
		this(pEntity != null ? pEntity.getId() : null);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the id value.
	 */
	public final Integer getId() {
		return this.id;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pId the new value for id attribute
	 */
	public final void setId(Integer pId) {
		this.id = pId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(this.getClass().getSimpleName());
		sb.append(",id=");
		sb.append(this.getId());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Formats a number with like x.xx.
	 *
	 * @param pNumber the number to format
	 * @return the formated number
	 */
	@JsonIgnore
	public static final String formatNumber(Number pNumber) {
		if (pNumber == null || pNumber.doubleValue() == 0) {
			return "0";
		}
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
		return df.format(pNumber.doubleValue());
	}
}
