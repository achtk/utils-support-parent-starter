package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物联设备信息
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class IotPushLastest {

    private Long after;
    private Long size;
    private Long count;
    private List<QueryDTO> query;

    @NoArgsConstructor
    @Data
    public static class QueryDTO {
        private String id;
        private String title;
        private String topicId;
        private Long topicType;
        private Long subType;
        private Long created;
    }
}
