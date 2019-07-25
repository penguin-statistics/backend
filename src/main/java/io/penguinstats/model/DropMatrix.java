package io.penguinstats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * DropMatrix is used to present a sparse matrix for drop records.<br>
 * <b>quantity</b> is how many times this item has dropped. <br>
 * <b>times</b> is how many times this stage has been played.
 * 
 * @author AlvISs_Reimu
 */
@Getter
@Setter
@Document(collection = "drop_matrix_v2")
public class DropMatrix {

	@Id
	@JsonIgnore
	private ObjectId id;
	private String stageId;
	private String itemId;
	private Integer quantity;
	private Integer times;

	public DropMatrix(String stageId, String itemId, Integer quantity, Integer times) {
		this.stageId = stageId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.times = times;
	}

	public void increaseTimes(int num) {
		this.times += num;
	}

	public void increaseQuantity(int num) {
		this.quantity += num;
	}

}