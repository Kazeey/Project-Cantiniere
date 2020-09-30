// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mother of simple entity class.
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
	private static final Logger LOG = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	/**
	 * Constructor of the object. <br>
	 */
	public AbstractEntity() {
		super();
	}

	/**
	 * Constructor of the object.<br>
	 *
	 * @param pId a value for PK
	 */
	public AbstractEntity(Integer pId) {
		super();
		this.setId(pId);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the id value.
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the attribute value.
	 *
	 * @param pId the new value for id attribute
	 */
	public void setId(Integer pId) {
		this.id = pId;
	}

	@Override
	public int hashCode() {
		if (this.getId() != null) {
			return (this.getClass().getName() + "-" + this.getId()).hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractEntity && this.getClass() == obj.getClass()) {
			return ((AbstractEntity) obj).getId() == this.getId()
					|| ((AbstractEntity) obj).getId().equals(this.getId());
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(this.getClass().getSimpleName());
		sb.append(",id=");
		sb.append(this.getId());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Converts a String like "1, 2, 45" into a set of integer.
	 *
	 * @param pValue the string respecting format 1,2,52
	 * @return a set of integer, null if none
	 */
	protected Set<Integer> stringToSet(String pValue) {
		if (pValue == null) {
			return null;
		}
		// Just in case ...
		pValue = pValue.replace('[', ' ');
		pValue = pValue.replace(']', ' ');
		pValue = pValue.replace('}', ' ');
		pValue = pValue.replace('}', ' ');
		pValue = pValue.replace(';', ',');
		pValue = pValue.replace('S', ' ');
		pValue = pValue.replace('s', ' ');
		pValue = pValue.trim();
		if (pValue.isEmpty()) {
			return Collections.emptySet();
		}
		Set<Integer> result = new HashSet<>();
		try {
			String[] dec = pValue.split(",");
			for (String elm : dec) {
				result.add(Integer.valueOf(elm.trim()));
			}
		} catch (Exception lExp) {
			AbstractEntity.LOG.error("stringToSet - Error with values {} and object {}", pValue,
					this.getClass().getSimpleName(), lExp);
		}
		return result;
	}

	/**
	 * Checks if value is empty or null.
	 *
	 * @param pValue a value
	 * @return this value trimmed or null
	 */
	protected final String checkAndClean(String pValue) {
		if (pValue == null || pValue.trim().isEmpty()) {
			return null;
		}
		return pValue.trim();
	}

}
