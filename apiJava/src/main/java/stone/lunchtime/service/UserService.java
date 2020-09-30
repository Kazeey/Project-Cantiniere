// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stone.lunchtime.dao.IRoleDao;
import stone.lunchtime.dao.IUserDao;
import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.UserDtoIn;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.ImageEntity;
import stone.lunchtime.entity.RoleLabel;
import stone.lunchtime.entity.RoleEntity;
import stone.lunchtime.entity.Sex;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.exception.EntityAlreadySavedException;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentRoleException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.LackOfMoneyException;
import stone.lunchtime.service.exception.ParameterException;

/**
 * User service.
 */
@Service
public class UserService extends AbstractServiceForEntity<UserEntity> {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IUserDao userDao;
	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private ImageService imageService;

	/**
	 * Will search a user that has the given email.
	 *
	 * @param pEmail an email (cannot be null)
	 * @return the user found, or throws an exception
	 * @throws EntityNotFoundException if entity was not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public UserEntity find(String pEmail) throws EntityNotFoundException {
		UserService.LOG.debug("find - {}", pEmail);
		if (pEmail == null) {
			UserService.LOG.error("find - null?");
			throw new ParameterException("Email est null !", "email");
		}
		if (pEmail.trim().isEmpty()) {
			UserService.LOG.error("find - ?");
			throw new ParameterException("Email est vide !", "email");
		}
		Optional<UserEntity> opResult = this.userDao.findOneByEmail(pEmail);
		if (opResult.isPresent()) {
			UserEntity result = opResult.get();
			UserService.LOG.info("find - OK found entity for email={}", pEmail);
			return result;
		}
		UserService.LOG.warn("find - KO No user found with email={}", pEmail);
		throw new EntityNotFoundException("Utilisateur introuvable", pEmail);
	}

	/**
	 * Registers a user. <br>
	 *
	 * The user can be a Lunch Lady only if there is no Lunch Lady in the data base.
	 * Otherwise this role can only be given through update and by a user who is a
	 * Lunch Lady himself. <br>
	 *
	 * @param pUser a new user
	 * @param pImage the user image (can be null)
	 * @return the user inserted
	 * @throws EntityAlreadySavedException if user is already in data base
	 * @throws InconsistentRoleException   if pUser has the role Lunch Lady and
	 *                                     should not.
	 * @throws ParameterException          if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity register(UserDtoIn pUser) throws EntityAlreadySavedException, InconsistentRoleException {
		UserService.LOG.debug("register - {}", pUser);
		if (pUser == null) {
			UserService.LOG.error("register - null?");
			throw new ParameterException("Utilisateur est null !", "pUser");
		}

		pUser.validate();

		if (this.exist(pUser.getEmail())) {
			UserService.LOG.error("register - User already in data base {}", pUser);
			throw new EntityAlreadySavedException(
					"Utilisateur avec email=[" + pUser.getEmail() + "] est deja dans la base de donnees.");
		}

		// Can ONLY register as a LunchLady if there is NONE in the data base
		if (Boolean.TRUE.equals(pUser.getIsLunchLady())) {
			if (this.roleDao.countLunchLady() == 0) {
				UserService.LOG
						.warn("register - User can register as a Lunch Lady, because there is none in the data base");
			} else {
				UserService.LOG.error(
						"register -User cannot register as a Lunch Lady, this role can only be given using update BY a Lunch Lady");
				throw new InconsistentRoleException(
						"Vous ne pouvez pas vous auto-proclamer avec le rôle cantinière. Ce rôle doit être indiqué lors d'une mise à jour par une cantinière.");
			}
		}
		UserEntity entityToInsert = pUser.toEntity();
		entityToInsert.setStatus(EntityStatus.ENABLED);
		if (entityToInsert.getRoles() == null || entityToInsert.getRoles().isEmpty()) {
			List<RoleEntity> roles = new ArrayList<>();
			roles.add(new RoleEntity(RoleLabel.ROLE_USER, entityToInsert));
			entityToInsert.setRoles(roles);
		}

		this.handleImage(entityToInsert, pUser);

		UserEntity resultSave = this.userDao.save(entityToInsert);
		UserService.LOG.info("register - OK with new id={}", resultSave.getId());
		return resultSave;
	}

	/**
	 * Indicates if user with given email is already in data base.
	 *
	 * @param pEmail an email.
	 * @return true if user with given email is in data base, false if not
	 * @throws ParameterException if parameter is invalid
	 */
	@Transactional(readOnly = true)
	public boolean exist(String pEmail) {
		if (pEmail == null) {
			UserService.LOG.error("find - null?");
			throw new ParameterException("Email est null !", "email");
		}
		Optional<UserEntity> foundUser = this.userDao.findOneByEmail(pEmail);
		return foundUser.isPresent();
	}

	/**
	 * Updates a user. <br>
	 *
	 * You cannot update status, wallet nor image with this method. <br>
	 * But you can become or lose the Lunch Lady status.
	 *
	 * @param pIdToUpdate         the id of the user to update
	 * @param pNewDto             some new info for the user.
	 * @param pCanHandleLunchLady if true then lunch lady role can be change,
	 *                            otherwise it will not be change.
	 * @return the updated user
	 * @throws EntityNotFoundException     if user with given id was not found
	 * @throws EntityAlreadySavedException if user changes its email and is already
	 *                                     in data base
	 * @throws InconsistentRoleException   if try to change the Lunch Lady role and
	 *                                     cannot
	 * @throws ParameterException          if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity update(Integer pIdToUpdate, UserDtoIn pNewDto, boolean pCanHandleLunchLady)
			throws EntityNotFoundException, EntityAlreadySavedException, InconsistentRoleException {
		UserService.LOG.debug("update - {} with {}", pIdToUpdate, pNewDto);
		if (pNewDto == null) {
			UserService.LOG.error("update - null?");
			throw new ParameterException("Utilisateur est null !", "pNewDto");
		}
		UserEntity entityInDataBase = super.getEntityFrom(pIdToUpdate);

		pNewDto.validate();

		if (pNewDto.getEmail() != null && !pNewDto.getEmail().equals(entityInDataBase.getEmail())) {
			UserService.LOG.debug("update - User email has changed");
			if (this.exist(pNewDto.getEmail())) {
				UserService.LOG.error("update - User already in data base {}) ", pNewDto);
				throw new EntityAlreadySavedException(
						"Utilisateur avec email=[" + pNewDto.getEmail() + "] est deja dans la base de donnees.");
			}
			entityInDataBase.setEmail(pNewDto.getEmail());
		} else {
			UserService.LOG.warn("update - bad or eq value for new User email will do nothing");
		}
		if (pNewDto.getPassword() != null && !pNewDto.getPassword().equals(entityInDataBase.getPassword())) {
			UserService.LOG.debug("update - User mdp has changed");
			entityInDataBase.setPassword(pNewDto.getPassword());
		} else {
			UserService.LOG.warn("update - bad or eq value for new User password will do nothing");
		}

		if (pNewDto.getWallet() != null && !pNewDto.getWallet().equals(entityInDataBase.getWallet())) {
			UserService.LOG.warn("update - User wallet has changed but will not be updated use credit/debit");
		}
		if (pCanHandleLunchLady && !pNewDto.getIsLunchLady().equals(entityInDataBase.getIsLunchLady())) {
			// If role is lost, check that there is still one lunch lady in the data base
			if (Boolean.TRUE.equals(entityInDataBase.getIsLunchLady()) && Boolean.FALSE.equals(pNewDto.getIsLunchLady())
					&& this.roleDao.countLunchLady() - 1 <= 0) {
				// Cannot remove the role
				UserService.LOG
						.error("update - User IsLunchLady has changed but this is the last one, so will not be change");
				throw new InconsistentRoleException(
						"Vous ne pouvez pas vous retirer le rôle de cantinière. Vous êtes la seule dans la base de données.");
			} else {
				UserService.LOG.debug("update - User IsLunchLady has changed");
				entityInDataBase.setIsLunchLady(pNewDto.getIsLunchLady());
			}
		}

		if (pNewDto.getAddress() != null) {
			if (!pNewDto.getAddress().equals(entityInDataBase.getAddress())) {
				UserService.LOG.debug("update - User Address has changed");
				entityInDataBase.setAddress(pNewDto.getAddress());
			}
		} else {
			entityInDataBase.setAddress(null);
		}
		if (pNewDto.getPostalCode() != null) {
			if (!pNewDto.getPostalCode().equals(entityInDataBase.getPostalCode())) {
				UserService.LOG.debug("update - User PostalCode has changed");
				entityInDataBase.setPostalCode(pNewDto.getPostalCode());
			}
		} else {
			entityInDataBase.setPostalCode(null);
		}

		if (pNewDto.getName() != null) {
			if (!pNewDto.getName().equals(entityInDataBase.getName())) {
				UserService.LOG.debug("update - User Name has changed");
				entityInDataBase.setName(pNewDto.getName());
			}
		} else {
			entityInDataBase.setName(null);
		}
		if (pNewDto.getFirstname() != null) {
			if (!pNewDto.getFirstname().equals(entityInDataBase.getFirstname())) {
				UserService.LOG.debug("update - User Firstname has changed");
				entityInDataBase.setFirstname(pNewDto.getFirstname());
			}
		} else {
			entityInDataBase.setFirstname(null);
		}
		if (pNewDto.getTown() != null) {
			if (!pNewDto.getTown().equals(entityInDataBase.getTown())) {
				UserService.LOG.debug("update - User Town has changed");
				entityInDataBase.setTown(pNewDto.getTown());
			}
		} else {
			entityInDataBase.setTown(null);
		}
		if (pNewDto.getPhone() != null) {
			if (!pNewDto.getPhone().equals(entityInDataBase.getPhone())) {
				UserService.LOG.debug("update - User Phone has changed");
				entityInDataBase.setPhone(pNewDto.getPhone());
			}
		} else {
			entityInDataBase.setPhone(null);
		}

		if (pNewDto.getSex() != null) {
			if (!pNewDto.getSex().equals(entityInDataBase.getSex().getValue())) {
				UserService.LOG.debug("update - User Sex has changed");
				entityInDataBase.setSex(Sex.fromValue(pNewDto.getSex()));
			}
		} else {
			UserService.LOG.warn("update - bad or eq value for new User sex will do nothing");
		}

		if (entityInDataBase.getRoles() == null || entityInDataBase.getRoles().isEmpty()) {
			List<RoleEntity> roles = new ArrayList<>();
			roles.add(new RoleEntity(RoleLabel.ROLE_USER, entityInDataBase));
			entityInDataBase.setRoles(roles);
		}

		// userInDataBase is updated with new values
		UserEntity resultUpdate = this.userDao.save(entityInDataBase);
		UserService.LOG.info("update - OK");
		return resultUpdate;
	}

	/**
	 * Will remove money from user's wallet.
	 *
	 * @param pUserId a user id
	 * @param pAmount an amount of money
	 * @return the user updated
	 * @throws EntityNotFoundException if entity was not found
	 * @throws LackOfMoneyException    if user has not enough money in its wallet
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity debit(Integer pUserId, BigDecimal pAmount) throws EntityNotFoundException, LackOfMoneyException {
		UserService.LOG.debug("debit - {} of {}", pUserId, pAmount);
		if (pUserId == null) {
			UserService.LOG.error("debit - id is null");
			throw new ParameterException("Utilisateur n'a pas d'id !", "pUserId");
		}
		if (pAmount == null) {
			UserService.LOG.error("debit - pAmount is null");
			throw new ParameterException("Somme est null !", "pAmount");
		}

		if (pAmount.doubleValue() < 0) {
			UserService.LOG.error("debit - somme is negative");
			throw new ParameterException("La somme a débiter doit être strictement positive !", "pAmount");
		}
		UserEntity user = super.getEntityFrom(pUserId);

		double actualSolde = user.getWallet().doubleValue();
		if (actualSolde - pAmount.doubleValue() >= 0) {
			user.setWallet(BigDecimal.valueOf(actualSolde - pAmount.doubleValue()));
			UserEntity resultUpdate = this.userDao.save(user);
			UserService.LOG.info("debit - OK new wallet is {}", user.getWallet().doubleValue());
			return resultUpdate;
		}
		UserService.LOG.error("debit - User with id={} has not enought money (left {})", user.getId(), actualSolde);
		throw new LackOfMoneyException("Utilisateur avec id=[" + user.getId() + "] n'a pas assez d'argent.");

	}

	/**
	 * Will add money to user's wallet.
	 *
	 * @param pUserId a user id
	 * @param pAmount an amount of money
	 * @return the user updated
	 * @throws EntityNotFoundException if entity was not found
	 * @throws ParameterException      if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity credit(Integer pUserId, BigDecimal pAmount) throws EntityNotFoundException {
		UserService.LOG.debug("credit - {} of {}", pUserId, pAmount);
		if (pUserId == null) {
			UserService.LOG.error("credit - id is null");
			throw new ParameterException("Utilisateur n'a pas d'id !", "pUserId");
		}
		if (pAmount == null) {
			UserService.LOG.error("credit - pAmount is null");
			throw new ParameterException("Somme est null !", "pAmount");
		}
		if (pAmount.doubleValue() <= 0) {
			UserService.LOG.error("debit - pAmount is negative");
			throw new ParameterException("La somme a créditer doit être strictement positive !", "pAmount");
		}

		UserEntity user = super.getEntityFrom(pUserId);
		double actualSolde = user.getWallet().doubleValue();
		user.setWallet(BigDecimal.valueOf(actualSolde + pAmount.doubleValue()));
		UserEntity resultUpdate = this.userDao.save(user);
		UserService.LOG.info("credit - OK new wallet is {}", user.getWallet().doubleValue());
		return resultUpdate;
	}

	/**
	 * Deletes the user. <br>
	 *
	 * Data are not removed from data base, only user's status will change.
	 *
	 * @param pUserId a user id
	 * @return the user deleted
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity delete(Integer pUserId) throws EntityNotFoundException, InconsistentStatusException {
		UserService.LOG.debug("delete - {}", pUserId);
		return this.updateStatus(pUserId, EntityStatus.DELETED);
	}

	/**
	 * Disables the user. <br>
	 *
	 * A disabled user cannot login not change any thing. Only a lunch lady can
	 * change this state.
	 *
	 * @param pUserId a user id
	 * @return the user disabled
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity disable(Integer pUserId) throws EntityNotFoundException, InconsistentStatusException {
		UserService.LOG.debug("disable - {}", pUserId);
		return this.updateStatus(pUserId, EntityStatus.DISABLED);
	}

	/**
	 * Enables the user. <br>
	 *
	 * A user enable can login or change any thing. Only a lunch lady can change
	 * this state.
	 *
	 * @param pUserId a user id
	 * @return the user enabled
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserEntity enable(Integer pUserId) throws EntityNotFoundException, InconsistentStatusException {
		UserService.LOG.debug("enable - {}", pUserId);
		return this.updateStatus(pUserId, EntityStatus.ENABLED);
	}

	/**
	 * Changes the user status. <br>
	 *
	 * @param pUserId        a user id
	 * @param pNewUserStatus the new status
	 * @return the user updated
	 * @throws EntityNotFoundException     if entity not found
	 * @throws InconsistentStatusException if entity state is not valid
	 * @throws ParameterException          if parameter is invalid
	 */
	private UserEntity updateStatus(Integer pUserId, EntityStatus pNewUserStatus)
			throws EntityNotFoundException, InconsistentStatusException {
		UserService.LOG.debug("updateStatus - {} for new state {}", pUserId, pNewUserStatus);
		if (pUserId == null) {
			UserService.LOG.error("updateStatus - id is null");
			throw new ParameterException("Utilisateur n'a pas d'id !", "pUserId");
		}
		if (pNewUserStatus == null) {
			UserService.LOG.error("updateStatus - newState is null");
			throw new ParameterException("Etat est null", "pNewUserStatus");
		}
		UserEntity user = super.getEntityFrom(pUserId);
		if (pNewUserStatus.equals(user.getStatus())) {
			throw new InconsistentStatusException(
					"Utilisateur ayant l'id " + pUserId + " est déjà dans l'état demandé (" + pNewUserStatus + ")");
		}
		user.setStatus(pNewUserStatus);
		UserEntity resultUpdate = this.userDao.save(user);
		UserService.LOG.info("updateStatus - OK");
		return resultUpdate;
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
	public UserEntity updateImage(Integer pUserId, ImageDtoIn pNewImageDto)
			throws EntityNotFoundException, InconsistentStatusException {
		UserService.LOG.debug("updateImage - {} for new image {}", pUserId, pNewImageDto);
		if (pUserId == null) {
			UserService.LOG.error("updateImage - id is null");
			throw new ParameterException("Utilisateur n'a pas d'id !", "pUserId");
		}
		if (pNewImageDto == null) {
			UserService.LOG.error("updateImage - pNewImageDto is null");
			throw new ParameterException("Image est null", "pNewImageDto");
		}
		UserEntity user = super.getEntityFrom(pUserId);
		if (user.isDeleted()) {
			throw new InconsistentStatusException("Impossible de changer l'image d'un utilisateur supprimé");
		}
		ImageEntity oldImg = user.getImage();
		if (oldImg != null) {
			if (Boolean.TRUE.equals(oldImg.getIsDefault())) {
				oldImg = pNewImageDto.toEntity();
			} else {
				oldImg.setImage64(pNewImageDto.getImage64());
				oldImg.setImagePath(pNewImageDto.getImagePath());
			}
			oldImg = this.imageService.saveIfNotInDataBase(oldImg);
			user.setImage(oldImg);
		}

		UserEntity resultUpdate = this.userDao.save(user);
		UserService.LOG.info("updateImage - OK");
		return resultUpdate;
	}

	@Override
	protected CrudRepository<UserEntity, Integer> getTargetedDao() {
		return this.userDao;
	}

	private void handleImage(UserEntity pEntity, UserDtoIn pNewDto) {
		ImageDtoIn imgDto = pNewDto.getImage();
		ImageEntity imgE = null;
		switch (pEntity.getSex()) {
		case MAN:
			imgE = this.imageService.saveIfNotInDataBase(DefaultImages.USER_DEFAULT_MAN_IMG);
			break;
		case WOMAN:
			imgE = this.imageService.saveIfNotInDataBase(DefaultImages.USER_DEFAULT_WOMAN_IMG);
			break;
		default:
			imgE = this.imageService.saveIfNotInDataBase(DefaultImages.USER_DEFAULT_OTHER_IMG);
			break;
		}

		if (imgDto != null) {
			UserService.LOG.debug("insertAndLinkImage - element has an image, will insert it");
			imgE = imgDto.toEntity();
			imgE = this.imageService.saveIfNotInDataBase(imgE);
			UserService.LOG.debug("insertAndLinkImage - elements's image was inserted with id {}", imgE.getId());

		}
		pEntity.setImage(imgE);
	}

}
