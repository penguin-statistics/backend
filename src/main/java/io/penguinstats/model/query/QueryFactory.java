package io.penguinstats.model.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.QueryType;
import io.penguinstats.service.DropMatrixElementService;
import io.penguinstats.service.PatternMatrixElementService;

@Component("queryFactory")
public class QueryFactory {

	@Autowired
	private DropMatrixElementService dropMatrixElementService;
	@Autowired
	private PatternMatrixElementService patternMatrixElementService;

	public BasicQuery getQuery(QueryType type) throws Exception {
		switch (type) {
			case MATRIX:
				return new MatrixQuery(dropMatrixElementService);
			case TREND:
				return new TrendQuery(dropMatrixElementService);
			case GLOBAL_MATRIX:
				return new GlobalMatrixQuery(dropMatrixElementService);
			case GLOBAL_TREND:
				return new GlobalTrendQuery(dropMatrixElementService);
			case GLOBAL_PATTERN:
				return new GlobalPatternQuery(patternMatrixElementService);
			default:
				throw new Exception("Failed to create query for " + type);
		}
	}

}
