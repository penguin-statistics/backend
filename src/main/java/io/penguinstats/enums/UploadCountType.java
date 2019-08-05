package io.penguinstats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadCountType {
    TOTAL_UPLOAD("totalUpload"),
    RELIABLE_UPLOAD("reliableUpload");

    private String name;

    @Override
    public String toString() {
        return name;
    }
}
