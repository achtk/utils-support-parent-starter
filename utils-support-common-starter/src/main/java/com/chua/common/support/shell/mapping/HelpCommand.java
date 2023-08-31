package com.chua.common.support.shell.mapping;

import com.chua.common.support.ansi.AnsiColor;
import com.chua.common.support.ansi.AnsiOutput;
import com.chua.common.support.shell.*;
import com.chua.common.support.utils.StringUtils;

import java.util.Collection;

import static com.chua.common.support.shell.mapping.DelegateCommand.COMMAND_LIMIT;


/**
 * wget命令
 *
 * @author CH
 */
public class HelpCommand {
    /**
     * help
     *
     * @return help
     */
    @ShellMapping(value = {"help"}, describe = "帮助")
    public ShellResult help(BaseShell shell) {
        StringBuilder stringBuilder = new StringBuilder("\r\n");
        Collection<CommandAttribute> command = shell.getCommand();
        for (CommandAttribute commandAttribute : command) {
            stringBuilder
                    .append(StringUtils.repeat("\t", 1))
                    .append(AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, ifx(commandAttribute.getName(), COMMAND_LIMIT)))
                    .append(" :  ")
                    .append(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, commandAttribute.getDescribe()))
                    .append("\r\n");
        }
        stringBuilder.append("\r\n");
        return ShellResult.builder().mode(ShellMode.OTHER).result(stringBuilder.toString()).build();
    }

    public static String ifx(String name, int commandLimit) {
        if (name.length() > commandLimit) {
            return name.substring(0, commandLimit - 3) + "...";
        }

        return name + StringUtils.repeat(" ", commandLimit - name.length());
    }

    /**
     * clear
     */
    @ShellMapping(value = {"clear"}, describe = "清除终端信息")
    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
