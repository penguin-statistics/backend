package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
