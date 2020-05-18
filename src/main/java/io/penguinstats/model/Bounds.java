package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model for report quantity interval.")
public class Bounds implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "Left end of the interval. Inclusive.")
	private Integer lower;

	@ApiModelProperty(notes = "Right end of the interval. Exclusive.")
	private Integer upper;

	@ApiModelProperty(notes = "Numbers that are not allowed in the interval.")
	private List<Integer> exceptions;

	public Bounds(Integer lower, Integer upper) {
		this.lower = lower;
		this.upper = upper;
		this.exceptions = null;
	}

	@JsonIgnore
	public boolean isValid(int num) {
		if (this.lower != null && num < this.lower)
			return false;
		if (this.upper != null && num > this.upper)
			return false;
		if (this.exceptions != null) {
			for (Integer ex : this.exceptions) {
				if (num == ex)
					return false;
			}
		}
		return true;
	}

	@JsonIgnore
	public Bounds simpleCombine(Bounds b) {
		return new Bounds(this.getLower() == null ? null : this.getLower() + b.getLower(),
				this.getUpper() == null ? null : this.getUpper() + b.getUpper());
	}

}
