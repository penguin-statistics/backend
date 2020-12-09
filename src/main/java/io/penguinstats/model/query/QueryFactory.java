package io.penguinstats.model.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.QueryType;
import io.penguinstats.service.DropMatrixElementService;

@Component("queryFactory")
public class QueryFactory {

	@Autowired
	private DropMatrixElementService dropMatrixElementService;

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
			default:
				throw new Exception("Failed to create query for " + type);
		}
	}

}
