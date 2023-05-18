package com.chua.common.support.lang.cmd;

import com.chua.common.support.annotations.Spi;
import lombok.Data;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * 外部进程处理工厂
 *
 * @author CH
 */
@Spi("exec")
public interface ProcessFactory {
    /**
     * 目录
     *
     * @param directory 目录
     * @return this
     */
    ProcessFactory directory(String directory);

    /**
     * 引号
     *
     * @param handleQuoting 引号
     * @return 超时时间
     */
    ProcessFactory handleQuoting(boolean handleQuoting);

    /**
     * 参数
     *
     * @param argument 参数
     * @return 超时时间
     */
    ProcessFactory argument(String argument);

    /**
     * 参数
     *
     * @param key   参数
     * @param value 值
     * @return this
     */
    ProcessFactory substitution(String key, String value);

    /**
     * 参数
     *
     * @param env 参数
     * @return this
     */
    default ProcessFactory substitution(Map<String, String> env) {
        env.forEach(this::substitution);
        return this;
    }

    /**
     * 超时时间
     *
     * @param timeout 超时时间
     * @return 超时时间
     */
    ProcessFactory timeout(int timeout);

    /**
     * 编码
     *
     * @param charset 编码
     * @return 编码
     */
    ProcessFactory charset(Charset charset);

    /**
     * 执行
     *
     * @param cmd 执行
     * @return 执行
     */
    ProcessStatus exec(String cmd);

    /**
     * 执行
     *
     * @return 执行
     */
    default ProcessStatus exec() {
        return exec(null);
    }

    @Data
    static class ProcessStatus {

        private int code;
        private String output;
    }
}
