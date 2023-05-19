package com.chua.digest.support.environment;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.spi.ServiceEnvironment;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * 初始化环境
 *
 * @author CH
 */
public class DigestEnvironment implements ServiceEnvironment {
    @Override
    public void afterPropertiesSet() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
