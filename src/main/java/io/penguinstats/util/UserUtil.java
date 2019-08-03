package io.penguinstats.util;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import io.penguinstats.service.ItemDropService;
import io.penguinstats.service.UserService;

@Component("userUtil")
public class UserUtil {

	private static UserUtil userUtil;

	@Autowired
	private UserService userService;
	@Autowired
	private ItemDropService itemDropService;

	@PostConstruct
	public void init() {
		userUtil = this;
		userUtil.userService = this.userService;
		userUtil.itemDropService = this.itemDropService;
	}

	/** 
	 * @Title: updateTwoUploadCountsForAllUsers 
	 * @Description: Update totalUpload and reliableUpload for all users from ItemDrop table.
	 * @return void
	 */
	public void updateTwoUploadCountsForAllUsers() {
		Map<String, Integer> totalUploadMap =
				itemDropService.generateUploadCountMap(Criteria.where("isDeleted").is(false));
		Map<String, Integer> reliableUploadMap = itemDropService.generateUploadCountMap(new Criteria()
				.andOperator(Criteria.where("isReliable").is(true), Criteria.where("isDeleted").is(false)));
		userService.updateUploadFromMap(totalUploadMap, "total");
		userService.updateUploadFromMap(reliableUploadMap, "reliable");
	}

}
