package com.chua.common.support.reflection.describe;

import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * 消息体
 *
 * @author CH
 */
@Data
public class BodyDescribe {
    /**
     * 返回值
     */
    private List<String> body = new LinkedList<>();

    /**
     * 构造器
     *
     * @return 构造器
     */
    public static BodyDescribeBuilder builder() {
        return new BodyDescribeBuilder();
    }

    /**
     * 构造器
     */
    public static class BodyDescribeBuilder {

        private final BodyDescribe describe = new BodyDescribe();

        /**
         * 消息体
         *
         * @param line 消息体
         * @return this
         */
        public BodyDescribeBuilder addLine(String line) {
            describe.body.add(StringUtils.endWithAppend(line, ";"));
            return this;
        }

        /**
         * 消息体
         *
         * @return this
         */
        public BodyDescribeBuilder addReturnUuid() {
            return addReturnLine("java.util.UUID.randomUUID().toString()");
        }

        /**
         * 消息体
         *
         * @param line 消息体
         * @return this
         */
        public BodyDescribeBuilder addReturnLine(String line) {
            describe.body.add("return " + StringUtils.endWithAppend(line, ";"));
            return this;
        }

        /**
         * 消息体
         *
         * @param target 类型
         * @return this
         */
        public BodyDescribeBuilder addReturnVoid(Class<?> target) {
            if (null == target) {
                return addReturnLine("");
            }

            if (long.class == target) {
                return addReturnLine("0L");
            }

            if (int.class == target) {
                return addReturnLine("0");
            }

            if (float.class == target) {
                return addReturnLine("0F");
            }

            if (double.class == target) {
                return addReturnLine("0D");
            }

            if (short.class == target) {
                return addReturnLine("(short)0");
            }

            if (byte.class == target) {
                return addReturnLine("(byte)0");
            }

            if (char.class == target) {
                return addReturnLine("(char)0");
            }
            return addReturnLine("null");
        }

        /**
         * 消息体
         *
         * @param body 消息体
         * @return this
         */
        public BodyDescribeBuilder body(String body) {
            describe.body.add(body);
            return this;
        }

        /**
         * 消息体
         *
         * @return this
         */
        public BodyDescribe build() {
            return describe;
        }
    }


    @Override
    public String toString() {
        return "{" + Joiner.on("\r\n").join(body) + "}";
    }
}
