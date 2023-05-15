package com.chua.common.support.spi.autowire;

/**
 * IOC
 * @author CH
 */
public class AutoServiceAutowire implements ServiceAutowire{

    public static final ServiceAutowire INSTANCE = new AutoServiceAutowire();

    @Override
    public Object autowire(Object object) {
        if(null == object) {
            return null;
        }
    }
}
