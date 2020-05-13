package io.penguinstats.model.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.service.ItemDropService;
import io.penguinstats.util.QueryUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrendQuery implements Serializable, BasicQuery {

	private static final long serialVersionUID = 1L;

	public TrendQuery(ItemDropService itemDropService) {
		this.itemDropService = itemDropService;
	}

	@JsonIgnore
	private ItemDropService itemDropService;

	private Server server;

	private String stageId;

	private List<String> itemIds;

	private Long start;

	private Long end;

	private String userID;

	private Integer interval;

	private Integer timeout;

	@Override
	public List<DropMatrixElement> execute() throws Exception {
		return QueryUtil.runQuery(
				() -> itemDropService.generateCustomDropMatrixElements(server, stageId, itemIds, start, end,
						Optional.ofNullable(userID).map(userID -> Arrays.asList(userID)).orElse(null), interval),
				timeout);
	}

}
