package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatrixResult implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("matrix")
	private List<DropMatrixElement> elements;

}
