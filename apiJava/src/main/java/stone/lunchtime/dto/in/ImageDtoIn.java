// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.ImageEntity;

/**
 * The dto class for the image database table.
 *
 * The default notion cannot be handled by DTO In. It is more the system that does this.
 */
@Schema(description = "Image element (pictures for user, meal, ingredient, menu)")
public class ImageDtoIn extends AbstractDtoIn<ImageEntity> {
	private static final long serialVersionUID = 1L;

	@Schema(description = "The image path.", required = false, nullable = true, example = "img/toto.png")
	private String imagePath;

	@Schema(description = "The image encoded in base 64.", required = false, nullable = true, example = "see https://www.base64-image.de/")
	private String image64;

	/**
	 * Constructor of the object.
	 */
	public ImageDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.
	 *
	 * @param pEntity an entity
	 */
	public ImageDtoIn(ImageEntity pEntity) {
		super();
		if (pEntity != null) {
			this.setImage64(pEntity.getImage64());
			this.setImagePath(pEntity.getImagePath());
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
		this.imagePath = this.checkAndClean(pImagePath);
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
		this.image64 = this.checkAndClean(pImage64);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append("path=");
		if (this.getImagePath() != null) {
			sb.append(this.getImagePath().substring(0, Math.min(20, this.getImagePath().length())));
		} else {
			sb.append("null");
		}
		sb.append(",base64=");
		if (this.getImage64() != null) {
			sb.append(this.getImage64().substring(0, Math.min(10, this.getImage64().length())));
		} else {
			sb.append("null");
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public ImageEntity toEntity() {
		ImageEntity result = new ImageEntity();
		result.setImage64(this.getImage64());
		result.setImagePath(this.getImagePath());
		return result;
	}

	@Override
	public void validate() {
		// No validation for this been
	}
}
