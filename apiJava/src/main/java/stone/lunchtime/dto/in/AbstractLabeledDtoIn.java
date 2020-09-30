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
import stone.lunchtime.entity.AbstractLabeledEntity;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Dto with label, description, image.
 *
 * Package visibility
 *
 * @param <T> The targeted entity
 */
@Schema(description = "Represents a labeled element.", subTypes = { IngredientDtoIn.class, AbstractEatableDtoIn.class })
public abstract class AbstractLabeledDtoIn<T extends AbstractLabeledEntity> extends AbstractDtoIn<T> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	@Schema(description = "A description of this element.", required = false, nullable = true)
	private String description;
	@Schema(description = "The visible label for this element.", required = true, nullable = false)
	private String label;

	@Schema(description = "The image.", required = false, nullable = true)
	private ImageDtoIn image;

	// status is not handled by DTO

	/**
	 * Constructor of the object. <br>
	 */
	public AbstractLabeledDtoIn() {
		super();
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pEntity an entity
	 */
	public AbstractLabeledDtoIn(T pEntity) {
		super();
		this.setDescription(pEntity.getDescription());
		this.setLabel(pEntity.getLabel());
		if (pEntity.getImage() != null) {
			this.setImage(new ImageDtoIn(pEntity.getImage()));
		}
	}

	/**
	 * Gets the attribute image.
	 *
	 * @return the value of image.
	 */
	public ImageDtoIn getImage() {
		return this.image;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pImage the new value for image attribute
	 */
	public void setImage(ImageDtoIn pImage) {
		this.image = pImage;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the description value.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pDescription the new value for description attribute
	 */
	public void setDescription(String pDescription) {
		this.description = super.checkAndClean(pDescription);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the label value.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pLabel the new value for label attribute
	 */
	public void setLabel(String pLabel) {
		this.label = super.checkAndClean(pLabel);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append("label=");
		sb.append(this.getLabel());
		if (this.getDescription() != null) {
			sb.append(",description=");
			sb.append(this.getDescription());
		}
		if (this.getImage() != null) {
			sb.append(",image=");
			sb.append(this.getImage());
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Fills the entity with the current DTO.
	 * No image in this entity.
	 *
	 * @param result the entity to fill
	 * @return the entity filled with the dto
	 */
	@JsonIgnore
	protected T toEntity(T result) {
		result.setDescription(this.getDescription());
		result.setLabel(this.getLabel());
		return result;
	}

	@Override
	@JsonIgnore
	public void validate() {
		if (this.getLabel() == null) {
			AbstractLabeledDtoIn.LOG.error("validate - Label must be set");
			throw new ParameterException("Libelle ne doit pas être null", "label");
		}
	}
}
