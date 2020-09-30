// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.ImageEntity;

/**
 * The dto class for the constraint database table.
 */
@Schema(description = "Image element (pictures for user, meal, ingredient, menu)")
public class ImageDtoOut extends AbstractDtoOut {
	private static final long serialVersionUID = 1L;

	@Schema(description = "The image path.", nullable = true, example = "img/toto.png")
	private String imagePath;

	@Schema(description = "The image encoded in base 64.", nullable = true, example = "see https://www.base64-image.de/")
	private String image64;

	@Schema(description = "Indicates if this is a default image or not", nullable = true)
	private boolean isDefault;

	/**
	 * Constructor of the object.
	 */
	public ImageDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pId an id
	 */
	public ImageDtoOut(Integer pId) {
		super(pId);
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity an entity
	 */
	public ImageDtoOut(ImageEntity pEntity) {
		super(pEntity);
		if (pEntity != null) {
			this.setImage64(pEntity.getImage64());
			this.setImagePath(pEntity.getImagePath());
			this.setDefault(pEntity.getIsDefault().booleanValue());
		}
	}

	/**
	 * Gets the attribute isDefault.
	 *
	 * @return the value of isDefault.
	 */
	public boolean isDefault() {
		return this.isDefault;
	}

	/**
	 * Sets a new value for the attribute isDefault.
	 *
	 * @param pIsDefault the new value for the attribute.
	 */
	public void setDefault(boolean pIsDefault) {
		this.isDefault = pIsDefault;
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
		this.imagePath = pImagePath;
	}

	/**
	 * Gets the attribute image64.
	 *
	 * @return the value of image64.
	 */
	public String getImage64() {
		return this.image64;
	}

	/**
	 * Sets a new value for the attribute image64.
	 *
	 * @param pImage64 the new value for the attribute.
	 */
	public void setImage64(String pImage64) {
		this.image64 = pImage64;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",path=");
		if (this.getImagePath() != null) {
			sb.append(this.getImagePath().substring(0, Math.min(20, this.getImagePath().length())));
		} else {
			sb.append("null");
		}
		sb.append(",64=");
		if (this.getImage64() != null) {
			sb.append(this.getImage64().substring(0, Math.min(10, this.getImage64().length())));
		} else {
			sb.append("null");
		}
		sb.append(",default=").append(this.isDefault());
		sb.append("}");
		return sb.toString();
	}
}
