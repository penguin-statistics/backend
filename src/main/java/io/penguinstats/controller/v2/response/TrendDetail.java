package io.penguinstats.controller.v2.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrendDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Integer> times;
	private List<Integer> quantity;

}
