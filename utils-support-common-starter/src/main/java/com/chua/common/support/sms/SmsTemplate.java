package com.chua.common.support.sms;

import lombok.*;

import java.util.HashMap;
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
    private Map<String, String> templateParam;
    private List<String> phoneNumbers;

    public static class Builder {
        /**
         * 添加短信模板参数.
         *
         * @param key   the key
         * @param value the value
         * @return this
         */
        public Builder addTemplateParam(final String key, final String value) {
            if (null == this.templateParam) {
                this.templateParam = new HashMap<>(3);
            }

            this.templateParam.put(key, value);
            return this;
        }
    }
}
