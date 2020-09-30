// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.controller.rest;

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
import stone.lunchtime.dto.in.IngredientDtoIn;
import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.dto.out.ImageDtoOut;
import stone.lunchtime.dto.out.IngredientDtoOut;
import stone.lunchtime.entity.IngredientEntity;
import stone.lunchtime.service.IngredientService;
import stone.lunchtime.service.exception.EntityAlreadySavedException;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;

/**
 * Ingredient controller.
 */
@RestController
@RequestMapping("/ingredient")
@Tag(name = "Ingredient management API", description = "Ingredient management API")
public class IngredientRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private IngredientService service;

	/**
	 * Adds an ingredient. <br>
	 *
	 * You need to be connected as a lunch lady.
	 *
	 * @param pIngredient the ingredient to be added
	 * @param request     the HttpServletRequest
	 * @return the ingredient added
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/add")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Ingredient management API" }, summary = "Adds an ingredient.", description = "Will add an ingredient into the data base. Will return it with its id when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your ingredient was added and returned in the response body.", content = @Content(schema = @Schema(implementation = IngredientDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your ingredient is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<IngredientDtoOut> add(
			@Parameter(description = "Ingredient object that will be stored in database", required = true) @RequestBody IngredientDtoIn pIngredient,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> add - {}", remoteIP, pIngredient);
		IngredientEntity result = this.service.add(pIngredient);
		IngredientDtoOut dtoOut = new IngredientDtoOut(result);
		IngredientRestController.LOG.info("[{}] <-- add - New ingredient has id {}", remoteIP, dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Updates an ingredient. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * You cannot update status with this method.
	 *
	 * @param pIngredientId id of the ingredient to be updated
	 * @param pIngredient   the new data for this ingredient
	 * @param request       the HttpServletRequest
	 * @return the ingredient updated
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/update/{ingredientId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Ingredient management API" }, summary = "Updates an ingredient.", description = "Will update an ingredient already present in the data base. Will return it when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your ingredient was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = IngredientDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your ingredient or ingredientId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<IngredientDtoOut> update(
			@Parameter(description = "The ingredient's id", required = true) @PathVariable("ingredientId") Integer pIngredientId,
			@Parameter(description = "Ingredient object that will be updated in database. All present values will be updated.", required = true) @RequestBody IngredientDtoIn pIngredient,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> update - {}", remoteIP, pIngredient);
		IngredientEntity result = this.service.update(pIngredientId, pIngredient);
		IngredientDtoOut dtoOut = new IngredientDtoOut(result);
		IngredientRestController.LOG.info("[{}] <-- update - Ingredient {} is updated by lunch lady {}", remoteIP,
				dtoOut.getId(), this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deletes an ingredient. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * Ingredient will still be in the data base but its status will be deleted.
	 * This status is permanent.
	 *
	 * @param pIngredientId id of the ingredient to be deleted
	 * @param request       the HttpServletRequest
	 * @return the ingredient deleted
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete/{ingredientId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Ingredient management API" }, summary = "Deletes an ingredient.", description = "Will delete an ingredient already present in the data base. Will return it when done. Note that element is not realy deleted from database but will change its status to DELETE (2). You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your ingredient was deleted and returned in the response body.", content = @Content(schema = @Schema(implementation = IngredientDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your ingredientId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to delete does not exist or is not a deleteable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<IngredientDtoOut> delete(
			@Parameter(description = "The ingredient's id", required = true) @PathVariable("ingredientId") Integer pIngredientId,
			HttpServletRequest request) throws EntityNotFoundException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> delete - {}", remoteIP, pIngredientId);
		IngredientEntity result = this.service.delete(pIngredientId);
		IngredientDtoOut dtoOut = new IngredientDtoOut(result);
		IngredientRestController.LOG.info("[{}] <-- delete - Ingredient {} is deleted by lunch lady {}", remoteIP,
				pIngredientId, this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets an ingredient. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param pIngredientId id of the ingredient you are looking for
	 * @param request       the HttpServletRequest
	 * @return the ingredient found or an error if none
	 * @throws EntityNotFoundException if an error occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find/{ingredientId}")
	@Operation(tags = {
			"Ingredient management API" }, summary = "Finds one ingredient.", description = "Will find an ingredient already present in the data base. Will return it when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your ingredient was found and returned in the response body.", content = @Content(schema = @Schema(implementation = IngredientDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your ingredientId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<IngredientDtoOut> find(
			@Parameter(description = "The ingredient's id", required = true) @PathVariable("ingredientId") Integer pIngredientId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> find - {}", remoteIP, pIngredientId);
		IngredientEntity result = this.service.find(pIngredientId);
		IngredientDtoOut dtoOut = new IngredientDtoOut(result);
		IngredientRestController.LOG.info("[{}] <-- find - Has found meal {}", remoteIP, pIngredientId);
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets all ingredients. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the ingredients found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findall")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Ingredient management API" }, summary = "Finds all ingredients.", description = "Will find all ingredients already present in the data base. Will return them when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All ingredients found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = IngredientDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<IngredientDtoOut>> findAll(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> findAll", remoteIP);
		List<IngredientEntity> result = this.service.findAll();
		List<IngredientDtoOut> dtos = new ArrayList<>();
		for (IngredientEntity elm : result) {
			dtos.add(new IngredientDtoOut(elm));
		}
		IngredientRestController.LOG.info("[{}] <-- findAll - Lunch lady {} has found {} ingredients", remoteIP,
				this.getConnectedUserId(), dtos.size());
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
			"Ingredient management API" }, summary = "Updates an element's image.", description = "Will update the image of an element already present in the data base.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your elements's image was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = IngredientDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your element id is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist or has not the correct status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<IngredientDtoOut> updateImage(
			@Parameter(description = "The element's id", required = true) @PathVariable("id") Integer id,
			@Parameter(description = "Image object that will be updated in database. ", required = true) @RequestBody ImageDtoIn pImage,
			HttpServletRequest request)
			throws EntityNotFoundException, EntityAlreadySavedException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> updateImage - {}", remoteIP, pImage);
		IngredientEntity result = this.service.updateImage(id, pImage);
		IngredientDtoOut dtoOut = new IngredientDtoOut(result);
		IngredientRestController.LOG.info("[{}] <-- updateImage - Ingredient {} image is updated by user {}", remoteIP,
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
			"Ingredient management API" }, summary = "Finds an elements's image.", description = "Will find an element's image already present in the data base. Will return it when done. Every one can call this method.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The image was found and returned in the response body.", content = @Content(schema = @Schema(implementation = ImageDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your id is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to find does not exist or is not findable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ImageDtoOut> findImage(
			@Parameter(description = "The elements's id", required = true) @PathVariable("id") Integer id,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		IngredientRestController.LOG.info("[{}] --> findImage - {}", remoteIP, id);
		IngredientEntity result = this.service.find(id);
		ImageDtoOut dtoOut = new ImageDtoOut(result.getImage());
		IngredientRestController.LOG.info("[{}] <-- findImage - Ingredient's image {} found by user {}", remoteIP,
				dtoOut.getId(), this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}
}
