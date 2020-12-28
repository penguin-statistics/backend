package io.penguinstats.model.query;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.penguinstats.model.MatrixElement;
import io.penguinstats.service.PatternMatrixElementService;
import io.penguinstats.util.QueryUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalPatternQuery implements Serializable, BasicQuery {

	private static final long serialVersionUID = 1L;

	public GlobalPatternQuery(PatternMatrixElementService patternMatrixElementService) {
		this.patternMatrixElementService = patternMatrixElementService;
	}

	@JsonIgnore
	private PatternMatrixElementService patternMatrixElementService;

	private Server server;

	private String stageId;

	private List<String> itemIds;

	private Long start;

	private Long end;

	private String userID;

	private Integer timeout;

	@Override
	public List<? extends MatrixElement> execute() throws Exception {
		return QueryUtil.runQuery(() -> patternMatrixElementService.generateGlobalPatternMatrixElements(server, userID),
				timeout);
	}

}
