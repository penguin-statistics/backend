package io.penguinstats.model.query;

import java.io.Serializable;
import java.util.List;

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
public class GlobalTrendQuery implements Serializable, BasicQuery {

	private static final long serialVersionUID = 1L;

	public GlobalTrendQuery(DropMatrixElementService dropMatrixElementService) {
		this.dropMatrixElementService = dropMatrixElementService;
	}

	@JsonIgnore
	private DropMatrixElementService dropMatrixElementService;

	private Server server;

	private Long interval;

	private Long range;

	private Integer timeout;

	@Override
	public List<? extends MatrixElement> execute() throws Exception {
		return QueryUtil.runQuery(
				() -> dropMatrixElementService.generateSegmentedGlobalDropMatrixElements(server, interval, range),
				timeout);
	}

}