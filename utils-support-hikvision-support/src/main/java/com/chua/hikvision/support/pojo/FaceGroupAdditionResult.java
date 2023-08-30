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
public class FaceGroupAdditionResult {

    /**
     * 人脸分组的唯一标识,由人脸监控应用服务生成
     */
    @JsonProperty("indexCode")
    private String indexCode;
    /**
     * 人脸分组的名称,人脸分组的名称是唯一的
     */
    @JsonProperty("name")
    private String name;
    /**
     * 人脸分组的描述
     */
    @JsonProperty("description")
    private String description;
}
