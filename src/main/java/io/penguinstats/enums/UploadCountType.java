package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadCountType {
    TOTAL_UPLOAD("total","totalUpload"),
    RELIABLE_UPLOAD("reliable", "reliableUpload");

    private String type;
    private String name;
}
