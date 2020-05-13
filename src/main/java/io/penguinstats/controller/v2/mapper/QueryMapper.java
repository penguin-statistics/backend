package io.penguinstats.controller.v2.mapper;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.controller.v2.request.SingleQuery;
import io.penguinstats.controller.v2.response.BasicQueryResponse;
import io.penguinstats.controller.v2.response.MatrixQueryResponse;
import io.penguinstats.controller.v2.response.TrendQueryResponse;
import io.penguinstats.enums.QueryType;
import io.penguinstats.model.DropMatrixElement;
import io.penguinstats.model.query.BasicQuery;
import io.penguinstats.model.query.MatrixQuery;
import io.penguinstats.model.query.QueryFactory;
import io.penguinstats.model.query.TrendQuery;

@Component("queryMapper")
public class QueryMapper {

	private static Logger logger = LogManager.getLogger(QueryMapper.class);

	@Autowired
	private QueryFactory queryFactory;

	public BasicQuery queryRequestToQueryModel(SingleQuery singleQuery, String userID, Integer timeout) {
		try {
			if (singleQuery.getInterval() == null) {
				MatrixQuery query = (MatrixQuery)queryFactory.getQuery(QueryType.MATRIX);
				return query.setServer(singleQuery.getServer()).setStageId(singleQuery.getStageId())
						.setItemIds(singleQuery.getItemIds()).setStart(singleQuery.getStart())
						.setEnd(singleQuery.getEnd()).setUserID(userID).setTimeout(timeout);
			} else {
				TrendQuery query = (TrendQuery)queryFactory.getQuery(QueryType.TREND);
				return query.setServer(singleQuery.getServer()).setStageId(singleQuery.getStageId())
						.setItemIds(singleQuery.getItemIds()).setStart(singleQuery.getStart())
						.setEnd(singleQuery.getEnd()).setUserID(userID).setInterval(singleQuery.getInterval())
						.setTimeout(timeout);
			}
		} catch (Exception e) {
			logger.error("Failed to map QueryRequest to Query", e);
			return null;
		}
	}

	public BasicQueryResponse elementsToBasicQueryResponse(SingleQuery singleQuery, List<DropMatrixElement> elements) {
		return singleQuery.getInterval() == null ? new MatrixQueryResponse(elements) : new TrendQueryResponse(elements);
	}

}
