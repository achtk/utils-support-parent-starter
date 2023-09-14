package com.chua.proxy.support.context;

import com.chua.common.support.net.channel.limit.LimitConfig;
import com.chua.common.support.net.channel.mapping.MappingConfig;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import lombok.Getter;

/**
 * 代理上下文
 *
 * @author CH
 * @since 2023/09/13
 */
@Getter
public class ProxyContext extends StandardConfigureObjectContext {

    final LimitConfig limitConfig = new LimitConfig();
    final MappingConfig mappingConfig = new MappingConfig();
    static final ProxyContext INSTANCE = new ProxyContext(ConfigureContextConfiguration.builder().build());

    public ProxyContext(ConfigureContextConfiguration configuration) {
        super(configuration);
        INSTANCE.register(limitConfig);
    }


    public static ProxyContext getInstance() {
        return INSTANCE;
    }


}
