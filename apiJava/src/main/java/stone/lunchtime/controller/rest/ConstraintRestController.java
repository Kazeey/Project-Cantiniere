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
import stone.lunchtime.dto.in.ConstraintDtoIn;
import stone.lunchtime.dto.out.ConstraintDtoOut;
import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.entity.ConstraintEntity;
import stone.lunchtime.service.ConstraintService;
import stone.lunchtime.service.exception.EntityNotFoundException;

/**
 * Constraint controller.
 */
@RestController
@RequestMapping("/constraint")
@Tag(name = "Constraint management API", description = "Constraint management API")
public class ConstraintRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private ConstraintService service;

	/**
	 * Adds a constraint. <br>
	 *
	 * You need to be connected as a lunch lady.
	 *
	 * @param pConstraint the constraint to be added
	 * @param request     the HttpServletRequest
	 * @return the constraint added
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/add")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Constraint management API" }, summary = "Adds a constraint.", description = "Will add a constraint into the data base. Will return it with its id when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your constraint was added and returned in the response body.", content = @Content(schema = @Schema(implementation = ConstraintDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your constraint is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ConstraintDtoOut> add(
			@Parameter(description = "Constraint object that will be stored in database.", required = true) @RequestBody ConstraintDtoIn pConstraint,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		ConstraintRestController.LOG.info("[{}] --> add - {}", remoteIP, pConstraint);
		ConstraintEntity result = this.service.add(pConstraint);
		ConstraintDtoOut dtoOut = new ConstraintDtoOut(result);
		ConstraintRestController.LOG.info("[{}] <-- add - New constraint has id {}", remoteIP, dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Updates a constraint. <br>
	 *
	 * You need to be connected as a lunch lady.
	 *
	 * @param pConstraintId id of the constraint to be updated
	 * @param pConstraint   the new data for this constraint
	 * @param request       the HttpServletRequest
	 * @return the constraint updated
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/update/{constraintId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Constraint management API" }, summary = "Updates a constraint.", description = "Will update a constraint already present in the data base. Will return it when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your constraint was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = ConstraintDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your constraint or constraintId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ConstraintDtoOut> update(
			@Parameter(description = "The contraint's id", required = true) @PathVariable("constraintId") Integer pConstraintId,
			@Parameter(description = "Constraint object that will be updated in database. All present values will be updated.", required = true) @RequestBody ConstraintDtoIn pConstraint,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		ConstraintRestController.LOG.info("[{}] --> update - {}", remoteIP, pConstraint);
		ConstraintEntity result = this.service.update(pConstraintId, pConstraint);
		ConstraintDtoOut dtoOut = new ConstraintDtoOut(result);
		ConstraintRestController.LOG.info("[{}] <-- update - Constraint {} is updated by lunch lady {}", remoteIP,
				dtoOut.getId(), this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deletes a constraint. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * Caution: constraint will be completely removed from data base.
	 *
	 * @param pConstraintId id of the constraint to be deleted
	 * @param request       the HttpServletRequest
	 * @return the HTTP Status regarding success or failure
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete/{constraintId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Constraint management API" }, summary = "Deletes a constraint.", description = "Will delete a constraint already present in the data base. Will not return it when done. Will realy delete it from data base. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your constraint was deleted and nothing in the response body."),
			@ApiResponse(responseCode = "400", description = "Your constraintId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to delete does not exist.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<?> delete(
			@Parameter(description = "The constraint's id", required = true) @PathVariable("constraintId") Integer pConstraintId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		ConstraintRestController.LOG.info("[{}] --> delete - {}", remoteIP, pConstraintId);
		this.service.delete(pConstraintId);
		ConstraintRestController.LOG.info("[{}] <-- delete - Constraint {} is deleted by lunch lady {}", remoteIP,
				pConstraintId, this.getConnectedUserId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Gets a constraint. <br>
	 *
	 * Every one can use this method. <br>
	 *
	 * @param pConstraintId id of the constraint you are looking for
	 * @param request       the HttpServletRequest
	 * @return the constraint found or an error if none
	 * @throws EntityNotFoundException if an error occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find/{constraintId}")
	@Operation(tags = {
			"Constraint management API" }, summary = "Finds one constraint.", description = "Will find a constraint already present in the data base. Will return it when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your constraint was found and returned in the response body.", content = @Content(schema = @Schema(implementation = ConstraintDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your constraintId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ConstraintDtoOut> find(
			@Parameter(description = "The constraint's id", required = true) @PathVariable("constraintId") Integer pConstraintId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		ConstraintRestController.LOG.info("[{}] --> find - {}", remoteIP, pConstraintId);
		ConstraintEntity result = this.service.find(pConstraintId);
		ConstraintDtoOut dtoOut = new ConstraintDtoOut(result);
		ConstraintRestController.LOG.info("[{}] <-- find - Has found constraint {}", remoteIP, pConstraintId);
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets all constraints. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the constraint found or an empty list if none @ if an error
	 *         occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findall")
	@Operation(tags = {
			"Constraint management API" }, summary = "Finds all constraints.", description = "Will find all constraints already present in the data base. Will return them when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All constraints found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConstraintDtoOut.class)))) })
	public ResponseEntity<List<ConstraintDtoOut>> findAll(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		ConstraintRestController.LOG.info("[{}] --> findAll", remoteIP);
		List<ConstraintEntity> result = this.service.findAll();
		List<ConstraintDtoOut> dtos = new ArrayList<>();
		for (ConstraintEntity elm : result) {
			dtos.add(new ConstraintDtoOut(elm));
		}
		ConstraintRestController.LOG.info("[{}] <-- findAll - Has found {} constraints", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
}
