package io.penguinstats.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import io.penguinstats.model.Stage.StageBaseView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(StageBaseView.class)
public class Bounds implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer lower;
	private Integer upper;
	private List<Integer> exceptions;

	public Bounds() {
		this.lower = null;
		this.upper = null;
		this.exceptions = new ArrayList<>();
	}

	public Bounds(Integer lower, Integer upper) {
		this.lower = lower;
		this.upper = upper;
		this.exceptions = new ArrayList<>();
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

	@Override
	@JsonIgnore
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.lower == null ? "-inf" : this.lower).append("~").append(this.upper == null ? "inf" : this.upper);
		return sb.toString();
	}

}
