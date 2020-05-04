package io.penguinstats.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;

import io.penguinstats.model.Item.ItemBaseView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonView(ItemBaseView.class)
public class ItemExistence implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean exist;

}
