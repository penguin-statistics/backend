package io.penguinstats.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "stage_v2")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;
	private String stageType;
	@Indexed
	private String stageId;
	private String zoneId;
	private String code;
	private Integer apCost;
	private Boolean isGacha;
	private List<String> normalDrop;
	private List<String> specialDrop;
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
