package io.penguinstats.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemQuantityBounds {

	private String itemId;
	private Bounds bounds;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.itemId).append(" [").append(this.bounds.toString()).append("]");
		return sb.toString();
	}

}
