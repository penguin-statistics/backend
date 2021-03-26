package io.penguinstats.model;

import java.io.Serializable;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

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
@org.springframework.data.mongodb.core.mapping.Document(collection = "outlier")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "The abnormal screenshot uploaded.")
public class Outlier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    private ObjectId id;

    private Document metadata;

    private String bucket;

    @ApiModelProperty(notes = "The path of the file.")
    private String url;

    @ApiModelProperty(notes = "The upload time of the file. Time unit is millisecond.")
    private Long time;

    private String userID;

    public Outlier(Document metadata, String bucket, String userID) {
        this.metadata = metadata;
        this.bucket = bucket;
        this.url = null;
        this.time = null;
        this.userID = userID;
    }

    public void setMetadata(Document metadata) {
        this.metadata = metadata;
    }

    public Document getMetadata() {
        return this.metadata;
    }

}
