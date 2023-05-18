package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 指数排名
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class AssessRanking {

    /**
     * 总数
     */
    private Integer totalCount;
    /**
     * 开始时间
     */
    private Long startDate;
    /**
     * 结束时间
     */
    private Long endDate;
    /**
     * 标签
     */
    private List<HeadersDTO> headers;
    private List<QueryDTO> query;

    @NoArgsConstructor
    @Data
    public static class HeadersDTO {
        /**
         * 乡镇ID
         */
        private Integer tabId;
        /**
         * 镇（街道）名称
         */
        private String title;
        /**
         * 礼堂数量
         */
        private Integer count;
        /**
         * 测评总分
         */
        private Integer score;
    }

    @NoArgsConstructor
    @Data
    public static class QueryDTO {
        /**
         * 数据ID
         */
        private String id;
        /**
         * 名次
         */
        private Integer order;
        /**
         * 礼堂名称
         */
        private String name;
        /**
         * 图片地址
         */
        private String imageUrl;
        /**
         * 得分
         */
        private Integer score;
        /**
         * 乡镇数据ID
         */
        private Integer tabId;
        private Integer townSort;
        private String townName;
    }
}
