package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CH
 */
@NoArgsConstructor
@Data
public class AssessList {

    /**
     *
     */
    private Long before;
    private List<QueryDTO> query;

    @NoArgsConstructor
    @Data
    public static class QueryDTO {
        /**
         * 数据ID
         */
        private String id;
        /**
         * 礼堂ID
         */
        private String placeId;
        /**
         * 礼堂名称
         */
        private String placeName;
        /**
         * 分类ID
         */
        private String catId;
        /**
         * 分类名称
         */
        private String catName;
        /**
         * 内容
         */
        private String contents;
        /**
         * 图片地址
         */
        private String imageUrls;
        /**
         * 图片集合
         */
        private List<String> images;
        /**
         * 得分
         */
        private Integer score;
        private String created;
        private VideoDTO video;

        @NoArgsConstructor
        @Data
        public static class VideoDTO {
            /**
             * 应用ID
             */
            private String appId;
            /**
             * 视频id
             */
            private String fileId;
            /**
             * 视频url
             */
            private String fileUrl;
            /**
             * 封面url
             */
            private String coverUrl;
        }
    }
}
