package com.chua.example.mapping.guangdian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主题数据
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class Topic {


    private AssessDTO assess;
    private LikeDTO like;
    private PlaceDTO place;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class AssessDTO {
        private Integer count;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LikeDTO {
        private Integer count;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class PlaceDTO {
        private Integer count;
    }
}
