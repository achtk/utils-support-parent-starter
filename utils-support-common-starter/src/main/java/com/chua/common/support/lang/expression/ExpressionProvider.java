package com.chua.common.support.lang.expression;

import com.chua.common.support.annotations.SpiIgnore;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.expression.listener.FileListener;
import com.chua.common.support.lang.expression.listener.Listener;
import com.chua.common.support.lang.expression.make.ExpressionMarker;
import com.chua.common.support.lang.expression.script.DelegateScriptExpression;
import com.chua.common.support.lang.expression.source.DelegateSourceExpression;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.util.Optional;

/**
 * 表达式解析器
 * @author CH
 */
@SpiIgnore
public class ExpressionProvider implements Expression{

    private final Expression expression;

    private ExpressionProvider(Expression expression){
        this.expression = expression;
    }

    /**
     * 初始化构造器
     *
     * @param type 表达式类型
     * @return ExpressionProviderBuilder
     */
    public static ExpressionProviderBuilder newBuilder(String type) {
        File file = Converter.convertIfNecessary(type, File.class);
        if(null != file) {
            return newScript().script(file).scriptType(FileUtils.getExtension(file));
        }

        ServiceProvider<ExpressionMarker> serviceProvider = ServiceProvider.of(ExpressionMarker.class);
        if(serviceProvider.listType().containsKey(type)) {
            return newScript().scriptType(type);
        }

        return newSource();
    }
    /**
     * 初始化构造器
     *
     * @return ExpressionProviderBuilder
     */
    public static ExpressionProviderBuilder newScript() {
        return new ExpressionProviderBuilder("script");
    }
    /**
     * 初始化构造器
     *
     * @return ExpressionProviderBuilder
     */
    public static ExpressionProviderBuilder newSource() {
        return new ExpressionProviderBuilder("source");
    }

    /**
     * 是否支持文件
     * @param extension 后缀
     * @return 结果
     */
    public static boolean isMatch(String extension) {
        return ServiceProvider.of(ExpressionMarker.class).has(extension);
    }

    @Override
    public <T> T createProxy(Class<T> type) {
        return expression.createProxy(type);
    }

    @Override
    public <T> T create(Class<T> type) {
        return expression.create(type);
    }

    @Override
    public Class<?> getType() {
        return expression.getType();
    }


    public static class ExpressionProviderBuilder {

        private final String type;
        private Object[] args;
        private ClassLoader classLoader;
        private Listener listener;
        private String scriptType;
        private String source;
        private String file;

        public ExpressionProviderBuilder(String type) {
            this.type = Optional.ofNullable(type).orElse("java");
        }

        /**
         * 初始化参数
         * @param args 参数
         * @return 结果
         */
        public ExpressionProviderBuilder args(Object... args) {
            this.args = args;
            return this;
        }

        /**
         * 监听
         * @param listener 监听
         * @return this
         */
        public ExpressionProviderBuilder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 类加载器
         * @param classLoader 类加载器
         * @return this
         */
        public ExpressionProviderBuilder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }
        /**
         * 脚本
         * @param  file 脚本
         * @return this
         */
        public ExpressionProviderBuilder script(File file) {
            this.file = file.getAbsolutePath();
            return this;
        }
        /**
         * 脚本
         * @param  file 脚本
         * @return this
         */
        public ExpressionProviderBuilder script(String file) {
            return this.script(Converter.convertIfNecessary(file, File.class));
        }
        /**
         * 源码
         * @param  source 源码
         * @return this
         */
        public ExpressionProviderBuilder source(String source) {
            this.source = source;
            return this;
        }
        /**
         * 脚本类型
         * @param scriptType 脚本类型
         * @return this
         */
        public ExpressionProviderBuilder scriptType(String scriptType) {
            this.scriptType = scriptType;
            return this;
        }
        /**
         * 构建
         *
         * @return 构建
         */
        public ExpressionProvider build() {
            return new ExpressionProvider(
                    "script".equalsIgnoreCase(type) ?
                    new DelegateScriptExpression(new File(file), classLoader, null == listener ? new FileListener(file) : listener, args):
                    new DelegateSourceExpression(source, scriptType, classLoader, listener, args)
            );
        }
    }

}
