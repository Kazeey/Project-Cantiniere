// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * The dto class for the exception
 */
@JsonInclude(Include.NON_NULL)
@Schema(description = "Represents an exception.")
public class ExceptionDtoOut implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	@Schema(description = "The exception classe name.")
	private String exceptionName;
	@Schema(description = "The exception message.")
	private String exceptionMessage;
	@Schema(description = "The exception cause.")
	private String exceptionCause;
	@Schema(description = "The parameter name responsible of the exception.")
	private String targetedParameter;
	@Schema(description = "The element id responsible of the exception.")
	private String targetedEntityPk;

	/**
	 * Constructor of the object.
	 */
	public ExceptionDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pException where to find information for building the DTO
	 */
	public ExceptionDtoOut(Throwable pException) {
		super();
		this.setExceptionName(pException.getClass().getName());
		this.setExceptionMessage(pException.getMessage());
		if (pException.getCause() != null) {
			try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
				pException.getCause().printStackTrace(pw);
				this.setExceptionCause(sw.toString());
			} catch (Exception e) {
				ExceptionDtoOut.LOG.fatal("Erreur lors de la recuperation de la cause", e);
			}
		}
		if (pException instanceof ParameterException) {
			this.targetedParameter = ((ParameterException) pException).getParameterName();
		}
		if (pException instanceof EntityNotFoundException) {
			this.targetedEntityPk = String.valueOf(((EntityNotFoundException) pException).getEntityId());
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the exceptionCause value.
	 */
	public String getExceptionCause() {
		return this.exceptionCause;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pExceptionCause the new value for exceptionCause attribute
	 */
	public void setExceptionCause(String pExceptionCause) {
		this.exceptionCause = pExceptionCause;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the exceptionName value.
	 */
	public String getExceptionName() {
		return this.exceptionName;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pExceptionName the new value for exceptionName attribute
	 */
	public void setExceptionName(String pExceptionName) {
		this.exceptionName = pExceptionName;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the exceptionMessage value.
	 */
	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pExceptionMessage the new value for exceptionMessage attribute
	 */
	public void setExceptionMessage(String pExceptionMessage) {
		this.exceptionMessage = pExceptionMessage;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the targetedParameter value.
	 */
	public String getTargetedParameter() {
		return this.targetedParameter;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pTargetedParameter the new value for targetedParameter attribute
	 */
	public void setTargetedParameter(String pTargetedParameter) {
		this.targetedParameter = pTargetedParameter;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the targetedEntityPk value.
	 */
	public String getTargetedEntityPk() {
		return this.targetedEntityPk;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pTargetedEntityPk the new value for targetedEntityPk attribute
	 */
	public void setTargetedEntityPk(String pTargetedEntityPk) {
		this.targetedEntityPk = pTargetedEntityPk;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" {exceptionName=");
		sb.append(this.exceptionName);
		sb.append(",exceptionMessage=");
		sb.append(this.exceptionMessage);
		sb.append(",exceptionCause=");
		sb.append(this.exceptionCause);
		sb.append(",targetedParameter=");
		sb.append(this.targetedParameter);
		sb.append(",targetedEntityPk=");
		sb.append(this.targetedEntityPk);
		sb.append("}");
		return sb.toString();
	}

}
