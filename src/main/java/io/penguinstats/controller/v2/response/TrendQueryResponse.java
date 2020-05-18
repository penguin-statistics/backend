package io.penguinstats.controller.v2.response;

import static java.util.stream.Collectors.groupingBy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.penguinstats.model.DropMatrixElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The response model for trend query.")
public class TrendQueryResponse implements Serializable, BasicQueryResponse {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "All trend results in a map. Key is stageId.")
	@JsonProperty("trend")
	private Map<String, StageTrend> stageTrendMap;

	public TrendQueryResponse(List<DropMatrixElement> els) {
		Map<String, Map<String, List<DropMatrixElement>>> map = els.stream()
				.collect(groupingBy(DropMatrixElement::getStageId, groupingBy(DropMatrixElement::getItemId)));

		Map<String, StageTrend> stageTrendMap = new HashMap<>();
		map.forEach((stageId, subMap) -> {
			Long[] startTime = new Long[] {null};
			Map<String, TrendDetail> trendDetailMap = new HashMap<>();
			subMap.forEach((itemId, elements) -> {
				List<Integer> quantityList = new ArrayList<>(elements.size());
				List<Integer> timesList = new ArrayList<>(elements.size());
				elements.forEach(element -> {
					quantityList.add(element != null ? element.getQuantity() : 0);
					timesList.add(element != null ? element.getTimes() : 0);
					Long start = element.getStart();
					if (startTime[0] == null || start != null && start.compareTo(startTime[0]) < 0)
						startTime[0] = start;
				});
				TrendDetail trendDetail = new TrendDetail(timesList, quantityList);
				trendDetailMap.put(itemId, trendDetail);
			});
			StageTrend stageTrend = new StageTrend(startTime[0], trendDetailMap);
			stageTrendMap.put(stageId, stageTrend);
		});
		this.stageTrendMap = stageTrendMap;
	}

}
