package io.penguinstats.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "stage_v2")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stage implements Serializable {

	public interface StageBaseView {};

	public interface StageNewView extends StageBaseView {};

	public interface StageLegacyView extends StageBaseView {};

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	@JsonView(StageBaseView.class)
	private String stageType;

	@Indexed
	@JsonView(StageBaseView.class)
	private String stageId;

	@JsonView(StageBaseView.class)
	private String zoneId;

	@JsonView(StageBaseView.class)
	private String code;

	@JsonView(StageBaseView.class)
	private Integer apCost;

	@JsonView(StageBaseView.class)
	private Boolean isGacha;

	@Transient
	@JsonView(StageNewView.class)
	private List<DropInfo> dropInfos;

	@JsonView(StageLegacyView.class)
	private List<String> normalDrop;

	@JsonView(StageLegacyView.class)
	private List<String> specialDrop;

	@JsonView(StageLegacyView.class)
	private List<String> extraDrop;

	public Stage(String stageType, String stageId, String zoneId, String code, Integer apCost, Boolean isGacha,
			List<String> normalDrop, List<String> specialDrop, List<String> extraDrop) {
		this.stageType = stageType;
		this.stageId = stageId;
		this.zoneId = zoneId;
		this.code = code;
		this.apCost = apCost;
		this.isGacha = isGacha;
		this.normalDrop = normalDrop;
		this.specialDrop = specialDrop;
		this.extraDrop = extraDrop;
	}

	@JsonIgnore
	public Set<String> getDropsSet() {
		Set<String> set = new HashSet<>();
		if (this.normalDrop != null)
			set.addAll(this.normalDrop);
		if (this.specialDrop != null)
			set.addAll(this.specialDrop);
		if (this.extraDrop != null)
			set.addAll(this.extraDrop);
		set.add("furni");
		return set;
	}

}
