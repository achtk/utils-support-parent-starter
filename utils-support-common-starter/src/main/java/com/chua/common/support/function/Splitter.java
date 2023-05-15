package com.chua.common.support.function;


import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.utils.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 字符串分割器
 *
 * @author CH
 */
public interface Splitter {
    /**
     * 初始化
     *
     * @param separator 分隔符
     * @return 拆分器
     */
    static Splitter on(char separator) {
        return new DelegateSplitter(String.valueOf(separator));
    }

    /**
     * 初始化
     *
     * @param separator 分隔符
     * @return 拆分器
     */
    static Splitter on(String separator) {
        return new DelegateSplitter(separator);
    }

    /**
     * 初始化
     *
     * @param separator 分隔符
     * @return 拆分器
     */
    static Splitter on(Pattern separator) {
        return new DelegateSplitter(separator.pattern());
    }

    /**
     * key value 拆分
     *
     * @param separator 分隔符
     * @return MapSplitter
     */
    MapSplitter withKeyValueSeparator(String separator);

    /**
     * key value 拆分
     *
     * @param separator 分隔符
     * @return MapSplitter
     */
    default MapSplitter withKeyValueSeparator(char separator) {
        return withKeyValueSeparator(String.valueOf(separator));
    }

    /**
     * 去除空值
     *
     * @return 去除空值
     */
    Splitter omitEmptyStrings();

    /**
     * 限制数量
     *
     * @param limit 限制数量
     * @return this
     */
    Splitter limit(int limit);

    /**
     * 去除首尾空值
     *
     * @return 去除首尾空值
     */
    Splitter trimResults();

    /**
     * 拆分为数组
     *
     * @param value 值
     * @return 结果
     */
    List<String> splitToList(String value);


    /**
     * 拆分器
     */
    abstract class AbstractSplitter implements Splitter {
        protected boolean omitEmptyStrings;
        protected boolean trimResults;
        protected int limit;

        @Override
        public Splitter omitEmptyStrings() {
            this.omitEmptyStrings = true;
            return this;
        }

        @Override
        public Splitter trimResults() {
            this.trimResults = true;
            return this;
        }

        @Override
        public Splitter limit(int limit) {
            this.limit = limit;
            return this;
        }


    }

    final class MapSplitter extends AbstractSplitter {

        private String separator;
        private String kvSeparator;

        public MapSplitter(String separator) {
            super();
            this.separator = separator;
        }

        @Override
        public MapSplitter withKeyValueSeparator(String separator) {
            this.kvSeparator = separator;
            return this;
        }

        @Override
        public List<String> splitToList(String value) {
            return Collections.emptyList();
        }

        /**
         * 拆分为数组
         *
         * @param value 值
         * @return 结果
         */
        public Map<String, String> split(String value) {
            Map<String, String> rs = new LinkedHashMap<>();
            String[] split = limit > 0 ? value.split(separator, limit) : value.split(separator);
            for (String s1 : split) {
                if (omitEmptyStrings && StringUtils.isBlank(s1)) {
                    continue;
                }

                if (trimResults) {
                    s1 = s1.trim();
                }

                String[] split1 = s1.split(kvSeparator, 2);
                if(split1.length == 1) {
                    if(omitEmptyStrings) {
                        continue;
                    }

                    rs.putIfAbsent(split1[0], null);
                    continue;
                }
                rs.putIfAbsent(split1[0], split1[1]);
            }

            return rs;
        }
    }
    /**
     * 简单拆分器
     */
    final class DelegateSplitter extends AbstractSplitter {

        private String s;

        public DelegateSplitter(String s) {
            this.s = s;
        }

        @Override
        public MapSplitter withKeyValueSeparator(String separator) {
            return new MapSplitter(separator);

        }
        @Override
        public List<String> splitToList(String value) {
            if (StringUtils.isEmpty(value)) {
                return Collections.emptyList();
            }

            List<String> rs = new LinkedList<>();
            String[] split = limit > 0 ? value.split(s, limit) : value.split(s);
            for (String s1 : split) {
                if (omitEmptyStrings && StringUtils.isBlank(s1)) {
                    continue;
                }

                if (trimResults) {
                    s1 = s1.trim();
                }

                rs.add(s1);
            }
            return rs;
        }

        /**
         * 拆分为数组
         *
         * @param value 值
         * @return 结果
         */
        public String[] split(String value) {
            return splitToList(value).toArray(EMPTY_ARRAY);
        }
    }

}
