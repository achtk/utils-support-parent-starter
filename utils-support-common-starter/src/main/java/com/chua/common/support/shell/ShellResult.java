package com.chua.common.support.shell;

import lombok.Builder;
import lombok.Data;

/**
 * 结果、
 *
 * @author CH
 */
@Data
@Builder
public class ShellResult {

    /**
     * 模式
     */
    private ShellMode mode;
    /**
     * 消息
     */
    private String result;

    static final ShellResult ERROR = ShellResult.builder().mode(ShellMode.ERROR).build();

    public static ShellResult error() {
        return ERROR;
    }

    public static ShellResult error(String text) {
        return ShellResult.builder().mode(ShellMode.ERROR).result(text).build();
    }

    public static ShellResult table(String text) {
        return ShellResult.builder().mode(ShellMode.TABLE).result(text).build();
    }

    public static ShellResult text(String text) {
        return ShellResult.builder().mode(ShellMode.NORMAL).result(text).build();
    }

    public static ShellResult html(String text) {
        return ShellResult.builder().mode(ShellMode.HTML).result(text).build();
    }

    public static ShellResult ansi(String text) {
        return ShellResult.builder().mode(ShellMode.ASNI).result(text).build();
    }
}
