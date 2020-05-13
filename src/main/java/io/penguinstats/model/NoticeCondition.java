package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeCondition implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> locale;
	private Long start;
	private Long end;

}
