package io.penguinstats.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.penguinstats.enums.SocialPlatform;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "authorization")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model of authorization stuff.")
public class Authorization implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	private ObjectId id;
	@Indexed
	private String userID;
	@Indexed
	private SocialPlatform platform;
	@Indexed
	private String state;
	private String redirectURI;
	private String accessToken;
	private String refreshToken;
	private String platformID;
	private Long authRequestTime;
	private Long tokenGenerateTime;

}
