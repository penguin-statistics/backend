package io.penguinstats.controller.v2.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.Server;
import io.penguinstats.model.TypedDrop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleReportRequest {

	@NotBlank
	private String stageId;

	private Server server;

	@NotNull
	private List<TypedDrop> drops;

	private String source;

	private String version;

}
