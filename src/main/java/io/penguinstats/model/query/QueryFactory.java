package io.penguinstats.model.query;

import io.penguinstats.enums.QueryType;
import io.penguinstats.service.ItemDropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("queryFactory")
public class QueryFactory {

	@Autowired
	private ItemDropService itemDropService;

	public BasicQuery getQuery(QueryType type) throws Exception {
		switch (type) {
			case MATRIX:
				return new MatrixQuery(itemDropService);
			case TREND:
				return new TrendQuery(itemDropService);
			case GLOBAL_MATRIX:
				return new GlobalMatrixQuery(itemDropService);
			case GLOBAL_TREND:
				return new GlobalTrendQuery(itemDropService);
			default:
				throw new Exception("Failed to create query for " + type);
		}
	}

}
