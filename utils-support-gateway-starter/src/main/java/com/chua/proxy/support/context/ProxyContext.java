package com.chua.proxy.support.context;

import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 代理上下文
 *
 * @author CH
 * @since 2023/09/13
 */
@Getter
public class ProxyContext extends StandardConfigureObjectContext {

    static final ProxyContext INSTANCE = new ProxyContext(ConfigureContextConfiguration.builder().build());

    private static final AtomicBoolean STATE = new AtomicBoolean(false);

    public ProxyContext(ConfigureContextConfiguration configuration) {
        super(configuration);
    }


    public static ProxyContext getInstance() {
        return INSTANCE;
    }


}
