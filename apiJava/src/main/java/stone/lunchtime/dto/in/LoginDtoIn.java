// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.ParameterException;

/**
 * The dto class for the login only.
 */
@Schema(description = "Login informations.")
public class LoginDtoIn extends AbstractDtoIn<UserEntity> {
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Schema(description = "Email of a user.", required = true, nullable = false, example = "toto@aol.com")
	private String email;
	@Schema(description = "Password of the user.", required = true, nullable = false)
	private String password;

	/**
	 * Constructor of the object.
	 */
	public LoginDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEmail an email
	 * @param pPassword a password
	 */
	public LoginDtoIn(String pEmail, String pPassword) {
		super();
		this.setEmail(pEmail);
		this.setPassword(pPassword);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the email value.
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the password value.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pEmail the new value for email attribute
	 */
	public void setEmail(String pEmail) {
		this.email = super.checkAndClean(pEmail);
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pPassword the new value for password attribute
	 */
	public void setPassword(String pPassword) {
		this.password = super.checkAndClean(pPassword);
	}

	@Override
	@JsonIgnore
	public void validate() {
		if (this.getEmail() == null) {
			LoginDtoIn.LOG.error("validate (User email is null)");
			throw new ParameterException("Email est null ou vide !", "email");
		}
		if (this.getPassword() == null) {
			LoginDtoIn.LOG.error("validate (User password is null)");
			throw new ParameterException("Mot de passe est null ou vide !", "password");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",email=");
		// DONT append password
		sb.append("}");
		return sb.toString();
	}

	@Override
	public UserEntity toEntity() {
		throw new IllegalAccessError("Do not use");
	}
}
