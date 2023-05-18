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
public class ArticleDetail {
    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 来源
     */
    private String source;
    /**
     * 栏目名
     */
    private String catName;
    /**
     * 导读内容或核心提示
     */
    private String excerpt;
    /**
     * 阅读
     */
    private Integer hits;
    /**
     * 阅读信息
     */
    private String readText;
    /**
     * 正文
     */
    private String contents;
    /**
     * 外部链接，此值不为空时， 则直接加载此url网页
     */
    private String webLink;
    /**
     * 评论是否启用
     */
    private Boolean commentEnable;
    /**
     * 评论数
     */
    private Integer commentCount;
    /**
     * 是否为首页推荐
     */
    private Boolean recommend;
    /**
     * 创建或更新时间
     */
    private Long created;
    /**
     * 格式化后的创建或更新时间
     */
    private String createdText;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 当前用户是否已点赞
     */
    private Boolean liked;
    private String coverUrl;
    private Integer htmlType;
    private ShareDTO share;
    /**
     * 版权
     */
    private String copyright;
    private List<String> jsUrls;
    private List<String> cssUrls;
    private String jsExUrl;
    /**
     * 版本号
     */
    private String version;
    /**
     * 模板ID
     */
    private String templateId;
    /**
     * 模板
     */
    private List<TemplatesDTO> templates;
    private String header;
    private String other;
    private Boolean hide;

    @NoArgsConstructor
    @Data
    public static class ShareDTO {
        private String url;
        private String title;
        private String content;
        private String thumbUrl;
    }

    @NoArgsConstructor
    @Data
    public static class TemplatesDTO {
        private String id;
        private String content;
    }
}
