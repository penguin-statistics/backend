package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.DropMatrixElementType;
import io.penguinstats.enums.Server;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MatrixElement is used to present a sparse matrix for drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "drop_matrix_element")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for the element in drop matrix.")
public class DropMatrixElement implements MatrixElement, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;

	@Indexed
	private DropMatrixElementType type;

	@Indexed
	private String stageId;

	private String itemId;

	@ApiModelProperty(notes = "The number of times this item has dropped")
	private Integer quantity;

	@ApiModelProperty(notes = "The number of times this stage has been played")
	private Integer times;

	@ApiModelProperty(notes = "The left end of the interval used in the calculation")
	private Long start;

	@ApiModelProperty(notes = "The right end of the interval used in the calculation")
	private Long end;

	@Indexed
	private Server server;

	@Indexed
	private Boolean isPast;

	private Long updateTime;

	public DropMatrixElement(DropMatrixElementType type, String stageId, String itemId, Integer quantity, Integer times,
			Long start, Long end, Server server, Boolean isPast, Long updateTime) {
		this.type = type;
		this.stageId = stageId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.times = times;
		this.start = start;
		this.end = end;
		this.server = server;
		this.isPast = isPast;
		this.updateTime = updateTime;
	}

	@JsonIgnore
	public DropMatrixElement toResultView() {
		this.type = null;
		this.server = null;
		this.isPast = null;
		this.updateTime = null;
		return this;
	}

}
