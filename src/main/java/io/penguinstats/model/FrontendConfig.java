package io.penguinstats.model;

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
@Document(collection = "frontend_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The model of frontend config key value pair.")
public class FrontendConfig {

    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    private ObjectId id;

    @Indexed
    private String key;

    private String value;

}
