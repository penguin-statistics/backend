package io.penguinstats.model.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.penguinstats.model.MatrixElement;
import io.penguinstats.service.DropMatrixElementService;
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

	public TrendQuery(DropMatrixElementService dropMatrixElementService) {
		this.dropMatrixElementService = dropMatrixElementService;
	}

	@JsonIgnore
	private DropMatrixElementService dropMatrixElementService;

	private Server server;

	private String stageId;

	private List<String> itemIds;

	private Long start;

	private Long end;

	private String userID;

	private Long interval;

	private Integer timeout;

	@Override
	public List<? extends MatrixElement> execute() throws Exception {
		return QueryUtil.runQuery(
				() -> dropMatrixElementService.generateCustomDropMatrixElements(server, stageId, itemIds, start, end,
						Optional.ofNullable(userID).map(userID -> Arrays.asList(userID)).orElse(null), interval),
				timeout);
	}

}
