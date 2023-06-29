package com.chua.common.support.function;

import com.chua.common.support.utils.*;

import java.util.*;

import static com.chua.common.support.constant.CommonConstant.EMPTY;

/**
 * Joiner
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public interface Joiner {
    /**
     * 初始化
     *
     * @param separator 分隔符
     * @return 拆分器
     */
    static Joiner on(char separator) {
        return new DelegateJoiner(String.valueOf(separator));
    }

    /**
     * 初始化
     *
     * @param separator 分隔符
     * @return 拆分器
     */
    static Joiner on(String separator) {
        return new DelegateJoiner(separator);
    }


    /**
     * key value 拆分
     *
     * @param separator 分隔符
     * @return MapSplitter
     */
    Joiner withKeyValueSeparator(String separator);

    /**
     * key value 拆分
     *
     * @param separator 分隔符
     * @return MapSplitter
     */
    default Joiner withKeyValueSeparator(char separator) {
        return withKeyValueSeparator(String.valueOf(separator));
    }

    /**
     * 去除空值
     *
     * @return 去除空值
     */
    Joiner omitEmptyStrings();

    /**
     * 限制数量
     *
     * @param limit 限制数量
     * @return this
     */
    Joiner limit(int limit);

    /**
     * 去除首尾空值
     *
     * @return 去除首尾空值
     */
    Joiner trimResults();


    /**
     * 合并数组
     *
     * @param array 值
     * @return 结果
     */
    <T> String join(T array);

    /**
     * 空置替换
     * @param s 替换值
     * @return this
     */
    Joiner useForNull(String s);

    /**
     * 前綴
     * @param prefix 前綴
     * @return this
     */
    Joiner withPrefix(String prefix);

    /**
     * 拆分器
     */
    abstract class AbstractJoiner implements Joiner {
        protected boolean omitEmptyStrings;
        protected boolean trimResults;
        protected int limit;

        @Override
        public Joiner omitEmptyStrings() {
            this.omitEmptyStrings = true;
            return this;
        }

        @Override
        public Joiner trimResults() {
            this.trimResults = true;
            return this;
        }

        @Override
        public Joiner limit(int limit) {
            this.limit = limit;
            return this;
        }


    }

    /**
     * 简单拆分器
     */
    final class DelegateJoiner extends AbstractJoiner {

        private String s;
        private String kvSeparator;
        private String replace;
        private String prefix = "";

        public DelegateJoiner(String s) {
            this.s = s;
        }

        @Override
        public Joiner withKeyValueSeparator(String separator) {
            this.kvSeparator = separator;
            return this;
        }
        public String join(Map array) {
            if (MapUtils.isEmpty(array)) {
                return EMPTY;
            }

            StringBuilder stringBuilder = new StringBuilder();
            array.forEach((k, v) -> {
                stringBuilder.append(s).append(prefix).append(k).append(kvSeparator).append(ObjectUtils.defaultIfNull(v, replace));
            });
            return stringBuilder.substring(s.length());
        }
        public <T> String join(T[] array) {
            if (ArrayUtils.isEmpty(array)) {
                return EMPTY;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (T t : array) {
                if (null == t && StringUtils.isEmpty(replace)) {
                    continue;
                }
                stringBuilder.append(s).append(prefix).append(ObjectUtils.defaultIfNull(t, replace));
            }
            return stringBuilder.substring(s.length());
        }
        public  String join(Collection array) {
            if (CollectionUtils.isEmpty(array)) {
                return EMPTY;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (Object t : array) {
                if (null == t) {
                    continue;
                }
                stringBuilder.append(s).append(prefix).append(t);
            }
            return stringBuilder.substring(s.length());
        }

        @Override
        public <T> String join(T array) {
            if(null == array) {
                return EMPTY;
            }

            if(array instanceof Map) {
                return join((Map)array);
            }

            if(array instanceof Collection) {
                return join((Collection)array);
            }

            if(array.getClass().isArray()) {
                return join((T[])array);
            }
            return EMPTY;
        }

        @Override
        public Joiner useForNull(String s) {
            this.replace = s;
            return this;
        }

        @Override
        public Joiner withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

    }

}
