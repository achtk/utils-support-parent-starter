package com.chua.common.support.extra.el.baseutil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 基础类
 * @author CH
 */
public class Formatter {
    static ConcurrentMap<String, Template> map = new ConcurrentHashMap<>();
    static ThreadLocal<StringBuilder> threadLocal = ThreadLocal.withInitial(StringBuilder::new);

    public static String format(String pattern, Object... params) {
        StringBuilder builder = threadLocal.get();
        map.computeIfAbsent(pattern, v -> {
            List<BaseSegment> segments = new LinkedList<>();
            char[] value = v.toCharArray();
            int start = 0;
            int pre = 0;
            int paramIndex = 0;
            while (pre < value.length) {
                start = indexOfBrace(value, pre);
                if (start == -1) {
                    segments.add(new StringSegment(String.valueOf(value, pre, value.length - pre)));
                    break;
                } else {
                    segments.add(new StringSegment(String.valueOf(value, pre, start - pre)));
                    segments.add(new ParamSegment(paramIndex));
                    paramIndex++;
                    pre = start + 2;
                }
            }
            return new Template(segments.toArray(new BaseSegment[0]));
        }).output(builder, params);
        String result = builder.toString();
        builder.setLength(0);
        return result;
    }

    static class Template {
        BaseSegment[] segments;

        public Template(BaseSegment[] segments) {
            this.segments = segments;
        }

        void output(StringBuilder builder, Object... params) {
            for (BaseSegment segment : segments) {
                segment.output(builder, params);
            }
        }
    }

    static abstract class BaseSegment {
        /**
         * 输出
         * @param builder 数据
         * @param params 参数
         */
        abstract void output(StringBuilder builder, Object... params);
    }

    static class StringSegment extends BaseSegment {
        final String value;

        StringSegment(String value) {
            this.value = value;
        }

        @Override
        void output(StringBuilder builder, Object... params) {
            builder.append(value);
        }
    }

    static class ParamSegment extends BaseSegment {
        final int index;

        ParamSegment(int index) {
            this.index = index;
        }

        @Override
        void output(StringBuilder builder, Object... params) {
            builder.append(params[index]);
        }
    }

    /**
     * 从char数组中确定大括号的位置，如果不存在返回-1
     *
     * @param array
     * @param off
     * @return
     */
    private static int indexOfBrace(char[] array, int off) {
        int length = array.length - 1;
        for (int i = off; i < length; i++) {
            if (array[i] == '{' && array[i + 1] == '}') {
                return i;
            }
        }
        return -1;
    }
}
