package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for the drop pattern.")
public class DropPattern implements Serializable {

	private static final long serialVersionUID = 1L;

	List<Drop> drops;

}
