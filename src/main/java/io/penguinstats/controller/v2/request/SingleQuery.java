package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleQuery {

	private Server server;

	@NotBlank
	private String stageId;

	private List<String> itemIds;

	private Long start;

	private Long end;

	private Integer interval;

	private Boolean isPersonal;

}
