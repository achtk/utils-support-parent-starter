package com.chua.example.mapping.guangdian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物联设备信息
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class Iot {


    private WarmDTO warm;
    private SmokeDTO smoke;
    private VideoDTO video;
    private VisitorDTO visitor;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class WarmDTO {
        private Integer onlineCount;
        private Integer totalCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SmokeDTO {
        private Integer onlineCount;
        private Integer totalCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class VideoDTO {
        private Integer onlineCount;
        private Integer totalCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class VisitorDTO {
        private Integer onlineCount;
        private Integer totalCount;
    }
}
