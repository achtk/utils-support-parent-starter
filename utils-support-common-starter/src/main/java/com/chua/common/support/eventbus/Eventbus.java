package com.chua.common.support.eventbus;

import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.environment.EnvironmentProvider;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.log.Log;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.spi.ServiceFactory;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.value.DelegateValue;
import com.chua.common.support.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 时间总线
 * @author CH
 */
public class Eventbus implements ServiceFactory<EventbusHandler>, InitializingAware {

    private static final Log log = Log.getLogger(Eventbus.class);

    private Profile profile;

    public Eventbus() {
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, Class<EventbusHandler>> listType = listType();
        for (Map.Entry<String, Class<EventbusHandler>> entry : listType.entrySet()) {
            String key = entry.getKey();
            Class<EventbusHandler> value = entry.getValue();
            Value<EventbusHandler> objectValue = Reflect.create(value).getObjectValue(profile.bind(key.toLowerCase(), HashMap.class));
            if(objectValue.isNull()) {
                log.warn("{}总线初始化失败: {}", key, objectValue.getThrowable());
                continue;
            }

            EnvironmentProvider environmentProvider = new EnvironmentProvider(profile);
            environmentProvider.refresh(objectValue.getValue());
        }
    }
}
