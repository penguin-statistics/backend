package io.penguinstats.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.DropType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypedDrop extends Drop {

	private static final long serialVersionUID = 1L;

	private DropType dropType;

	public TypedDrop(String itemId, Integer quantity, DropType dropType) {
		super(itemId, quantity);
		this.dropType = dropType;
	}

}
