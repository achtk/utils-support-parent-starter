package com.chua.common.support.lang.mail;

import com.chua.common.support.utils.CollectionUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 邮件实体
 */
@Data
@Builder
public class Mail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 邮件标题
     */
    private String title;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 内容格式(默认html)
     */
    private String contentType;
    /**
     * 接收邮件地址
     */
    private String to;


    @Singular("attachment")
    private List<EmailAttachment> attachment;

    //*************模板发送****************/
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板变量替换
     */
    private Map<String, Object> maps;

    /**
     * 是否有附件
     * @return 是否有附件
     */
    public boolean hasAttach() {
        return CollectionUtils.isNotEmpty(attachment);
    }
}
