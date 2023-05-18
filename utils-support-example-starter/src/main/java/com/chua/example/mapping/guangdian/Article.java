package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class Article {

    /**
     * 记录总数
     */
    private Integer totalCount;
    private List<QueryDTO> query;

    @NoArgsConstructor
    @Data
    public static class QueryDTO {
        /**
         * 文章ID
         */
        private String id;
        /**
         * 文章标题
         */
        private String title;
        /**
         * 接录
         */
        private String extract;
        private String catId;
        /**
         * 栏目名称
         */
        private String catName;
        /**
         * 图片地址
         */
        private String coverUrl;
        /**
         * 创建时间
         */
        private String created;
        /**
         * 文章列表项布局视图类型
         */
        private Integer viewType;
    }
}
