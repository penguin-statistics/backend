package io.penguinstats.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.penguinstats.enums.Server;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "item_drop_v2")
@ApiModel(description = "The model of a drop sample.")
@CompoundIndexes({@CompoundIndex(def = "{'stageId':1}", name = "stageId"),
        @CompoundIndex(def = "{'server':1}", name = "server"),
        @CompoundIndex(def = "{'timestamp':1}", name = "timestamp"),
        @CompoundIndex(def = "{'isReliable':1}", name = "isReliable"),
        @CompoundIndex(def = "{'isDeleted':1}", name = "isDeleted"),
        @CompoundIndex(def = "{'userID':1}", name = "userID"),
        @CompoundIndex(def = "{'screenshotMetadata':1}", name = "screenshotMetadata"),
        @CompoundIndex(def = "{'screenshotMetadata.md5':1}", name = "screenshotMetadata_md5")})
public class ItemDrop {

    @Id
    private ObjectId id;
    private String stageId;
    private Server server;
    private Integer times;
    private List<Drop> drops;
    private Long timestamp;
    private String ip;
    private Boolean isReliable;
    private Boolean isDeleted = false;
    private String source;
    private String version;
    private String userID;
    private ScreenshotMetadata screenshotMetadata;

    @JsonIgnore
    public int getDropQuantity(String itemId) {
        for (Drop drop : this.drops) {
            if (drop.getItemId().equals(itemId)) {
                return drop.getQuantity();
            }
        }
        return 0;
    }

    @JsonIgnore
    public ItemDrop toNoIDView() {
        this.id = null;
        return this;
    }

}
