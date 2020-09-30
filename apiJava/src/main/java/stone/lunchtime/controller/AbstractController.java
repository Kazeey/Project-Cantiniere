// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import stone.lunchtime.dto.out.UserDtoOut;

/**
 * Mother class of all controller.
 */
@RestController
public abstract class AbstractController {
	private static final Logger LOG = LogManager.getLogger();
	/** Key used to store logged user in session. */
	public static final String KEY_USER = "user";

	protected static final String MSG_AUTH_AND_LL = "Vous devez vous authentifier et avoir le rôle de cantinière!";
	protected static final String MSG_AUTH_OR_LL = "Vous devez vous authentifier ou avoir le rôle de cantinière!";
	protected static final String MSG_AUTH = "Vous devez vous authentifier!";

	@Value("${configuration.date.pattern}")
	private String datePattern;

	/**
	 * Gets the connected user.
	 *
	 * @return the connected user or null if none found
	 */
	protected UserDtoOut getConnectedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDtoOut resu = null;
		if (auth != null) {
			UserDtoOut user = (UserDtoOut) auth.getDetails();
			resu = user != null ? user : null;
		}
		return resu;
	}

	/**
	 * Gets the connected user id.
	 *
	 * @return the connected user id or null if none found
	 */
	protected Integer getConnectedUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Integer resu = null;
		if (auth != null) {

			Object obj = auth.getDetails();
			if (obj instanceof UserDtoOut) {
				UserDtoOut user = (UserDtoOut) auth.getDetails();
				resu = user != null ? user.getId() : null;
			}
		}
		return resu;
	}

	/**
	 * Indicates if the user is authenticate or not.
	 *
	 * @return true if the user is authenticate, false otherwise
	 */
	protected boolean isConnected() {
		return this.getConnectedUser() != null;
	}

	/**
	 * Indicates if the authenticate user has the lunch lady role or not.
	 *
	 * @return true if the authenticate user has the lunch lady role, false
	 *         otherwise
	 */
	protected boolean hasLunchLadyRole() {
		UserDtoOut dto = this.getConnectedUser();
		return dto != null && dto.getIsLunchLady().booleanValue();
	}

	/**
	 * Indicates if the authenticate user has the same id as the one given.
	 *
	 * @param pId     a user id
	 * @return true if the authenticate user.id = pId, false otherwise
	 */
	protected boolean isTheConnectedUser(Integer pId) {
		Integer connectedUserId = this.getConnectedUserId();
		return pId != null && connectedUserId != null && pId.equals(connectedUserId);
	}

	/**
	 * Transforms a String into a date.
	 *
	 * @param pDateValue a date value
	 * @return the date
	 */
	protected LocalDate getDate(String pDateValue) {
		if (pDateValue != null && !pDateValue.trim().isEmpty()) {
			try {
				return LocalDate.parse(pDateValue, DateTimeFormatter.ofPattern(this.datePattern));
			} catch (Exception lExp) {
				AbstractController.LOG.warn("Error, date is not valid", lExp);
			}
		}
		return null;
	}
}
