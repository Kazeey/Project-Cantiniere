// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import stone.lunchtime.controller.AbstractController;
import stone.lunchtime.dto.in.ImageDtoIn;
import stone.lunchtime.dto.in.UserDtoIn;
import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.dto.out.ImageDtoOut;
import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.entity.UserEntity;
import stone.lunchtime.service.UserService;
import stone.lunchtime.service.exception.EntityAlreadySavedException;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentRoleException;
import stone.lunchtime.service.exception.InconsistentStatusException;
import stone.lunchtime.service.exception.LackOfMoneyException;

/**
 * User controller.
 */
@RestController
@RequestMapping("/user")
@Tag(name = "User management API", description = "User management API")
public class UserRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private UserService service;

	/**
	 * Registers a user. <br>
	 *
	 * You do not need to be connected. <br>
	 * Lunch lady role can only be set if there is none in the data base. Otherwise
	 * you'll need to use update in order to set/unset the lunch lady role.
	 *
	 * @param pUser   a new user
	 * @param request the HttpServletRequest
	 * @return the new subscribed user
	 * @throws InconsistentRoleException   if an error occurred
	 * @throws EntityAlreadySavedException if an error occurred
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/register")
	@Operation(tags = {
			"User management API" }, summary = "Adds a user.", description = "Will add a user into the data base. Will return it with its id when done. You do not need to be connected in order to execute this action. Lunch lady role can only be set if there is none in the data base. Otherwise you'll need to use update in order to set/unset the lunch lady role.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user was added and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your user is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "User is already in the data base (email must be unique) or there is a role problem.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> register(
			@Parameter(description = "User object that will be stored in database", required = true) @RequestBody UserDtoIn pUser,
			HttpServletRequest request) throws EntityAlreadySavedException, InconsistentRoleException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> subscribe - {}", remoteIP, pUser);
		UserEntity result = this.service.register(pUser);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- subscribe - New user has id {}", remoteIP, dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Updates a user. <br>
	 *
	 * You need to set in the DTO in ONLY the values that you want to update, set the others to null.
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to update yourself.
	 * <br>
	 * You cannot change status with this method. <br>
	 * Only Lunch Lady can update the lunch lady role.
	 *
	 * @param pUserId id of the user to be updated
	 * @param pUser   where to find the new information
	 * @param request the HttpServletRequest
	 * @return the user updated
	 * @throws InconsistentRoleException           if an error occurred
	 * @throws EntityAlreadySavedException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/update/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Updates a user.", description = "Will update a user already present in the data base. Will return it when done. You must be connected in order to update yourself, or have the lunch lady role in order to update someone. You need to set in the DTO in ONLY the values that you want to update, set the others to null. Note that status and image cannot be changed and only a lunch lady can add/remove the lunch lady role.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your user or userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot update this user (because you are not a lunch lady or it is not you).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> update(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			@Parameter(description = "User object that will be updated in database. ONLY not null values will be updated (status and image cannot be changed and only a lunch lady can add/remove the lunch lady role).", required = true) @RequestBody UserDtoIn pUser,
			HttpServletRequest request)
			throws EntityNotFoundException, EntityAlreadySavedException, InconsistentRoleException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> update - {}", remoteIP, pUser);
		// Check for role, since only LunchLady can change LunchLady
		UserEntity result = this.service.update(pUserId, pUser, super.hasLunchLadyRole());
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- update - User {} is updated by user {}", remoteIP, dtoOut.getId(),
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deletes a user. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to delete yourself.
	 * <br>
	 * User will still be in the data base but its status will be deleted. He will
	 * be able to do nothing. This status is permanent.
	 *
	 * @param pUserId id of the user to be deleted
	 * @param request the HttpServletRequest
	 * @return the user deleted
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Deletes a user.", description = "Will delete a user already present in the data base. Will return it when done. Note that element is not realy deleted from database but will change its status to DELETE (2). You must be connected in order to delete yourself, or have the lunch lady role in order to delete someone.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user was deleted and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot delete this user (because you are not a lunch lady or it is not you).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to delete does not exist or is not a deleteable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> delete(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			HttpServletRequest request) throws EntityNotFoundException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> delete - {}", remoteIP, pUserId);
		UserEntity result = this.service.delete(pUserId);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- delete - User {} is deleted by user {}", remoteIP, pUserId,
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deactivates a user. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * User deactivated cannot do anything until they are reactivated.
	 *
	 * @param pUserId id of the user to be deactivated
	 * @param request the HttpServletRequest
	 * @return the user deactivated
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/deactivate/{userId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Deactivates a user.", description = "Will deactivate a user already present in the data base. Will return it when done. Will change the user's status to DISABLED (1). You must be connected and have the lunch lady role in order to deactivate someone.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user was deactivated and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or you are not a lunch lady.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to deactivate does not exist or is not a deactivable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> deactivate(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			HttpServletRequest request) throws EntityNotFoundException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> deactivate - {}", remoteIP, pUserId);
		UserEntity result = this.service.disable(pUserId);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- deactivate - User {} is deactivated by lunch lady {}", remoteIP, pUserId,
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Activates a user. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param pUserId id of the user to be activated
	 * @param request the HttpServletRequest
	 * @return the user activated
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/activate/{userId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Activates a user.", description = "Will activate a user already present in the data base. Will return it when done. Will change the user's status to ENABLED (0). You must be connected and have the lunch lady role in order to activate someone.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user was activated and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or you are not a lunch lady.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to activate does not exist or is not an activable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> activate(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			HttpServletRequest request) throws EntityNotFoundException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> activate - {}", remoteIP, pUserId);
		UserEntity result = this.service.enable(pUserId);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- activate - User {} is activated by lunch lady {}", remoteIP, pUserId,
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Will add money to the user's wallet. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param pUserId id of the user
	 * @param pAmount amount of money that will be added to user's wallet
	 * @param request the HttpServletRequest
	 * @return the user credited
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/credit/{userId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Adds money to a user's wallet.", description = "Will add money to a user's wallet already present in the data base. Will return it when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user's wallet was updated and user is returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or you are not a lunch lady.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "User was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> credit(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			@Parameter(description = "The amount of money to add to the user's wallet.", required = true) @RequestParam("amount") BigDecimal pAmount,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> credit - {} of {}", remoteIP, pUserId, pAmount);
		UserEntity result = this.service.credit(pUserId, pAmount);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- credit - User {} is credited of {} by lunch lady {}", remoteIP, pUserId,
				pAmount, this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Will remove money to the user's wallet. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param pUserId id of the user
	 * @param pAmount amount of money that will be removed of user's wallet
	 * @param request the HttpServletRequest
	 * @return the user debited
	 * @throws LackOfMoneyException                if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 *
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/debit/{userId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Removes money to a user's wallet.", description = "Will remove money to a user's wallet already present in the data base. Will return it when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user's wallet was updated and user is returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or you are not a lunch lady.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "User was not found or the wallet does not have enought money.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> debit(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			@Parameter(description = "The amount of money to remove from the user's wallet.", required = true) @RequestParam("amount") BigDecimal pAmount,
			HttpServletRequest request) throws EntityNotFoundException, LackOfMoneyException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> debit - {} of {}", remoteIP, pUserId, pAmount);
		UserEntity result = this.service.debit(pUserId, pAmount);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- debit - User {} is credited of {} by user {}", remoteIP, pUserId, pAmount,
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Finds a user. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to find yourself. <br>
	 *
	 * @param pUserId id of the user you are looking for
	 * @param request the HttpServletRequest
	 * @return the user found or an error if none
	 * @throws EntityNotFoundException             if an error occurred
	 *
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Finds one user.", description = "Will find a user already present in the data base. Will return it when done. You must be connected in order to find yourself, or have the lunch lady role in order to find someone.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The user was found and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot find this user (because you are not a lunch lady or it is not you).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to find does not exist or is not findable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> find(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> find - {}", remoteIP, pUserId);
		UserEntity result = this.service.find(pUserId);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- find - User {} has found user {}", remoteIP, this.getConnectedUserId(),
				pUserId);
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Finds all users. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the users found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findall")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Finds all users.", description = "Will find all users already present in the data base. Will return them when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All users found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<UserDtoOut>> findAll(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> findAll", remoteIP);
		List<UserEntity> result = this.service.findAll();
		List<UserDtoOut> dtos = new ArrayList<>();
		for (UserEntity elm : result) {
			dtos.add(new UserDtoOut(elm));
		}
		UserRestController.LOG.info("[{}] <-- findAll - Lunch Lady {} has found {} users", remoteIP,
				this.getConnectedUserId(), dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Updates a user's image. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to update yourself.
	 * <br>
	 *
	 * @param pUserId id of the user to be updated
	 * @param pImage   where to find the new information
	 * @param request the HttpServletRequest
	 * @return the user updated
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityAlreadySavedException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/updateimg/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Updates a user image.", description = "Will update the image of a user already present in the data base. Will return it when done. You must be connected in order to update yourself, or have the lunch lady role in order to update someone.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your user's image was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = UserDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot update this user (because you are not a lunch lady or it is not you).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist or has not the correct status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<UserDtoOut> updateImage(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			@Parameter(description = "Image object that will be updated in database. ", required = true) @RequestBody ImageDtoIn pImage,
			HttpServletRequest request)
			throws EntityNotFoundException, EntityAlreadySavedException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> updateImage - {}", remoteIP, pImage);
		// Check for role, since only LunchLady can change LunchLady
		UserEntity result = this.service.updateImage(pUserId, pImage);
		UserDtoOut dtoOut = new UserDtoOut(result);
		UserRestController.LOG.info("[{}] <-- updateImage - User {} image is updated by user {}", remoteIP,
				dtoOut.getId(), this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Finds a user's image. <br>
	 *
	 * You need to be connected. <br>
	 * If your are not the lunch lady, you will only be able to find yourself. <br>
	 *
	 * @param pUserId id of the user's image you are looking for
	 * @param request the HttpServletRequest
	 * @return the image found or an error if none
	 * @throws EntityNotFoundException             if an error occurred
	 *
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findimg/{userId}")
	@PreAuthorize("#pUserId == authentication.details.id or hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"User management API" }, summary = "Finds the user's image.", description = "Will find a user's image already present in the data base. Will return it when done. You must be connected in order to find your image, or have the lunch lady role in order to find someone's image.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The image was found and returned in the response body.", content = @Content(schema = @Schema(implementation = ImageDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your userId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or cannot find this user (because you are not a lunch lady or it is not you).", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to find does not exist or is not findable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ImageDtoOut> findImage(
			@Parameter(description = "The user's id", required = true) @PathVariable("userId") Integer pUserId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		UserRestController.LOG.info("[{}] --> findImage - {}", remoteIP, pUserId);
		UserEntity result = this.service.find(pUserId);
		ImageDtoOut dtoOut = new ImageDtoOut(result.getImage());
		UserRestController.LOG.info("[{}] <-- findImage - User's image {} found by user {}", remoteIP, dtoOut.getId(),
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

}
