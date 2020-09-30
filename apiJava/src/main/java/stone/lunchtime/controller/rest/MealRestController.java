// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import stone.lunchtime.dto.in.MealDtoIn;
import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.dto.out.ImageDtoOut;
import stone.lunchtime.dto.out.MealDtoOut;
import stone.lunchtime.entity.MealEntity;
import stone.lunchtime.service.MealService;
import stone.lunchtime.service.exception.EntityAlreadySavedException;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;

/**
 * Meal controller.
 */
@RestController
@RequestMapping("/meal")
@Tag(name = "Meal management API", description = "Meal management API")
public class MealRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private MealService service;

	/**
	 * Adds a meal. <br>
	 *
	 * You need to be connected as a lunch lady.
	 *
	 * @param pMeal   the meal to be added
	 * @param request the HttpServletRequest
	 * @return the meal added
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/add")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Meal management API" }, summary = "Adds a meal.", description = "Will add a meal into the data base. Will return it with its id when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your meal was added and returned in the response body.", content = @Content(schema = @Schema(implementation = MealDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your meal is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MealDtoOut> add(
			@Parameter(description = "Meal object that will be stored in database.", required = true) @RequestBody MealDtoIn pMeal,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> add - {}", remoteIP, pMeal);
		MealEntity result = this.service.add(pMeal);
		MealDtoOut dtoOut = new MealDtoOut(result);
		MealRestController.LOG.info("[{}] <-- add - New meal has id {}", remoteIP, dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Updates a meal. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * You cannot update status with this method.
	 *
	 * @param pMeal   the meal to be added
	 * @param pMealId the meal id that need to be updated
	 * @param request the HttpServletRequest
	 * @return the meal updated
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/update/{mealId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Meal management API" }, summary = "Updates a meal.", description = "Will update a meal already present in the data base. Will return it when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your meal was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = MealDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your meal or constraintId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MealDtoOut> update(
			@Parameter(description = "The meal's id", required = true) @PathVariable("mealId") Integer pMealId,
			@Parameter(description = "Meal object that will be updated in database. All present values will be updated.", required = true) @RequestBody MealDtoIn pMeal,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> update - {}", remoteIP, pMeal);
		MealEntity result = this.service.update(pMealId, pMeal);
		MealDtoOut dtoOut = new MealDtoOut(result);
		MealRestController.LOG.info("[{}] <-- update - Meal {} is updated by lunch lady {}", remoteIP, dtoOut.getId(),
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deletes a meal. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * Meal will still be in the data base but its status will be deleted. This
	 * status is permanent.
	 *
	 * @param pMealId the meal id that need to be deleted
	 * @param request the HttpServletRequest
	 * @return the meal deleted
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete/{mealId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Meal management API" }, summary = "Deletes a meal.", description = "Will delete a meal already present in the data base. Will return it when done. Note that element is not realy deleted from database but will change its status to DELETE (2). You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your meal was deleted and returned in the response body.", content = @Content(schema = @Schema(implementation = MealDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your mealId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to delete does not exist or is not a deleteable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MealDtoOut> delete(
			@Parameter(description = "The meal's id", required = true) @PathVariable("mealId") Integer pMealId,
			HttpServletRequest request) throws EntityNotFoundException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> delete - {}", remoteIP, pMealId);
		MealEntity result = this.service.delete(pMealId);
		MealDtoOut dtoOut = new MealDtoOut(result);
		MealRestController.LOG.info("[{}] <-- delete - Meal {} is deleted by lunch lady {}", remoteIP, pMealId,
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets a meal. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param pMealId id of the meal you are looking for
	 * @param request the HttpServletRequest
	 * @return the meal found or an error if none
	 * @throws EntityNotFoundException if an error occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find/{mealId}")
	@Operation(tags = {
			"Meal management API" }, summary = "Finds one meal.", description = "Will find a meal already present in the data base. Will return it when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your meal was found and returned in the response body.", content = @Content(schema = @Schema(implementation = MealDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your mealId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MealDtoOut> find(
			@Parameter(description = "The meal's id", required = true) @PathVariable("mealId") Integer pMealId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> find - {}", remoteIP, pMealId);
		MealEntity result = this.service.find(pMealId);
		MealDtoOut dtoOut = new MealDtoOut(result);
		MealRestController.LOG.info("[{}] <-- find - Has found meal {}", remoteIP, pMealId);
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets all meals. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the meals found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findall")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Meal management API" }, summary = "Finds all meals.", description = "Will find all meals already present in the data base. Will return them when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All meals found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MealDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<MealDtoOut>> findAll(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> findAll", remoteIP);
		List<MealEntity> result = this.service.findAll();
		List<MealDtoOut> dtos = new ArrayList<>();
		for (MealEntity elm : result) {
			dtos.add(new MealDtoOut(elm));
		}
		MealRestController.LOG.info("[{}] <-- findAll - Lunch lady {} has found {} meals", remoteIP,
				this.getConnectedUserId(), dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all meals available for the given week. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param pWeeknumber a week number between [1, 52]
	 * @param request     the HttpServletRequest
	 * @return all the meals available for the given week number found or an empty
	 *         list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallavailableforweek/{weeknumber}")
	@Operation(tags = {
			"Meal management API" }, summary = "Finds all meals for a specific week.", description = "Will find all meals already present in the data base and available for the specified week. Will return them when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All meals found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MealDtoOut.class)))),
			@ApiResponse(responseCode = "400", description = "Your weeknumber is not valid. Should be a number between [1..52].", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<MealDtoOut>> findAllForWeek(
			@Parameter(description = "The week's number. A number between 1 and 52.", required = true) @PathVariable("weeknumber") Integer pWeeknumber,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> findallavailableforweek - week {}", remoteIP, pWeeknumber);
		List<MealEntity> result = this.service.findAllAvailableForWeek(pWeeknumber);
		List<MealDtoOut> dtos = new ArrayList<>();
		for (MealEntity elm : result) {
			dtos.add(new MealDtoOut(elm));
		}
		MealRestController.LOG.info("[{}] <-- findallavailableforweek - Has found {} meals", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all meals available for today. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the meals available for today found or an empty list if none
	 *
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallavailablefortoday")
	@Operation(tags = {
			"Meal management API" }, summary = "Finds all meals for this current week.", description = "Will find all meals already present in the data base and available for this current week. Will return them when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All meals found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MealDtoOut.class)))) })
	public ResponseEntity<List<MealDtoOut>> findAllForToday(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> findallavailablefortoday", remoteIP);
		List<MealEntity> result = this.service
				.findAllAvailableForWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
		List<MealDtoOut> dtos = new ArrayList<>();
		for (MealEntity elm : result) {
			dtos.add(new MealDtoOut(elm));
		}
		MealRestController.LOG.info("[{}] <-- findallavailablefortoday - Has found {} meals", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Updates a menu image. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param id id of the element to be updated
	 * @param pImage   where to find the new information
	 * @param request the HttpServletRequest
	 * @return the element updated
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityAlreadySavedException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/updateimg/{id}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Meal management API" }, summary = "Updates an element's image.", description = "Will update the image of an element already present in the data base.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your elements's image was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = MealDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your element id is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist or has not the correct status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MealDtoOut> updateImage(
			@Parameter(description = "The element's id", required = true) @PathVariable("id") Integer id,
			@Parameter(description = "Image object that will be updated in database. ", required = true) @RequestBody ImageDtoIn pImage,
			HttpServletRequest request)
			throws EntityNotFoundException, EntityAlreadySavedException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> updateImage - {}", remoteIP, pImage);
		MealEntity result = this.service.updateImage(id, pImage);
		MealDtoOut dtoOut = new MealDtoOut(result);
		MealRestController.LOG.info("[{}] <-- updateImage - Meal {} image is updated by user {}", remoteIP,
				dtoOut.getId(), this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Finds an element's image. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param id id of the element's image you are looking for
	 * @param request the HttpServletRequest
	 * @return the image found or an error if none
	 * @throws EntityNotFoundException             if an error occurred
	 *
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findimg/{id}")
	@Operation(tags = {
			"Meal management API" }, summary = "Finds an elements's image.", description = "Will find an element's image already present in the data base. Will return it when done. Every one can call this method.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The image was found and returned in the response body.", content = @Content(schema = @Schema(implementation = ImageDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your id is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to find does not exist or is not findable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ImageDtoOut> findImage(
			@Parameter(description = "The elements's id", required = true) @PathVariable("id") Integer id,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		MealRestController.LOG.info("[{}] --> findImage - {}", remoteIP, id);
		MealEntity result = this.service.find(id);
		ImageDtoOut dtoOut = new ImageDtoOut(result.getImage());
		MealRestController.LOG.info("[{}] <-- findImage - Meal's image {} found by user {}", remoteIP, dtoOut.getId(),
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}
}
