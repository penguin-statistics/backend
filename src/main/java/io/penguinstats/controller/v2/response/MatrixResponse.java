package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.penguinstats.model.DropMatrixElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatrixResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("matrix")
	private List<DropMatrixElement> elements;

}
