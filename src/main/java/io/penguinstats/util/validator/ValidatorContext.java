package io.penguinstats.util.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;
import io.penguinstats.model.TypedDrop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Component
public class ValidatorContext {

	private String stageId;
	private Server server;
	private Integer times;
	private List<TypedDrop> drops;
	private Long timestamp;
	private String ip;
	private String userID;

}
