package io.penguinstats.controller.v2.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.constant.Constant.LastUpdateMapKeyName;
import io.penguinstats.model.Notice;
import io.penguinstats.service.NoticeService;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("noticeController_v2")
@RequestMapping("/api/v2/notice")
@Api(tags = {"Notice"})
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@ApiOperation(value = "Get current active Notices")
	@GetMapping(produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Notice>> getAllNotice() {
		List<Notice> items = noticeService.getAvailableNotice(System.currentTimeMillis());
		HttpHeaders headers = new HttpHeaders();
		String lastModified =
				DateUtil.formatDate(new Date(LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.NOTICE_LIST)));
		headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
		return new ResponseEntity<List<Notice>>(items, headers, HttpStatus.OK);
	}

}
