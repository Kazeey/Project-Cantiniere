// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.Sex;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.ParameterException;

/**
 * The dto class for the user.
 */
@Schema(description = "Represents a user of the application.")
public class UserDtoIn extends AbstractDtoIn<UserEntity> {
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Schema(description = "Adress of the user.", required = false, nullable = true, example = "3 road of iron")
	private String address;
	@Schema(description = "Amount of money owned by the user.", required = true, nullable = false, example = "35.5", minimum = "0", maximum = "999")
	private BigDecimal wallet;
	@Schema(description = "Postal code of the user.", required = false, nullable = true, example = "78140")
	private String postalCode;
	@Schema(description = "Email of the user.", required = true, nullable = false, example = "toto@aol.com")
	private String email;
	@Schema(description = "Indicates if the user has the lunch lady role.", required = false, nullable = true)
	private Boolean isLunchLady;
	// Password IS handled by IN DTO
	@Schema(description = "The user password.", required = true, nullable = false)
	private String password;
	@Schema(description = "The name of the user.", required = false, nullable = true, example = "Albert")
	private String name;
	@Schema(description = "The first name of the user.", required = false, nullable = true, example = "Smith")
	private String firstname;
	@Schema(description = "The phone number of the user.", required = false, nullable = true, example = "0147503190")
	private String phone;
	@Schema(description = "The town of the user.", required = false, nullable = true, example = "Versailles")
	private String town;
	@Schema(description = "The sex of the user. 0 for man, 1 for woman, 2 for other", required = false, nullable = true, example = "0", defaultValue = "0", minimum = "0", maximum = "2", type = "number", allowableValues = {
			"0", "1", "2" })
	private Byte sex;

	@Schema(description = "The image.", required = false, nullable = true)
	private ImageDtoIn image;

	// Status is not handled by IN DTO
	// registrationDate is not handled by IN DTO

	/**
	 * Constructor of the object.
	 */
	public UserDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pUserEntity a user entity
	 */
	public UserDtoIn(UserEntity pUserEntity) {
		super();
		this.setAddress(pUserEntity.getAddress());
		this.setWallet(pUserEntity.getWallet());
		this.setPostalCode(pUserEntity.getPostalCode());
		this.setEmail(pUserEntity.getEmail());
		this.setIsLunchLady(pUserEntity.getIsLunchLady());
		this.setImage(new ImageDtoIn(pUserEntity.getImage()));
		this.setName(pUserEntity.getName());
		this.setFirstname(pUserEntity.getFirstname());
		this.setPhone(pUserEntity.getPhone());
		this.setTown(pUserEntity.getTown());
		this.setSex(pUserEntity.getSex().getValue());
		this.setPassword(pUserEntity.getPassword());
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the sex value.
	 */
	public Byte getSex() {
		return this.sex;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pSex the new value for sex attribute
	 */
	public void setSex(Byte pSex) {
		if (pSex == null) {
			this.sex = Sex.MAN.getValue();
		} else {
			this.sex = pSex;
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the address value.
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pValue the new value for address attribute
	 */
	public void setAddress(String pValue) {
		this.address = super.checkAndClean(pValue);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the wallet value.
	 */
	public BigDecimal getWallet() {
		return this.wallet;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pWallet the new value for wallet attribute
	 */
	public void setWallet(BigDecimal pWallet) {
		if (pWallet == null || pWallet.doubleValue() < 0D) {
			this.wallet = BigDecimal.valueOf(0D);
		} else {
			this.wallet = pWallet;
		}
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the postalCode value.
	 */
	public String getPostalCode() {
		return this.postalCode;
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
	 * @return the isLunchLady value.
	 */
	public Boolean getIsLunchLady() {
		return this.isLunchLady != null ? this.isLunchLady : Boolean.FALSE;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pIsLunchLady the new value for isLunchLady attribute
	 */
	public void setIsLunchLady(Boolean pIsLunchLady) {
		if (pIsLunchLady == null) {
			this.isLunchLady = Boolean.FALSE;
		} else {
			this.isLunchLady = pIsLunchLady;
		}
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
	 * Gets the attribute value.
	 *
	 * @return the name value.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the first name value.
	 */
	public String getFirstname() {
		return this.firstname;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the phone value.
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the town value.
	 */
	public String getTown() {
		return this.town;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the image value.
	 */
	public ImageDtoIn getImage() {
		return this.image;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pPostalCode the new value for postalCode attribute
	 */
	public void setPostalCode(String pPostalCode) {
		this.postalCode = super.checkAndClean(pPostalCode);
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

	/**
	 * Sets the attribute value.
	 *
	 * @param pName the new value for name attribute
	 */
	public void setName(String pName) {
		this.name = super.checkAndClean(pName);
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pFirstName the new value for firstname attribute
	 */
	public void setFirstname(String pFirstName) {
		this.firstname = super.checkAndClean(pFirstName);
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pPhone the new value for phone attribute
	 */
	public void setPhone(String pPhone) {
		this.phone = super.checkAndClean(pPhone);
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pTown the new value for town attribute
	 */
	public void setTown(String pTown) {
		this.town = super.checkAndClean(pTown);
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pImage the new value for image attribute
	 */
	public void setImage(ImageDtoIn pImage) {
		this.image = pImage;
	}

	@Override
	@JsonIgnore
	public UserEntity toEntity() {
		UserEntity result = new UserEntity();
		result.setAddress(this.getAddress());
		result.setWallet(this.getWallet());
		result.setPostalCode(this.getPostalCode());
		result.setRegistrationDate(LocalDateTime.now());
		result.setEmail(this.getEmail());
		result.setIsLunchLady(this.getIsLunchLady());
		result.setPassword(this.getPassword());
		result.setName(this.getName());
		result.setFirstname(this.getFirstname());
		result.setPhone(this.getPhone());
		result.setTown(this.getTown());
		result.setSex(Sex.fromValue(this.getSex()));
		return result;
	}

	@Override
	@JsonIgnore
	public void validate() {
		if (this.getEmail() == null) {
			UserDtoIn.LOG.error("validate (User email is null)");
			throw new ParameterException("Email est null ou vide !", "email");
		}
		if (this.getPassword() == null) {
			UserDtoIn.LOG.error("validate (User password is null)");
			throw new ParameterException("Mot de passe est null ou vide !", "password");
		}

		if (this.getWallet() == null || this.getWallet().doubleValue() < 0) {
			UserDtoIn.LOG.warn("validate (User wallet will be 0)");
			this.setWallet(BigDecimal.valueOf(0D));
		}

		if (!Sex.inRange(this.getSex())) {
			UserDtoIn.LOG.error("validate (User sex should be {}, {} or {})", Sex.MAN.getValue(), Sex.WOMAN.getValue(),
					Sex.OTHER.getValue());
			throw new ParameterException("Sexe doit avoir la valeur " + Sex.MAN.getValue() + ", " + Sex.WOMAN.getValue()
					+ ", " + Sex.OTHER.getValue(), "sex");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append("name=");
		sb.append(this.name);
		sb.append(",firstname=");
		sb.append(this.firstname);
		sb.append(",address=");
		sb.append(this.address);
		if (this.getWallet() != null && this.getWallet().doubleValue() != 0) {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(0);
			df.setGroupingUsed(false);
			sb.append(",wallet=");
			sb.append(df.format(this.wallet));
		} else {
			sb.append(",wallet=0");
		}

		sb.append(",postalCode=");
		sb.append(this.postalCode);
		sb.append(",email=");
		sb.append(this.email);
		sb.append(",isLunchLady=");
		sb.append(this.isLunchLady);
		// DONT : append password in toString
		sb.append(",phone=");
		sb.append(this.phone);
		sb.append(",town=");
		sb.append(this.town);
		if (this.getImage() != null) {
			sb.append(",image=");
			sb.append(this.getImage());
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Indicates if user is man
	 *
	 * @return true if user is a man
	 */
	@JsonIgnore
	public boolean isMan() {
		return Sex.MAN.getPrimitiveValue() == this.getSex().byteValue();
	}

	/**
	 * Indicates if user is woman
	 *
	 * @return true if user is a woman
	 */
	@JsonIgnore
	public boolean isWoman() {
		return Sex.WOMAN.getPrimitiveValue() == this.getSex().byteValue();
	}

	/**
	 * Indicates if user is not a man nor a woman
	 *
	 * @return true if user is not a man nor a woman
	 */
	@JsonIgnore
	public boolean isOther() {
		return Sex.OTHER.getPrimitiveValue() == this.getSex().byteValue();
	}

}
