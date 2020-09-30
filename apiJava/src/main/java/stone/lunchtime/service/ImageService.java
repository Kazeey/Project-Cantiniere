// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IImageDao;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * Images service.
 */
@Service
public class ImageService extends AbstractServiceForEntity<ImageEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IImageDao imageDao;

	@Transactional(rollbackFor = Exception.class)
	public ImageEntity saveIfNotInDataBase(ImageEntity pEntity) {
		ImageService.LOG.debug("saveIfNotInDataBase - {}", pEntity);
		if (pEntity == null) {
			ImageService.LOG.error("saveIfNotInDataBase - null?");
			throw new ParameterException("Image est null !", "pEntity");
		}
		if (pEntity.getIsDefault().booleanValue()) {
			Optional<ImageEntity> img = this.imageDao.findOneByImagePath(pEntity.getImagePath());
			if (img.isPresent()) {
				ImageService.LOG.warn("saveIfNotInDataBase - image with path {} already in DB", pEntity.getImagePath());
				return img.get();
			}
			ImageService.LOG.debug("saveIfNotInDataBase - image with path {} not in DB, will save it",
					pEntity.getImagePath());
		}
		if (pEntity.getId() != null) {
			ImageService.LOG.debug("saveIfNotInDataBase - image has an id {}, will save it again if path changed",
					pEntity.getId());
		}

		ImageEntity resultSave = this.imageDao.save(pEntity);
		ImageService.LOG.info("saveIfNotInDataBase - OK with id={}", resultSave.getId());
		return resultSave;
	}

	@Override
	protected CrudRepository<ImageEntity, Integer> getTargetedDao() {
		return this.imageDao;
	}

	/**
	 * Deletes the constraint. <br>
	 *
	 * Caution: Data are completely removed from data base.
	 *
	 * @param pId a constraint id
	 * @throws EntityNotFoundException if entity not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer pId) throws EntityNotFoundException {
		ImageService.LOG.debug("delete - {}", pId);
		ImageEntity entity = super.getEntityFrom(pId);
		this.imageDao.delete(entity);
	}
}
