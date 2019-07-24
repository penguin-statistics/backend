package io.penguinstats.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String userID;
	private Double weight;
	private List<String> tags;
	private List<String> ips;
	private String comment;
	private Long createTime;

	public User(String userID, Double weight, List<String> tags, List<String> ips, String comment, Long createTime) {
		this.userID = userID;
		this.weight = weight;
		this.tags = tags;
		this.ips = ips;
		this.comment = comment;
		this.createTime = createTime;
	}

}
