package com.chua.common.support.lang.mail;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.net.URL;

/**
 * 附件
 * @author CH
 */
@Data
@Builder
public class EmailAttachment {
    /**
     * 描述
     */
    private String desc;
    /**
     * 附件
     */
    private URL url;
}
