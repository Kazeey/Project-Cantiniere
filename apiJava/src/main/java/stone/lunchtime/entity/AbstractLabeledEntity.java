// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the labeled table.
 */
@MappedSuperclass
public abstract class AbstractLabeledEntity extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "description", columnDefinition = "TEXT")
	@Lob
	@Type(type = "org.hibernate.type.TextType") // For Postgres
	private String description;

	@Column(name = "label", length = 200, nullable = false)
	private String label;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private EntityStatus status;

	// Handling of image is not done by this entity
	@LazyCollection(LazyCollectionOption.TRUE)
	@ManyToOne(optional = true)
	@JoinColumn(name = "image_id", nullable = true)
	private ImageEntity image;

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

	/**
	 * Gets the attribute value.
	 *
	 * @return the status value.
	 */
	public EntityStatus getStatus() {
		return this.status != null ? this.status : EntityStatus.DISABLED;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pStatus the new value for status attribute
	 */
	public void setStatus(EntityStatus pStatus) {
		if (pStatus == null) {
			this.status = EntityStatus.DISABLED;
		} else {
			this.status = pStatus;
		}
	}

	/**
	 * Gets the attribute image.
	 *
	 * @return the value of image.
	 */
	public ImageEntity getImage() {
		return this.image;
	}

	/**
	 * Sets a new value for the attribute image.
	 *
	 * @param pImage the new value for the attribute.
	 */
	public void setImage(ImageEntity pImage) {
		this.image = pImage;
	}

	/**
	 * Sets a new value for the attribute image.
	 *
	 * If both values are null then image is null.
	 *
	 * @param pImage64 the image in base 64
	 * @param pImagePath the image path
	 */
	public void setImage(String pImage64, String pImagePath) {
		if (pImage64 == null && pImagePath == null) {
			this.setImage(null);
		} else {
			ImageEntity ie = new ImageEntity();
			ie.setImage64(pImage64);
			ie.setImagePath(pImagePath);
			this.setImage(ie);
		}
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
		sb.append(",label=");
		sb.append(this.getLabel());
		if (this.getImage() != null) {
			sb.append(",image=");
			sb.append(this.getImage());
		}

		sb.append("}");
		return sb.toString();
	}

	/**
	 * Indicates if entity has the status EntityStatus.ENABLED
	 *
	 * @return true if entity has status EntityStatus.ENABLED
	 */
	public boolean isEnabled() {
		return EntityStatus.ENABLED.equals(this.getStatus());
	}

	/**
	 * Indicates if entity has the status EntityStatus.DELETED
	 *
	 * @return true if entity has status EntityStatus.DELETED
	 */
	public boolean isDeleted() {
		return EntityStatus.DELETED.equals(this.getStatus());
	}
}
