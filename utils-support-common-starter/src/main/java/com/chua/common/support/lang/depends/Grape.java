package com.chua.common.support.lang.depends;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.File;

/**
 * grape
 *
 * @author CH
 */
@AllArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
public class Grape {

    private final Object args;

    /**
     * 调用
     *
     * @param dependencyPath 依赖存放位置
     * @param parent         类加载器
     * @return 环绕
     */
    public Surroundings execute(String dependencyPath, ClassLoader parent) {
        if (args instanceof File) {
            GrapeZip grapeZip = new GrapeZip((File) args, dependencyPath, parent);
            return grapeZip.execute();
        }

        if (args instanceof Class) {
            return new GrapeType((Class<?>) args).execute();
        }

        throw new UnsupportedOperationException();
    }
}
