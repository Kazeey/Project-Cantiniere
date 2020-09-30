// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.entity;

import java.util.Base64;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the ltimage database table.
 */
@Entity
@Table(name = "ltimage")
public class ImageEntity extends AbstractEntity {
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Column(name = "image_path", length = 500)
	private String imagePath;

	@Column(name = "image_64", columnDefinition = "TEXT")
	@Lob
	@Type(type = "org.hibernate.type.TextType") // Pour Postgres
	private String image64;

	@Column(name = "image_bin")
	@Lob
	@Type(type = "org.hibernate.type.BinaryType") // Pour Postgres
	private byte[] imageBin;

	@Column(name = "is_default", nullable = false)
	private Boolean isDefault = Boolean.FALSE;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(mappedBy = "image")
	private List<IngredientEntity> ingredients;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(mappedBy = "image")
	private List<MealEntity> meals;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(mappedBy = "image")
	private List<MenuEntity> menus;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(mappedBy = "image")
	private List<UserEntity> users;

	public ImageEntity() {
		super();
	}

	public ImageEntity(Integer pId) {
		super(pId);
	}

	public ImageEntity(String pImagePath, String pImage64, Boolean pIsDefault) {
		super();
		this.setImagePath(pImagePath);
		this.setImage64(pImage64);
		this.setIsDefault(pIsDefault);
	}

	/**
	 * Gets the attribute isDefault.
	 *
	 * @return the value of isDefault.
	 */
	public Boolean getIsDefault() {
		return this.isDefault;
	}

	/**
	 * Sets a new value for the attribute isDefault.
	 *
	 * @param pIsDefault the new value for the attribute.
	 */
	public void setIsDefault(Boolean pIsDefault) {
		if (pIsDefault == null) {
			this.isDefault = Boolean.FALSE;
		} else {
			this.isDefault = pIsDefault;
		}
	}

	/**
	 * Gets the attribute imagePath.
	 *
	 * @return the value of imagePath.
	 */
	public String getImagePath() {
		return this.imagePath;
	}

	/**
	 * Sets a new value for the attribute imagePath.
	 *
	 * @param pImagePath the new value for the attribute.
	 */
	public void setImagePath(String pImagePath) {
		this.imagePath = super.checkAndClean(pImagePath);
	}

	/**
	 * Gets the attribute imageBin.
	 *
	 * @return the value of imageBin.
	 */
	public byte[] getImageBin() {
		return this.imageBin;
	}

	/**
	 * Sets a new value for the attribute imageBin.
	 *
	 * @param pImageBin the new value for the attribute.
	 */
	public void setImageBin(byte[] pImageBin) {
		this.imageBin = pImageBin;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the image value.
	 */
	public String getImage64() {
		return this.image64;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pImage the new value for image attribute
	 */
	public void setImage64(String pImage) {
		this.image64 = super.checkAndClean(pImage);
		if (this.image64 != null) {
			try {
				byte[] decodedByte = null;
				// We need to remove 'data:image/png;base64,' from the string
				if (this.image64.startsWith("data:")) {
					int idV = this.image64.indexOf(',') + 1;
					decodedByte = Base64.getDecoder().decode(this.image64.substring(idV));
				} else {
					decodedByte = Base64.getDecoder().decode(this.image64);
				}
				this.setImageBin(decodedByte);
			} catch (Exception lExp) {
				ImageEntity.LOG.error("Error while transforming base 64 image to byte[]", lExp);
				this.setImageBin(null);
			}
		} else {
			this.setImageBin(null);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		if (this.getImagePath() != null) {
			sb.append(",path=");
			sb.append(this.getImagePath().substring(0, Math.min(20, this.getImagePath().length())));
			sb.append("...");
		}
		if (this.getImage64() != null) {
			sb.append(",base64=");
			sb.append(this.getImage64().substring(0, Math.min(10, this.getImage64().length())));
			sb.append("...");
		}
		sb.append(",default=");
		sb.append(this.getIsDefault());
		if (this.getImageBin() != null) {
			sb.append(",blob=YES[").append(this.getImageBin().length).append(']');
		} else {
			sb.append(",blob=NO");
		}
		sb.append("}");
		return sb.toString();
	}

}
