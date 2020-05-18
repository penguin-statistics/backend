package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "time_range")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model of a time range.")
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

	public TimeRange(Long start, Long end) {
		this.start = start;
		this.end = end;
	}

	@JsonIgnore
	@Override
	public String toString() {
		return "[" + this.start + ", " + (this.end == null ? "null" : this.end) + ")";
	}

	@JsonIgnore
	public boolean isInclude(TimeRange range) {
		if (this.start != null && this.start.compareTo(range.getStart()) > 0)
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
			return new TimeRange(this.start, range.getEnd());
		if (this.start.equals(range.getEnd()))
			return new TimeRange(range.getStart(), this.end);
		return null;
	}

	@JsonIgnore
	public TimeRange intersection(TimeRange range) {
		Long start1 = this.start == null ? Long.MIN_VALUE : this.start;
		Long end1 = this.end == null ? Long.MAX_VALUE : this.end;
		Long start2 = range.getStart() == null ? Long.MIN_VALUE : range.getStart();
		Long end2 = range.getEnd() == null ? Long.MAX_VALUE : range.getEnd();
		Long newStart = start1.compareTo(start2) <= 0 ? start2 : start1;
		Long newEnd = end1.compareTo(end2) >= 0 ? end2 : end1;
		if (newStart.compareTo(newEnd) >= 0)
			return null;
		return new TimeRange(newStart.equals(Long.MIN_VALUE) ? null : newStart,
				newEnd.equals(Long.MAX_VALUE) ? null : newEnd);
	}

}
