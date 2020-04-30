package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.DropType;
import io.penguinstats.enums.Server;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "drop_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DropInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private Server server;
	@Indexed
	private String stageId;
	@Indexed
	private String itemId;
	private DropType dropType;
	private String timeRangeID;
	private Bounds bounds;

	public DropInfo(Server server, String stageId, String itemId, DropType dropType, String timeRangeID,
			Bounds bounds) {
		this.server = server;
		this.stageId = stageId;
		this.itemId = itemId;
		this.dropType = dropType;
		this.timeRangeID = timeRangeID;
		this.bounds = bounds;
	}

}
