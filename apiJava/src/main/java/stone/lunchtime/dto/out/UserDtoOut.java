// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.dto.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.Sex;
import stone.lunchtime.entity.UserEntity;

/**
 * The DTO out class for the User.
 */
@Schema(description = "Represents a user of the application. No password is present in this DTO.")
public class UserDtoOut extends AbstractDtoOut {
	@JsonIgnore
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Schema(description = "Adress of the user.", example = "3 road of iron")
	private String address;
	@Schema(description = "Amount of money owned by the user.", example = "35.5")
	private BigDecimal wallet;
	@Schema(description = "Postal code of the user.", example = "78140")
	private String postalCode;
	@Schema(description = "Date of creation.")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime registrationDate;
	@Schema(description = "Email of the user.", example = "toto@aol.com")
	private String email;
	@Schema(description = "Indicates if the user has the lunch lady role.")
	private Boolean isLunchLady;
	// NO password for DTO-OUT
	@Schema(description = "The name of the user.", example = "Albert")
	private String name;
	@Schema(description = "The first name of the user.", example = "Smith")
	private String firstname;
	@Schema(description = "The phone number of the user.", example = "0147503190")
	private String phone;
	@Schema(description = "The town of the user.", example = "Versailles")
	private String town;
	@Schema(description = "The sex of the user. 0 for man, 1 for woman, 2 for other", example = "0", type = "number", allowableValues = {
			"0", "1", "2" })
	private Byte sex;
	@Schema(description = "The status for the user. 0 for Enabled, 1 for Disabled, 2 for Deleted", example = "0", type = "number", allowableValues = {
			"0", "1", "2" })
	private Byte status;

	// We do this in order to limit the size of this DTO.
	// Otherwise image would be send always, this will cause an heavy load of data
	@Schema(description = "The image id.", nullable = true)
	private Integer imageId;

	/**
	 * Constructor of the object.
	 */
	public UserDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity where to take information
	 */
	public UserDtoOut(UserEntity pEntity) {
		super(pEntity);
		this.setAddress(pEntity.getAddress());
		if (pEntity.getWallet() != null) {
			this.setWallet(pEntity.getWallet());
		} else {
			this.setWallet(BigDecimal.valueOf(0D));
		}
		this.setPostalCode(pEntity.getPostalCode());
		this.setRegistrationDate(pEntity.getRegistrationDate());
		this.setEmail(pEntity.getEmail());
		this.setIsLunchLady(pEntity.getIsLunchLady());
		if (pEntity.getImage() != null) {
			this.setImageId(pEntity.getImage().getId());
		}
		this.setName(pEntity.getName());
		this.setFirstname(pEntity.getFirstname());
		this.setPhone(pEntity.getPhone());
		this.setTown(pEntity.getTown());
		this.setSex(pEntity.getSex().getValue());
		this.setStatus(pEntity.getStatus().getValue());
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pMap where to take information
	 */
	public UserDtoOut(Map<String, ?> pMap) {
		super();
		if (pMap.get("id") != null) {
			this.setId((Integer) pMap.get("id"));
		}
		this.setAddress((String) pMap.get("address"));
		if (pMap.get("wallet") != null) {
			this.setWallet(BigDecimal.valueOf(((Number) pMap.get("wallet")).doubleValue()));
		} else {
			this.setWallet(BigDecimal.valueOf(0D));
		}
		this.setPostalCode((String) pMap.get("postalCode"));
		this.setEmail((String) pMap.get("email"));
		this.setImageId((Integer) pMap.get("imageId"));
		this.setName((String) pMap.get("name"));
		this.setFirstname((String) pMap.get("firstname"));
		this.setPhone((String) pMap.get("phone"));
		this.setTown((String) pMap.get("town"));

		this.setIsLunchLady(Boolean.FALSE);
		if (pMap.get("isLunchLady") != null) {
			this.setIsLunchLady((Boolean) pMap.get("isLunchLady"));
		}

		this.setSex(Sex.OTHER.getValue());
		if (pMap.get("sex") != null) {
			this.setSex(Byte.valueOf(((Number) pMap.get("sex")).byteValue()));
		}

		if (pMap.get("registrationDate") != null) {
			UserDtoOut.LOG.trace("registrationDate is stored as {}", pMap.get("registrationDate").getClass().getName());
			@SuppressWarnings("unchecked")
			List<Integer> o = (List<Integer>) pMap.get("registrationDate");
			// [2019, 3, 2, 15, 17, 28]
			this.setRegistrationDate(
					LocalDateTime.of(o.get(0), Month.of(o.get(1)), o.get(2), o.get(3), o.get(4), o.get(5), 0));
		}

		this.setStatus(EntityStatus.DISABLED.getValue());
		if (pMap.get("status") != null) {
			this.setStatus(Byte.valueOf(((Number) pMap.get("status")).byteValue()));
		}
	}

	/**
	 * Gets the attribute imageId.
	 *
	 * @return the value of imageId.
	 */
	public Integer getImageId() {
		return this.imageId;
	}

	/**
	 * Sets a new value for the attribute imageId.
	 *
	 * @param pImageId the new value for the attribute.
	 */
	public void setImageId(Integer pImageId) {
		this.imageId = pImageId;
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
	 * @param pAddress the new value for address attribute
	 */
	public void setAddress(String pAddress) {
		this.address = pAddress;
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
		this.wallet = pWallet;
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
	 * Sets the attribute value.
	 *
	 * @param pPostalCode the new value for postalCode attribute
	 */
	public void setPostalCode(String pPostalCode) {
		this.postalCode = pPostalCode;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the registrationDate value.
	 */
	public LocalDateTime getRegistrationDate() {
		return this.registrationDate;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pRegistrationDate the new value for registrationDate attribute
	 */
	public void setRegistrationDate(LocalDateTime pRegistrationDate) {
		if (pRegistrationDate != null) {
			// Some data base handle nano s
			pRegistrationDate = pRegistrationDate.withNano(0);
		}
		this.registrationDate = pRegistrationDate;
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
	 * Sets the attribute value.
	 *
	 * @param pEmail the new value for email attribute
	 */
	public void setEmail(String pEmail) {
		this.email = pEmail;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the isLunchLady value.
	 */
	public Boolean getIsLunchLady() {
		return this.isLunchLady;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pIsLunchLady the new value for isLunchLady attribute
	 */
	public void setIsLunchLady(Boolean pIsLunchLady) {
		this.isLunchLady = pIsLunchLady;
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
	 * Sets the attribute value.
	 *
	 * @param pName the new value for name attribute
	 */
	public void setName(String pName) {
		this.name = pName;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the firstname value.
	 */
	public String getFirstname() {
		return this.firstname;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pFirstname the new value for firstname attribute
	 */
	public void setFirstname(String pFirstname) {
		this.firstname = pFirstname;
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
	 * Sets the attribute value.
	 *
	 * @param pPhone the new value for phone attribute
	 */
	public void setPhone(String pPhone) {
		this.phone = pPhone;
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
	 * Sets the attribute value.
	 *
	 * @param pTown the new value for town attribute
	 */
	public void setTown(String pTown) {
		this.town = pTown;
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
		this.sex = pSex;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the status value.
	 */
	public Byte getStatus() {
		return this.status;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pStatus the new value for status attribute
	 */
	public void setStatus(Byte pStatus) {
		this.status = pStatus;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",address=");
		sb.append(this.address);
		sb.append(",wallet=");
		sb.append(AbstractDtoOut.formatNumber(this.wallet));
		sb.append(",postalCode=");
		sb.append(this.postalCode);
		sb.append(",email=");
		sb.append(this.email);
		sb.append(",isLunchLady=");
		sb.append(this.isLunchLady);
		sb.append(",status=");
		sb.append(this.getStatus());
		sb.append(",name=");
		sb.append(this.name);
		sb.append(",firstname=");
		sb.append(this.firstname);
		sb.append(",phone=");
		sb.append(this.phone);
		sb.append(",town=");
		sb.append(this.town);
		if (this.getImageId() != null) {
			sb.append(",imageId=");
			sb.append(this.getImageId());
		}
		sb.append("}");
		return sb.toString();
	}

}
