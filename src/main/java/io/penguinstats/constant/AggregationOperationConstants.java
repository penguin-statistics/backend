package io.penguinstats.constant;

import java.util.Arrays;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.LiteralOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

/**
 * @author AlvISsReimu
 */
@Component("aggregationOperationConstants")
public class AggregationOperationConstants {

	// shared
	/**
	 *
	 */
	public final AggregationOperation MATCH_NOT_DELETED = Aggregation.match(Criteria.where("isDeleted").is(false));
	/**
	 *
	 */
	public final AggregationOperation MATCH_RELIEABLE_NOT_DELETED = Aggregation.match(
			new Criteria().andOperator(Criteria.where("isReliable").is(true), Criteria.where("isDeleted").is(false)));
	/**
	 *
	 */
	public final AggregationOperation MATCH_QUANTITY_NOT_ZERO = Aggregation.match(Criteria.where("quantity").ne(0));
	/**
	 *
	 */
	public final AggregationOperation LOOKUP_USER = Aggregation.lookup("user", "_id", "userID", "user");
	/**
	 *
	 */
	public final AggregationOperation PROJECT_WEIGHT = Aggregation.project("itemDrops")
			.and(ArrayOperators.ArrayElemAt.arrayOf("user.weight").elementAt(0)).as("weight");
	/**
	 *
	 */
	public final AggregationOperation UNWIND_ITEMDROPS = Aggregation.unwind("itemDrops", false);
	/**
	 *
	 */
	public final AggregationOperation UNWIND_ADD_TIME = Aggregation.unwind("addTime", "point", true);
	/**
	 *
	 */
	public final AggregationOperation GROUP_BY_STAGEID = Aggregation.group("stageId")
			.push(new BasicDBObject("times", "$times").append("timePoint", "$_id.point")).as("allTimes");
	/**
	 *
	 */
	public final AggregationOperation UNWIND_ITEMDROPS_DROPS = Aggregation.unwind("itemDrops.drops", false);
	/**
	 *
	 */
	public final AggregationOperation GROUP_BY_SECTION =
			Aggregation.group("section").push(new BasicDBObject("itemDrop", "$$ROOT")).as("itemDrops");
	/**
	 *
	 */
	public final AggregationOperation PROJECT_ADD_TIME_FOR_WEIGHTED_STAGE_TIMES =
			Aggregation.project("itemDrops", "weight")
					.and(LiteralOperators.Literal.asLiteral(Arrays.asList(Constant.ADD_TIME_POINTS))).as("addTime");

	/**
	 * @Description:  aggregateItemDropQuantities
	 */
	public final AggregationOperation UNWIND_DROPS = Aggregation.unwind("drops", false);
	/**
	 *
	 */
	public final AggregationOperation GROUP_BY_STAGEID_AND_ITEMID =
			Aggregation.group("stageId", "drops.itemId").sum("drops.quantity").as("quantity");

	/**
	 * @Description: aggregateStageTimes
	 */
	public final AggregationOperation PROJECT_ADD_TIME = Aggregation.project("stageId", "times", "timestamp")
			.and(LiteralOperators.Literal.asLiteral(Arrays.asList(Constant.ADD_TIME_POINTS))).as("addTime");

	public final AggregationOperation PROJECT_TIMESTAMP_GT_ADD_TIME = Aggregation.project("stageId", "point")
			.and(ConditionalOperators.when(Criteria.where("timestamp").gt("$addTime")).thenValueOf("$times")
					.otherwise(LiteralOperators.Literal.asLiteral(0)))
			.as("times");

	public final AggregationOperation GROUP_BY_STAGEID_AND_POINT =
			Aggregation.group("stageId", "point").sum("times").as("times");

	/**
	 * @Description: aggregateWeightedItemDropQuantities
	 */
	public final AggregationOperation GROUP_BY_USERID_FOR_WEIGHTED_QUANTITIES = Aggregation.group("userID")
			.push(new BasicDBObject("drops", "$$ROOT.drops").append("stageId", "$$ROOT.stageId")).as("itemDrops");

	/**
	 *
	 */
	public final AggregationOperation PROJECT_ITEMDROPS =
			Aggregation.project("weight").and("itemDrops.stageId").as("stageId").and("itemDrops.drops").as("drops");
	/**
	 *
	 */
	public final AggregationOperation GROUP_BY_STAGEID_AND_ITEMID_SUM_WEIGHTED_QUANTITY =
			Aggregation.group("stageId", "drops.itemId")
					.sum(ArithmeticOperators.Multiply.valueOf("weight").multiplyBy("drops.quantity")).as("quantity");

	/**
	 * @Description: aggregateWeightedStageTimes
	 */
	public final AggregationOperation GROUP_BY_USERID_FOR_WEIGHTED_STAGE_TIMES =
			Aggregation.group("userID").push(new BasicDBObject("times", "$$ROOT.times")
					.append("stageId", "$$ROOT.stageId").append("timestamp", "$$ROOT.timestamp")).as("itemDrops");
	/**
	 *
	 */
	public final AggregationOperation PROJECT_TIMESTAMP_GT_ADD_TIME_FOR_WEIGHTED_STAGE_TIMES =
			Aggregation.project("point", "weight").and("itemDrops.stageId").as("stageId")
					.and(ConditionalOperators.when(Criteria.where("itemDrops.timestamp").gt("$addTime"))
							.thenValueOf(LiteralOperators.Literal.asLiteral(1))
							.otherwise(LiteralOperators.Literal.asLiteral(0)))
					.as("times");
	/**
	 *
	 */
	public final AggregationOperation GROUP_BY_STAGEID_AND_POINT_SUM_WEIGHTED_TIMES =
			Aggregation.group("stageId", "point")
					.sum(ArithmeticOperators.Multiply.valueOf("weight").multiplyBy("times")).as("times");

	/**
	 *@Description:  aggregateUploadCount
	 */
	public final AggregationOperation GROUP_BY_USERID_SUM_TIMES = Aggregation.group("userID").sum("times").as("count");

}
