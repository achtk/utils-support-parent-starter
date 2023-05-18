package com.chua.common.support.geo;

import com.chua.common.support.lang.profile.ProfileProvider;
import com.chua.common.support.spi.ServiceProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * geo构建器
 *
 * @author CH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpBuilder {

    private IpPosition ipPosition;
    private final Map<String, Object> environment = new HashMap<>();

    /**
     * 环境变量
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    public IpBuilder environment(String name, Object value) {
        environment.put(name, value);
        return this;
    }

    /**
     * 环境变量
     *
     * @param value 值
     * @return this
     */
    public IpBuilder database(Object value) {
        return environment("database", value);
    }

    /**
     * 构建
     *
     * @param type 类型
     * @return 定位
     */
    public IpPosition build(String type) {
        IpPosition position = ServiceProvider.of(IpPosition.class).getExtension(com.chua.common.support.utils.StringUtils.defaultString(type, "lite2"));
        if(position instanceof ProfileProvider) {
            ((ProfileProvider<?>) position).addProfile(environment);
        }
        try {
            position.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    /**
     * 构建
     *
     * @return 定位
     */
    public IpPosition build() {
        return build("lite2");
    }

    /**
     * 构建器
     *
     * @return this
     */
    public static IpBuilder newBuilder() {
        return new IpBuilder();
    }
}
