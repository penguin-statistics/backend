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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "drop_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(
		description = "The drop information for a specific item, in one stage, in one server and in a certain time range.")
public class DropInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	@Indexed
	private Server server;

	@Indexed
	private String stageId;

	@ApiModelProperty(
			notes = "If itemId is null, the `bounds` property means the limitation in the number of different item kinds in a certain drop type.")
	@Indexed
	private String itemId;

	private DropType dropType;

	private String timeRangeID;

	private Bounds bounds;

	@ApiModelProperty(
			notes = "If one dropInfo is accumulatable, it means the drop data (quantity and times) of this item in the stage can be accumulated with future time ranges."
					+ "For example, item ap_supply_lt_010 in stage main_01-07 has several drop infos under 3 time ranges A, B and C."
					+ "If `accumulatable` for A is false while for B and C are true, then we say the \"latest max accumulatable time ranges are B~C.\"")
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
