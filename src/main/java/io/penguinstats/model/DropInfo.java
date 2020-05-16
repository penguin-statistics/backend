package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.DropType;
import io.penguinstats.enums.Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
	private Boolean accumulatable;
	@Transient
	private TimeRange timeRange;

	@JsonIgnore
	public DropInfo toStageView() {
		this.server = null;
		this.stageId = null;
		this.timeRangeID = null;
		this.accumulatable = null;
		this.timeRange = null;
		return this;
	}

}
