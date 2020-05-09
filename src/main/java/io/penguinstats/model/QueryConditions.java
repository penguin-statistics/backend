package io.penguinstats.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryConditions {

	private List<StageWithTimeRange> stages;
	private List<String> itemIds;
	private List<Server> servers;
	private List<String> userIDs;
	private Long interval;

	public QueryConditions() {
		this.stages = new ArrayList<>();
		this.itemIds = new ArrayList<>();
		this.servers = new ArrayList<>();
		this.userIDs = new ArrayList<>();
		this.interval = null;
	}

	public QueryConditions addStage(String stageId, Long start, Long end) {
		this.stages.add(new StageWithTimeRange(stageId, start, end));
		return this;
	}

	public QueryConditions addItemId(String itemId) {
		this.itemIds.add(itemId);
		return this;
	}

	public QueryConditions addServer(Server server) {
		this.servers.add(server);
		return this;
	}

	public QueryConditions addUserID(String userID) {
		this.userIDs.add(userID);
		return this;
	}

	public QueryConditions setInterval(int days) {
		this.interval = TimeUnit.DAYS.toMillis(days);
		return this;
	}

	@JsonIgnore
	@Override
	public String toString() {
		return "QueryConditions [stages=" + stages + ", itemIds=" + itemIds + ", servers=" + servers + ", userIDs="
				+ userIDs + ", interval=" + interval + "]";
	}

	@Getter
	@Setter
	public static class StageWithTimeRange {

		private String stageId;
		private Long start;
		private Long end;

		public StageWithTimeRange(String stageId, Long start, Long end) {
			this.stageId = stageId;
			this.start = start;
			this.end = end;
		}

		@JsonIgnore
		@Override
		public String toString() {
			return stageId + ": " + start + "-" + end;
		}

	}

}