package io.penguinstats.controller;

import io.penguinstats.model.Drop;
import io.penguinstats.model.ItemDrop;
import io.penguinstats.model.rest.SaveSingleReportRequest;
import io.penguinstats.service.DropMatrixService;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.UserService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.HashUtil;
import io.penguinstats.util.IpUtil;
import io.penguinstats.util.LimitationUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

	private static Logger logger = LogManager.getLogger(ReportController.class);

	@Autowired
	private ItemDropService itemDropService;

	@Autowired
	private DropMatrixService dropMatrixService;

	@Autowired
	private UserService userService;

	@Autowired
	private LimitationUtil limitationUtil;

	@Autowired
	private CookieUtil cookieUtil;

	@ApiOperation("Save single report")
	@PostMapping
	public ResponseEntity<String> saveSingleReport(@RequestBody @Valid SaveSingleReportRequest requestBody, HttpServletRequest request,
												   HttpServletResponse response) {
		try {
			String userID = cookieUtil.readUserIDFromCookie(request);
			if (userID == null) {
				userID = userService.createNewUser(IpUtil.getIpAddr(request));
			}
			try {
				CookieUtil.setUserIDCookie(response, userID);
			} catch (UnsupportedEncodingException e) {
				logger.error("Error in handleUserIDFromCookie: ", e);
			}
			logger.info("user " + userID + " POST /report\n" + requestBody.toString());
			Long timestamp = System.currentTimeMillis();
			String ip = IpUtil.getIpAddr(request);
			String stageId = requestBody.getStageId();
			int furnitureNum = requestBody.getFurnitureNum();
			String source = requestBody.getSource();
			String version = requestBody.getVersion();
			List<Drop> drops = requestBody.getDrops();
			if (furnitureNum > 0)
				drops.add(new Drop("furni", furnitureNum));

			// TODO: we should design checkers here, such as tag checker, time checker, limitation checker, etc.
			Boolean isReliable = null;
			if (source != null && source.equals("penguin-stats.io(internal)"))
				isReliable = true;
			else
				isReliable = limitationUtil.checkDrops(drops, stageId, timestamp);
			if (!isReliable)
				logger.warn("Abnormal drop data!");

			ItemDrop itemDrop = new ItemDrop(stageId, 1, drops, timestamp, ip, isReliable, source, version, userID);
			itemDropService.saveItemDrop(itemDrop);
			String itemDropHashId = HashUtil.getHash(itemDrop.getId().toString());
			if (isReliable) {
				// FIXME: For old stages, if a new kind of item drops, it has no corresponding matrix element in the database.
				if (!dropMatrixService.hasElementsForOneStage(stageId))
					dropMatrixService.initializeElementsForOneStage(stageId);
				for (Drop drop : drops)
					dropMatrixService.increaseQuantityForOneElement(stageId, drop.getItemId(), drop.getQuantity());
				dropMatrixService.increaseTimesForOneStage(stageId, 1);
			}
			return new ResponseEntity<>(itemDropHashId, HttpStatus.CREATED);
		} catch (JSONException jsonException) {
			logger.error("Error in saveSingleReport", jsonException);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Error in saveSingleReport", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Get personal report history")
	@GetMapping(path = "/history", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<ItemDrop>> getPersonalReportHistory(HttpServletRequest request,
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "page_size", defaultValue = "50") Integer pageSize,
			@RequestParam(name = "sort_by", defaultValue = "timestamp") String sortBy,
			@RequestParam(name = "direction", defaultValue = "ASC") String direction) {
		try {
			String userID = cookieUtil.readUserIDFromCookie(request);
			if (userID == null) {
				logger.error("Error in getPersonalReportHistory: Cannot read user ID");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			logger.info("user " + userID + " GET /report/history\n");
			Pageable pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.fromString(direction), sortBy));
			Page<ItemDrop> userItemDrops = itemDropService.getVisibleItemDropsByUserID(userID, pageable);
			return new ResponseEntity<>(userItemDrops.getContent(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getPersonalReportHistory", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(path = "/history")
	public ResponseEntity<String> deletePersonalReportHistory(HttpServletRequest request,
			@RequestParam("item_drop_id") String itemDropId) {
		try {
			String userID = cookieUtil.readUserIDFromCookie(request);
			if (userID == null) {
				logger.error("Error in deletePersonalReportHistory: Cannot read user ID");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			logger.info("user " + userID + " POST /report/history\n");
			itemDropService.deleteItemDrop(userID, itemDropId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in deletePersonalReportHistory", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Recall the last report")
	@PostMapping(path = "/recall")
	public ResponseEntity<String> recallPersonalReport(HttpServletRequest request,
			@RequestParam("item_drop_hash_id") String itemDropHashId) {
		try {
			String userID = cookieUtil.readUserIDFromCookie(request);
			if (userID == null) {
				logger.error("Error in recallPersonalReport: Cannot read user ID");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			logger.info("user " + userID + " POST /report/recall\n");
			itemDropService.recallItemDrop(userID, itemDropHashId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in recallPersonalReport", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
