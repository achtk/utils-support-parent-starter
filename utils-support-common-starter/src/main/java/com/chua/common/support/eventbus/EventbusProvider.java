package com.chua.common.support.eventbus;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.environment.EnvironmentProvider;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.log.Log;
import com.chua.common.support.spi.ServiceFactory;
import com.chua.common.support.utils.IoUtils;
import lombok.Builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间总线
 * @author CH
 */
@Builder
public class EventbusProvider implements ServiceFactory<Eventbus>, InitializingAware, AutoCloseable{

    private static final Log log = Log.getLogger(EventbusProvider.class);

    private Profile profile;

    private static final Map<String, Eventbus> EVENTBUS_MAP = new ConcurrentHashMap<>();
    public EventbusProvider() {
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        EnvironmentProvider environmentProvider = new EnvironmentProvider(profile);
        Map<String, Eventbus> list = list();
        for (Map.Entry<String, Eventbus> entry : list.entrySet()) {
            Eventbus eventbus = entry.getValue();
            EVENTBUS_MAP.put(eventbus.event().name(), eventbus);
            environmentProvider.refresh(eventbus);
        }
    }

    @Override
    public void close() throws Exception {
        for (Eventbus eventbus : EVENTBUS_MAP.values()) {
            IoUtils.closeQuietly(eventbus);
        }
    }
}
