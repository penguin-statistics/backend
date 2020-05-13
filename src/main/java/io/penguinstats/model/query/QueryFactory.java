package io.penguinstats.model.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.QueryType;
import io.penguinstats.service.ItemDropService;

@Component("queryFactory")
public class QueryFactory {

	@Autowired
	private ItemDropService itemDropService;

	public Query getQuery(QueryType type) throws Exception {
		switch (type) {
			case MATRIX:
				return new MatrixQuery(itemDropService);
			case TREND:
				return new TrendQuery(itemDropService);
			case GLOBAL_MATRIX:
				return new GlobalMatrixQuery(itemDropService);
			case GLOBAL_TREND:
				return new GlobalTrendQuery(itemDropService);
			case ADVANCED:
				return new AdvancedQuery();
			default:
				throw new Exception("Failed to create query for " + type);
		}
	}

}
