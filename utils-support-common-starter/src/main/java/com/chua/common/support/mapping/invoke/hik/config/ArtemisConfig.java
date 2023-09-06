package com.chua.common.support.mapping.invoke.hik.config;

import lombok.Data;

/**
 * artemis host、appKey、appSecret配置
 *
 * @author zhangtuo
 * @update CH
 * @Date 2017/4/26
 */
@Data
public class ArtemisConfig {
    private String host;
    private String appKey;
    private String appSecret;

}
