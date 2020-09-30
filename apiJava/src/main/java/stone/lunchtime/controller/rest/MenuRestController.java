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
import stone.lunchtime.dto.in.MenuDtoIn;
import stone.lunchtime.dto.out.ExceptionDtoOut;
import stone.lunchtime.dto.out.ImageDtoOut;
import stone.lunchtime.dto.out.MenuDtoOut;
import stone.lunchtime.entity.MenuEntity;
import stone.lunchtime.service.MenuService;
import stone.lunchtime.service.exception.EntityAlreadySavedException;
import stone.lunchtime.service.exception.EntityNotFoundException;
import stone.lunchtime.service.exception.InconsistentStatusException;

/**
 * Menu controller.
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "Menu management API", description = "Menu management API")
public class MenuRestController extends AbstractController {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private MenuService service;

	/**
	 * Adds a menu. <br>
	 *
	 * You need to be connected as a lunch lady.
	 *
	 * @param pMenu   the menu to be added
	 * @param request the HttpServletRequest
	 * @return the menu added
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/add")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Menu management API" }, summary = "Adds a menu.", description = "Will add a menu into the data base. Will return it with its id when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your menu was added and returned in the response body.", content = @Content(schema = @Schema(implementation = MenuDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your menu is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MenuDtoOut> add(
			@Parameter(description = "Menu object that will be stored in database.", required = true) @RequestBody MenuDtoIn pMenu,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> add - {}", remoteIP, pMenu);
		MenuEntity result = this.service.add(pMenu);
		MenuDtoOut dtoOut = new MenuDtoOut(result);
		MenuRestController.LOG.info("[{}] <-- add - New menu has id {}", remoteIP, dtoOut.getId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Updates a menu. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * You cannot update status with this method.
	 *
	 * @param pMenu   the menu to be added
	 * @param pMenuId the menu id that need to be updated
	 * @param request the HttpServletRequest
	 * @return the menu updated
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/update/{menuId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Menu management API" }, summary = "Updates a menu.", description = "Will update a menu already present in the data base. Will return it when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your menu was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = MenuDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your menu or menuId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MenuDtoOut> update(
			@Parameter(description = "The menu's id", required = true) @PathVariable("menuId") Integer pMenuId,
			@Parameter(description = "Menu object that will be updated in database. All present values will be updated.", required = true) @RequestBody MenuDtoIn pMenu,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> update - {}", remoteIP, pMenu);
		MenuEntity result = this.service.update(pMenuId, pMenu);
		MenuDtoOut dtoOut = new MenuDtoOut(result);
		MenuRestController.LOG.info("[{}] <-- update - Menu {} is updated by lunch lady {}", remoteIP, dtoOut.getId(),
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Deletes a menu. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 * Meal will still be in the data base but its status will be deleted. This
	 * status is permanent.
	 *
	 * @param pMenuId the menu id that need to be deleted
	 * @param request the HttpServletRequest
	 * @return the menu deleted
	 * @throws InconsistentStatusException         if an error occurred
	 * @throws EntityNotFoundException             if an error occurred
	 */
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/delete/{menuId}")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Menu management API" }, summary = "Deletes a menu.", description = "Will delete a menu already present in the data base. Will return it when done. Note that element is not realy deleted from database but will change its status to DELETE (2). You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your menu was deleted and returned in the response body.", content = @Content(schema = @Schema(implementation = MenuDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your menuId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to delete does not exist or is not a deleteable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MenuDtoOut> delete(
			@Parameter(description = "The menu's id", required = true) @PathVariable("menuId") Integer pMenuId,
			HttpServletRequest request) throws EntityNotFoundException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> delete - {}", remoteIP, pMenuId);
		MenuEntity result = this.service.delete(pMenuId);
		MenuDtoOut dtoOut = new MenuDtoOut(result);
		MenuRestController.LOG.info("[{}] <-- delete - Menu {} is deleted by lunch lady {}", remoteIP, pMenuId,
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets a menu. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param pMenuId id of the menu you are looking for
	 * @param request the HttpServletRequest
	 * @return the menu found or an error if none
	 * @throws EntityNotFoundException if an error occurred
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find/{menuId}")
	@Operation(tags = {
			"Menu management API" }, summary = "Finds one menu.", description = "Will find a menu already present in the data base. Will return it when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your menu was found and returned in the response body.", content = @Content(schema = @Schema(implementation = MenuDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your menuId is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element was not found.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MenuDtoOut> find(
			@Parameter(description = "The menu's id", required = true) @PathVariable("menuId") Integer pMenuId,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> find - {}", remoteIP, pMenuId);
		MenuEntity result = this.service.find(pMenuId);
		MenuDtoOut dtoOut = new MenuDtoOut(result);
		MenuRestController.LOG.info("[{}] <-- find - Has found menu {}", remoteIP, pMenuId);
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}

	/**
	 * Gets all menus. <br>
	 *
	 * You need to be connected as a lunch lady. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the menus found or an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findall")
	@PreAuthorize("hasRole('ROLE_LUNCHLADY')")
	@Operation(tags = {
			"Menu management API" }, summary = "Finds all menus.", description = "Will find all menus already present in the data base. Will return them when done. You must be connected and have the lunch lady role in order to execute this action.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All menus found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuDtoOut.class)))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<MenuDtoOut>> findAll(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> findAll", remoteIP);
		List<MenuEntity> result = this.service.findAll();
		List<MenuDtoOut> dtos = new ArrayList<>();
		for (MenuEntity elm : result) {
			dtos.add(new MenuDtoOut(elm));
		}
		MenuRestController.LOG.info("[{}] <-- findAll - Lunch lady has found {} menus", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all menus available for the given week. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param pWeeknumber a week number between [1, 52]
	 * @param request     the HttpServletRequest
	 * @return all the menus available for the given week number found or an empty
	 *         list if none
	 *
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallavailableforweek/{weeknumber}")
	@Operation(tags = {
			"Menu management API" }, summary = "Finds all menus for a specific week.", description = "Will find all menus already present in the data base and available for the specified week. Will return them when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All menus found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuDtoOut.class)))),
			@ApiResponse(responseCode = "400", description = "Your weeknumber is not valid. Should be a number between [1..52].", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<List<MenuDtoOut>> findAllForWeek(
			@Parameter(description = "The week's number. A number between 1 and 52.", required = true) @PathVariable("weeknumber") Integer pWeeknumber,
			HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> findallavailableforweek - week {}", remoteIP, pWeeknumber);
		List<MenuEntity> result = this.service.findAllAvailableForWeek(pWeeknumber);
		List<MenuDtoOut> dtos = new ArrayList<>();
		for (MenuEntity elm : result) {
			dtos.add(new MenuDtoOut(elm));
		}
		MenuRestController.LOG.info("[{}] <-- findallavailableforweek - Has found {} menus", remoteIP, dtos.size());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	/**
	 * Gets all menus available for today. <br>
	 *
	 * Every one can use this method. No need to be connected. <br>
	 *
	 * @param request the HttpServletRequest
	 * @return all the menus available for today, an empty list if none
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/findallavailablefortoday")
	@Operation(tags = {
			"Menu management API" }, summary = "Finds all menus for today (= this week).", description = "Will find all menus already present in the data base and available for today (= this current week). Will return them when done. You do not need to be connected in order to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All menus found will be in the response body.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuDtoOut.class)))) })
	public ResponseEntity<List<MenuDtoOut>> findAllForWeek(HttpServletRequest request) {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> findallavailablefortoday", remoteIP);
		List<MenuEntity> result = this.service
				.findAllAvailableForWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
		List<MenuDtoOut> dtos = new ArrayList<>();
		for (MenuEntity elm : result) {
			dtos.add(new MenuDtoOut(elm));
		}
		MenuRestController.LOG.info("[{}] <-- findallavailablefortoday - Has found {} menus", remoteIP, dtos.size());
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
			"Menu management API" }, summary = "Updates an element's image.", description = "Will update the image of an element already present in the data base.", security = {
					@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Your elements's image was updated and returned in the response body.", content = @Content(schema = @Schema(implementation = MenuDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your element id is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "401", description = "You are not connected or do not have the LunchLady role.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to update does not exist or has not the correct status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<MenuDtoOut> updateImage(
			@Parameter(description = "The element's id", required = true) @PathVariable("id") Integer id,
			@Parameter(description = "Image object that will be updated in database. ", required = true) @RequestBody ImageDtoIn pImage,
			HttpServletRequest request)
			throws EntityNotFoundException, EntityAlreadySavedException, InconsistentStatusException {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> updateImage - {}", remoteIP, pImage);
		MenuEntity result = this.service.updateImage(id, pImage);
		MenuDtoOut dtoOut = new MenuDtoOut(result);
		MenuRestController.LOG.info("[{}] <-- updateImage - Menu {} image is updated by user {}", remoteIP,
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
			"Menu management API" }, summary = "Finds an elements's image.", description = "Will find an element's image already present in the data base. Will return it when done. Every one can call this method.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The image was found and returned in the response body.", content = @Content(schema = @Schema(implementation = ImageDtoOut.class))),
			@ApiResponse(responseCode = "400", description = "Your id is not valid.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))),
			@ApiResponse(responseCode = "412", description = "The element to find does not exist or is not findable status.", content = @Content(schema = @Schema(implementation = ExceptionDtoOut.class))) })
	public ResponseEntity<ImageDtoOut> findImage(
			@Parameter(description = "The elements's id", required = true) @PathVariable("id") Integer id,
			HttpServletRequest request) throws EntityNotFoundException {
		final String remoteIP = request.getRemoteAddr();
		MenuRestController.LOG.info("[{}] --> findImage - {}", remoteIP, id);
		MenuEntity result = this.service.find(id);
		ImageDtoOut dtoOut = new ImageDtoOut(result.getImage());
		MenuRestController.LOG.info("[{}] <-- findImage - Menu's image {} found by user {}", remoteIP, dtoOut.getId(),
				this.getConnectedUserId());
		return new ResponseEntity<>(dtoOut, HttpStatus.OK);
	}
}
