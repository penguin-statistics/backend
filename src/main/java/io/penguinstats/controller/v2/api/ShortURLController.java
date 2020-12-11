package io.penguinstats.controller.v2.api;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant;
import io.penguinstats.model.Item;
import io.penguinstats.model.Stage;
import io.penguinstats.service.GeoIPLocationService;
import io.penguinstats.service.ItemService;
import io.penguinstats.service.StageService;
import io.penguinstats.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController("shortURLController_v2")
@RequestMapping("/api/v2/short")
@Api(tags = {"ShortURL"})
public class ShortURLController {

	@Autowired
	private GeoIPLocationService geoIPLocationService;
	@Autowired
	private StageService stageService;
	@Autowired
	private ItemService itemService;

	@ApiOperation(value = "Redirect to penguin-stats homepage")
	@GetMapping("/")
	public ResponseEntity<Void> redirectWithoutWord(HttpServletRequest request) {
		return redirect(getPenguinStatsURL(request) + "?utm_medium=root&utm_campaign=root");
	}

	@ApiOperation(value = "Redirect to penguin-stats site by the given word")
	@GetMapping("/{word}")
	public ResponseEntity<Void> redirect(HttpServletRequest request, @PathVariable("word") String word) {
		if ("item".equals(word))
			return redirect(getPenguinStatsURL(request) + "result/item");

		if ("stage".equals(word))
			return redirect(getPenguinStatsURL(request) + "result/stage");

		if ("planner".equals(word))
			return redirect(getPenguinStatsURL(request) + "planner");

		ResponseEntity<Void> response = null;

		response = redirectByItemName(request, word);
		if (response != null)
			return response;

		response = redirectByStageCode(request, word);
		if (response != null)
			return response;

		response = redirectByItemId(request, word);
		if (response != null)
			return response;

		response = redirectByStageId(request, word);
		if (response != null)
			return response;

		return redirectUnknown(request, word);
	}

	private ResponseEntity<Void> redirect(String to) {
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(to)).build();
	}

	private ResponseEntity<Void> redirectByItemName(HttpServletRequest request, String word) {
		Map<String, Item> allNameItemMap = itemService.getAllNameItemMap();
		if (!allNameItemMap.containsKey(word))
			return null;
		String itemId = allNameItemMap.get(word).getItemId();
		return redirect(getPenguinStatsURL(request) + "result/item/" + itemId
				+ "?utm_source=exusiai&utm_medium=item&utm_campaign=name");
	}

	private ResponseEntity<Void> redirectByItemId(HttpServletRequest request, String word) {
		Map<String, Item> itemMap = itemService.getItemMap();
		if (!itemMap.containsKey(word))
			return null;
		String itemId = itemMap.get(word).getItemId();
		return redirect(getPenguinStatsURL(request) + "result/item/" + itemId
				+ "?utm_source=exusiai&utm_medium=item&utm_campaign=id");
	}

	private ResponseEntity<Void> redirectByStageCode(HttpServletRequest request, String word) {
		Map<String, Stage> allCodeStageMap = stageService.getAllCodeStageMap();
		if (!allCodeStageMap.containsKey(word))
			return null;
		Stage stage = allCodeStageMap.get(word);
		String stageId = stage.getStageId();
		String zoneId = stage.getZoneId();
		return redirect(getPenguinStatsURL(request) + "result/stage/" + zoneId + "/" + stageId
				+ "?utm_source=exusiai&utm_medium=stage&utm_campaign=code");
	}

	private ResponseEntity<Void> redirectByStageId(HttpServletRequest request, String word) {
		Map<String, Stage> stageMap = stageService.getStageMap();
		if (!stageMap.containsKey(word))
			return null;
		Stage stage = stageMap.get(word);
		String stageId = stage.getStageId();
		String zoneId = stage.getZoneId();
		return redirect(getPenguinStatsURL(request) + "result/stage/" + zoneId + "/" + stageId
				+ "?utm_source=exusiai&utm_medium=stage&utm_campaign=id");
	}

	private ResponseEntity<Void> redirectUnknown(HttpServletRequest request, String word) {
		try {
			String encoded = URLEncoder.encode(word, StandardCharsets.UTF_8.toString());
			return redirect(getPenguinStatsURL(request)
					+ "search?utm_source=exusiai&utm_medium=search&utm_campaign=fallback&q=" + encoded);
		} catch (UnsupportedEncodingException e) {
			log.error("Error in redirectUnknown: ", e);
			return redirect(getPenguinStatsURL(request) + "?utm_source=exusiai&utm_medium=root&utm_campaign=error");
		}
	}

	private String getPenguinStatsURL(HttpServletRequest request) {
		String ip = IpUtil.getIpAddr(request);
		return geoIPLocationService.isFromChinaMainland(ip) ? Constant.SiteURL.PENGUIN_STATS_CN
				: Constant.SiteURL.PENGUIN_STATS_IO;
	}

}
