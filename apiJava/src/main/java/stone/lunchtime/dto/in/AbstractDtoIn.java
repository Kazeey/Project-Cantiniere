// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.AbstractEntity;

/**
 * The mother of all dto in class.
 *
 * A DTO in is a JSon coming FROM the client.
 *
 * @param <T> The targeted entity
 */
@Schema(description = "Default DTO In", subTypes = { AbstractLabeledDtoIn.class, ConstraintDtoIn.class,
		LoginDtoIn.class, OrderDtoIn.class, QuantityDtoIn.class, UserDtoIn.class })
public abstract class AbstractDtoIn<T extends AbstractEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public AbstractDtoIn() {
		super();
	}

	/**
	 * Will transform the DTO into an entity. <br>
	 * This entity may not be complete.
	 *
	 * @return an entity
	 */
	@JsonIgnore
	public abstract T toEntity();

	/**
	 * Will validate the DTO.
	 *
	 * @throws ParameterException if an error occurred
	 */
	@JsonIgnore
	public abstract void validate();

	/**
	 * Checks if value is empty or null.
	 *
	 * @param pValue a value
	 * @return this value trimmed or null
	 */
	@JsonIgnore
	protected final String checkAndClean(String pValue) {
		if (pValue == null || pValue.trim().isEmpty()) {
			return null;
		}
		return pValue.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" {}");
		return sb.toString();
	}
}
