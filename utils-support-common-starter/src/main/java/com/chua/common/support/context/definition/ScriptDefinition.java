package com.chua.common.support.context.definition;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.expression.ExpressionProvider;
import com.chua.common.support.utils.FileUtils;

import java.io.File;

/**
 * 脚本定义
 *
 * @author CH
 */
public class ScriptDefinition<T> extends ClassDefinition<T> implements TypeDefinition<T>, InitializingAware {
    private final String script;
    private final String suffix;

    /**
     * 初始化
     *
     * @param script      脚本文件
     * @param suffix      后缀
     * @param classLoader 类加载器
     * @param type        类型
     */
    public ScriptDefinition(String script, String suffix, ClassLoader classLoader, Class<T> type) {
        super(type);
        this.script = script;
        this.suffix = suffix;
        single(true);
        this.isLoaded = true;
        ExpressionProvider provider = ExpressionProvider.newBuilder(suffix).script(script).classLoader(classLoader).build();
        this.object = provider.create(type);
    }

    /**
     * 初始化
     *
     * @param script      脚本
     * @param classLoader 类加载器
     * @param type        类型
     */
    public ScriptDefinition(File script, ClassLoader classLoader, Class<T> type) {
        this(script.getAbsolutePath(), FileUtils.getExtension(script), classLoader, type);
    }

    /**
     * 初始化
     *
     * @param script 脚本
     * @param type   类型
     */
    public ScriptDefinition(File script, Class<T> type) {
        this(script.getAbsolutePath(), FileUtils.getExtension(script), type.getClassLoader(), type);
    }

    /**
     * 初始化
     *
     * @param script      脚本
     * @param classLoader 类加载器
     * @param type        类型
     */
    public ScriptDefinition(String script, ClassLoader classLoader, Class<T> type) {
        this(script, FileUtils.getExtension(script), classLoader, type);
    }

    /**
     * 初始化
     *
     * @param script 脚本
     * @param type   类型
     */
    public ScriptDefinition(String script, Class<T> type) {
        this(script, FileUtils.getExtension(script), type.getClassLoader(), type);
    }


    @Override
    public T getObject(Object... args) {
        return object;
    }

}
