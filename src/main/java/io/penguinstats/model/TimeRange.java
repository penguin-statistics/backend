package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "time_range")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeRange implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String rangeID;
	private Long start;
	private Long end;
	private String comment;
	private Boolean accumulatable;

	public TimeRange(String rangeID, Long start, Long end, String comment, Boolean accumulatable) {
		this.rangeID = rangeID;
		this.start = start == null ? 0L : start;
		this.end = end;
		this.comment = comment;
		this.accumulatable = accumulatable;
	}

	@JsonIgnore
	@Override
	public String toString() {
		return "[" + this.start + ", " + (this.end == null ? "null" : this.end) + ")";
	}

	@JsonIgnore
	public boolean isInclude(TimeRange range) {
		if (this.start.compareTo(range.getStart()) > 0)
			return false;
		if (this.end == null)
			return true;
		else {
			if (range.getEnd() == null)
				return false;
			return this.getEnd().compareTo(range.getEnd()) >= 0;
		}
	}

	@JsonIgnore
	public boolean isIn(Long time) {
		if (time == null)
			return false;
		return this.start.compareTo(time) <= 0 && (this.end == null || this.end.compareTo(time) > 0);
	}

	@JsonIgnore
	public TimeRange combine(TimeRange range) {
		if (range.getStart().equals(this.end))
			return new TimeRange(null, this.start, range.getEnd(), null, true);
		if (this.start.equals(range.getEnd()))
			return new TimeRange(null, range.getStart(), this.end, null, true);
		return null;
	}

}
