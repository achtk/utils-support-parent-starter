package com.chua.hikvision.support.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人脸分组
 * @author CH
 */
@NoArgsConstructor
@Data
public class FaceGroupSearchResult {

    @JsonProperty("indexCode")
    private String indexCode;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
}
