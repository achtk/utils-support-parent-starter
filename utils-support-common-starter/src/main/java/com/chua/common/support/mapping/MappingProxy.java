package com.chua.common.support.mapping;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.profile.ProfileBuilder;
import com.chua.common.support.mapping.annotation.MappingType;
import com.chua.common.support.spi.ServiceProvider;

/**
 * mapping
 *
 * @author CH
 */
public class MappingProxy<T> {


    private final Class<T> target;
    private final String name;
    private final Profile profile;

    public MappingProxy(Class<T> target, Profile profile) {
        this.target = target;
        this.name = getName();
        this.profile = profile;
    }

    private String getName() {
        Spi spi = target.getDeclaredAnnotation(Spi.class);
        if(null != spi) {
            return spi.value()[0];
        }

        MappingType mappingType = target.getDeclaredAnnotation(MappingType.class);
        if(null != mappingType) {
            return mappingType.value();
        }

        Extension annotation = target.getDeclaredAnnotation(Extension.class);
        if(null != annotation) {
            return annotation.value();
        }

        return "http";
    }

    /**
     * 初始化
     *
     * @param target 目标类型
     * @param <T>    类型
     * @return 结果
     */
    public static <T> T create(Class<T> target) {
        return new MappingProxy<>(target, ProfileBuilder.newBuilder().build()).create();
    }
    /**
     * 初始化
     *
     * @param target 目标类型
     * @param profile   参数
     * @param <T>    类型
     * @return 结果
     */
    public static <T> T create(Class<T> target, Profile profile) {
        return new MappingProxy<>(target, profile).create();
    }

    /**
     * 创建
     *
     * @return 结果
     */
    private T create() {
        return ServiceProvider.of(MappingResolver.class)
                .getNewExtension(name, profile).create(target);
    }

}
