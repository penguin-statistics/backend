package io.penguinstats.model;

import java.io.Serializable;
import java.util.List;

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
@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model of a user.")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private Integer totalUpload = 0;
	private Integer reliableUpload = 0;

	@JsonIgnore
	public boolean containsIp(String ip) {
		if (this.ips == null)
			return false;
		return ips.contains(ip);
	}

}
