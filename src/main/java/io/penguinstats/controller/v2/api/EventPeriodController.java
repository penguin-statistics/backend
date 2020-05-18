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
import io.penguinstats.model.EventPeriod;
import io.penguinstats.service.EventPeriodService;
import io.penguinstats.util.DateUtil;
import io.penguinstats.util.LastUpdateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("eventPeriodController_v2")
@RequestMapping("/api/v2/period")
@Api(tags = {"Period"})
public class EventPeriodController {

	@Autowired
	private EventPeriodService eventPeriodService;

	@ApiOperation("Get all event periods sorted with starting time by ascending order")
	@GetMapping(produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<EventPeriod>> getAllSortedEventPeriod() {
		List<EventPeriod> periods = eventPeriodService.getAllSortedEventPeriod();
		HttpHeaders headers = new HttpHeaders();
		String lastModified = DateUtil
				.formatDate(new Date(LastUpdateTimeUtil.getLastUpdateTime(LastUpdateMapKeyName.EVENT_PERIOD_LIST)));
		headers.add(HttpHeaders.LAST_MODIFIED, lastModified);
		return new ResponseEntity<List<EventPeriod>>(periods, headers, HttpStatus.OK);
	}

}
