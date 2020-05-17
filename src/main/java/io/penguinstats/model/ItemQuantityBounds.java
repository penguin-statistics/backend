package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model of an item with its quantity limitation.")
public class ItemQuantityBounds implements Serializable {

	private static final long serialVersionUID = 1L;

	private String itemId;
	private Bounds bounds;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.itemId).append(" [").append(this.bounds.toString()).append("]");
		return sb.toString();
	}

}
