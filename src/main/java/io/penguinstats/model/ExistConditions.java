package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model of conditions to control show/hide in the front-end")
public class ExistConditions implements Serializable {

	private static final long serialVersionUID = 1L;

	List<Server> servers;
	Long start;
	Long end;

}
