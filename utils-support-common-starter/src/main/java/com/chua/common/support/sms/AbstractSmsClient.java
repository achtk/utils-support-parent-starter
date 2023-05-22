package com.chua.common.support.sms;

import com.chua.common.support.utils.Preconditions;

import java.util.Collections;
import java.util.Map;

/**
 * sms client
 *
 * @author CH
 */
public abstract class AbstractSmsClient<Profile> implements SmsClient {


    protected final Map<String, SmsTemplate> smsTemplates;
    protected Profile profile;

    /**
     * Instantiates a new SmsClient.
     *
     * @param accessKeyId     短信 accessKeyId
     * @param accessKeySecret 短信 accessKeySecret
     * @param profile         额外参数
     */
    public AbstractSmsClient(final String accessKeyId, final String accessKeySecret, final Profile profile) {
        this(accessKeyId, accessKeySecret, Collections.emptyMap(), profile);
    }

    /**
     * Instantiates a new SmsClient.
     *
     * @param accessKeyId     阿里云短信 accessKeyId
     * @param accessKeySecret 阿里云短信 accessKeySecret
     * @param smsTemplates    预置短信模板
     */
    public AbstractSmsClient(final String accessKeyId,
                             final String accessKeySecret,
                             final Map<String, SmsTemplate> smsTemplates, final Profile profile) {
        this.profile = profile;
        Preconditions.checkNotNull(accessKeyId, "'accessKeyId' must be not empty");
        Preconditions.checkNotNull(accessKeySecret, "'accessKeySecret' must be not empty");


        this.smsTemplates = smsTemplates;
    }

}
