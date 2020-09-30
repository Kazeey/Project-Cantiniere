// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import stone.lunchtime.entity.AbstractLabeledEntity;

/**
 * Dto with label, description, image id.
 */
@Schema(description = "Represents a labeled element.", subTypes = { IngredientDtoOut.class,
		AbstractEatableDtoOut.class })
public abstract class AbstractLabeledDtoOut extends AbstractDtoOut {
	private static final long serialVersionUID = 1L;

	@Schema(description = "A description of this element.")
	private String description;
	@Schema(description = "The visible label for this element.")
	private String label;
	@Schema(description = "The status for this element. 0 for Enabled, 1 for Disabled, 2 for Deleted", example = "0", type = "number", allowableValues = {
			"0", "1", "2" })
	private Byte status;

	// We do this in order to limit the size of this DTO.
	// Otherwise image would be send always, this will cause an heavy load of data
	@Schema(description = "The image id.")
	private Integer imageId;

	/**
	 * Constructor of the object. <br>
	 */
	public AbstractLabeledDtoOut() {
		super();
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pId a value for PK
	 */
	public AbstractLabeledDtoOut(Integer pId) {
		super(pId);
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pEntity an entity
	 */
	public AbstractLabeledDtoOut(AbstractLabeledEntity pEntity) {
		super(pEntity);
		this.setDescription(pEntity.getDescription());
		this.setLabel(pEntity.getLabel());
		if (pEntity.getImage() != null) {
			this.setImageId(pEntity.getImage().getId());
		}
		this.setStatus(pEntity.getStatus().getValue());
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
		this.description = pDescription;
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
		this.label = pLabel;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String parent = super.toString();
		parent = parent.substring(0, parent.length() - 1);
		sb.append(parent);
		sb.append(",status=");
		sb.append(this.getStatus());
		if (this.getDescription() != null) {
			sb.append(",description=");
			sb.append(this.getDescription());
		}
		if (this.getLabel() != null) {
			sb.append(",label=");
			sb.append(this.getLabel());
		}
		if (this.getImageId() != null) {
			sb.append(",imageId=");
			sb.append(this.getImageId());
		}

		sb.append("}");
		return sb.toString();
	}

}
