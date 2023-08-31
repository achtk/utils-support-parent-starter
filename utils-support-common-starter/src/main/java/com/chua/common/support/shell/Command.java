package com.chua.common.support.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命令
 *
 * @author CH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command {
    /**
     * 命令
     */
    private String command;

    /**
     * 上一个命令处理结果
     */
    private ShellResult pipeData;

    /**
     * 命令usage
     */
    private CommandAttribute attribute;

    public ShellResult execute(BaseShell shell, Object obj) {
        return attribute.execute(command, pipeData, shell, obj);
    }
}
