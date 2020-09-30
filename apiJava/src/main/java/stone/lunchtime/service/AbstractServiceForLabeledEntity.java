// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.ILabeledDao;
import stone.lunchtime.dto.in.AbstractLabeledDtoIn;
import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.entity.AbstractLabeledEntity;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Mother class for all services that handle labeled entity.
 *
 * @param <T> the targeted entity
 */
@Service
abstract class AbstractServiceForLabeledEntity<T extends AbstractLabeledEntity> extends AbstractServiceForEntity<T> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private ImageService imageService;

	/**
	 * Will begin the update process. <br>
	 *
	 * This method does not call save method.
	 *
	 * @param pIdToUpdate an entity id. The one that needs update.
	 * @param pNewDto     the new values for this entity
	 * @return the partially updated entity
	 * @throws EntityNotFoundException if entity not found
	 * @throws ParameterException      if parameter is invalid
	 */
	protected T beginUpdate(Integer pIdToUpdate, AbstractLabeledDtoIn<T> pNewDto) throws EntityNotFoundException {
		AbstractServiceForLabeledEntity.LOG.debug("beginUpdate - {} with {} in {}", pIdToUpdate, pNewDto,
				this.getClass().getSimpleName());
		if (pNewDto == null) {
			AbstractServiceForLabeledEntity.LOG.error("beginUpdate - DTO is null");
			throw new ParameterException("Pas de nouvelle données !", "pNewDto");
		}
		if (pIdToUpdate == null) {
			AbstractServiceForLabeledEntity.LOG.error("beginUpdate - Id is null");
			throw new ParameterException("Entite n'a pas id !", "pIdToUpdate");
		}

		pNewDto.validate();

		T entityInDateBase = super.getEntityFrom(pIdToUpdate);

		if (pNewDto.getDescription() != null) {
			if (!pNewDto.getDescription().equals(entityInDateBase.getDescription())) {
				AbstractServiceForLabeledEntity.LOG.debug("beginUpdate - Entity description has changed");
				entityInDateBase.setDescription(pNewDto.getDescription());
			}
		} else {
			entityInDateBase.setDescription(null);
		}

		if (!pNewDto.getLabel().equals(entityInDateBase.getLabel())) {
			AbstractServiceForLabeledEntity.LOG.debug("beginUpdate - Entity label has changed");
			entityInDateBase.setLabel(pNewDto.getLabel());
		}
		return entityInDateBase;
	}

	protected void handleImage(T pEntity, AbstractLabeledDtoIn<T> pNewDto) {
		ImageDtoIn imgDto = pNewDto.getImage();
		ImageEntity imgE = this.getDefault();
		if (imgDto != null) {
			AbstractServiceForLabeledEntity.LOG.debug("insertAndLinkImage - element has an image, will insert it");
			imgE = imgDto.toEntity();
			imgE = this.getImageService().saveIfNotInDataBase(imgE);
			AbstractServiceForLabeledEntity.LOG.debug("insertAndLinkImage - elements's image was inserted with id {}",
					imgE.getId());

		}
		pEntity.setImage(imgE);
	}

	/**
	 * Will change entity status. <br>
	 *
	 * @param pEntityId  an entity id
	 * @param pNewStatus the new entity status
	 * @return the updated entity
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	private T updateStatus(Integer pEntityId, EntityStatus pNewStatus)
			throws InconsistentStatusException, EntityNotFoundException {
		AbstractServiceForLabeledEntity.LOG.debug("updateStatus - {} for new state {} in service {}", pEntityId,
				pNewStatus, this.getClass().getSimpleName());
		if (pEntityId == null) {
			AbstractServiceForLabeledEntity.LOG.error("updateStatus - id is null");
			throw new ParameterException("Entite n'a pas d'id !", "pEntityId");
		}
		if (pNewStatus == null) {
			AbstractServiceForLabeledEntity.LOG.error("updateStatus - newState is null");
			throw new ParameterException("Etat est null", "pNewStatus");
		}
		if (pNewStatus.equals(EntityStatus.DISABLED)) {
			AbstractServiceForLabeledEntity.LOG.error("updateStatus - newState cannot be disabled");
			throw new ParameterException("Il n'est pas possible de désactiver une entité");
		}

		T entity = super.getEntityFrom(pEntityId);
		if (pNewStatus.equals(entity.getStatus())) {
			throw new InconsistentStatusException(
					"Entite " + pEntityId + " est déjà dans l'état demandé " + pNewStatus);
		}

		if (entity.isDeleted()) {
			throw new InconsistentStatusException(
					"Entite " + pEntityId + " est supprimée, elle ne peut pas changer de status");
		}

		entity.setStatus(pNewStatus);
		T resultUpdate = this.getTargetedDao().save(entity);
		AbstractServiceForLabeledEntity.LOG.info("updateStatus - OK");
		return resultUpdate;
	}

	/**
	 * Deletes the entity. <br>
	 *
	 * Data are not removed from data base, only entity's status will change.
	 *
	 * @param pId an entity id
	 * @return the entity deleted
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public T delete(Integer pId) throws EntityNotFoundException, InconsistentStatusException {
		AbstractServiceForLabeledEntity.LOG.debug("delete - {} for service {}", pId, this.getClass().getSimpleName());
		return this.updateStatus(pId, EntityStatus.DELETED);
	}

	/**
	 * Finds all entities that are not deleted.
	 *
	 * @return the entities found, an empty list if none
	 * @throws ParameterException if parameter is invalid
	 */
	@Override
	@Transactional(readOnly = true)
	public List<T> findAll() {
		AbstractServiceForLabeledEntity.LOG.debug("findAll (NotDeleted) - for service {}",
				this.getClass().getSimpleName());
		Optional<List<T>> opResult = ((ILabeledDao<T>) this.getTargetedDao()).findAllNotDeleted();
		List<T> result = Collections.emptyList();
		if (opResult.isPresent()) {
			result = opResult.get();
		}
		AbstractServiceForLabeledEntity.LOG.info("findAll (NotDeleted) - Found {} values for service {}", result.size(),
				this.getClass().getSimpleName());
		return result;
	}

	/**
	 * Gets the default image for this element from the data base.
	 * @return the default image for this element from the data base.
	 */
	protected abstract ImageEntity getDefault();

	/**
	 * Gets the image service.
	 * @return the image service
	 */
	protected ImageService getImageService() {
		return this.imageService;
	}

	/**
	 * Changes the image of this element. <br>
	 *
	 * @param pUserId        a user id
	 * @param pNewImage the new image
	 * @return the user updated
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	public T updateImage(Integer pElmId, ImageDtoIn pNewImageDto)
			throws EntityNotFoundException, InconsistentStatusException {
		AbstractServiceForLabeledEntity.LOG.debug("updateImage - {} for new image {}", pElmId, pNewImageDto);
		if (pElmId == null) {
			AbstractServiceForLabeledEntity.LOG.error("updateImage - id is null");
			throw new ParameterException("Element n'a pas d'id !", "pElmId");
		}
		if (pNewImageDto == null) {
			AbstractServiceForLabeledEntity.LOG.error("updateImage - pNewImageDto is null");
			throw new ParameterException("Image est null", "pNewImageDto");
		}
		T elm = this.getEntityFrom(pElmId);
		if (elm.isDeleted()) {
			throw new InconsistentStatusException("Impossible de changer l'image d'un element supprimé");
		}
		ImageEntity oldImg = elm.getImage();
		if (oldImg != null) {
			if (Boolean.TRUE.equals(oldImg.getIsDefault())) {
				oldImg = pNewImageDto.toEntity();
			} else {
				oldImg.setImage64(pNewImageDto.getImage64());
				oldImg.setImagePath(pNewImageDto.getImagePath());
			}
			oldImg = this.imageService.saveIfNotInDataBase(oldImg);
			elm.setImage(oldImg);
		}

		T resultUpdate = this.getTargetedDao().save(elm);
		AbstractServiceForLabeledEntity.LOG.info("updateImage - OK");
		return resultUpdate;
	}

}
