package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pattern_matrix_element")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for the element in pattern matrix.")
public class PatternMatrixElement implements MatrixElement, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	@Indexed
	private String stageId;

	private DropPattern pattern;

	@ApiModelProperty(notes = "The number of times this pattern has dropped")
	private Integer quantity;

	@ApiModelProperty(notes = "The number of times this stage has been played")
	private Integer times;

	@ApiModelProperty(notes = "The left end of the interval used in the calculation")
	private Long start;

	@ApiModelProperty(notes = "The right end of the interval used in the calculation")
	private Long end;

	@Indexed
	private Server server;

	private Long updateTime;

	@JsonIgnore
	public PatternMatrixElement toResultView() {
		this.server = null;
		this.updateTime = null;
		return this;
	}

}
