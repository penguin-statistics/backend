package io.penguinstats.model;

import java.io.Serializable;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.penguinstats.enums.Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "event_period")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventPeriod implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	private Long start;

	private Long end;

	@JsonProperty("label_i18n")
	private Map<String, String> labelMap;

	private ExistConditions conditions;

	private Map<Server, Existence> existence;

}
