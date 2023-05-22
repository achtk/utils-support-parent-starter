package com.chua.common.support.sms;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 阿里云 SMS 短信模板.
 *
 * @author cn-src
 */
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsTemplate {
    private String signName;
    private String templateCode;
    @Singular("addTemplateParam")
    private Map<String, String> templateParam;
    private List<String> phoneNumbers;

}
