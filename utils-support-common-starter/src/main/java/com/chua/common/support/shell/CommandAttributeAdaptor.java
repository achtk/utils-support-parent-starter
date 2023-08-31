package com.chua.common.support.shell;

import com.chua.common.support.annotations.Spi;

import java.util.List;
import java.util.Map;

/**
 * 适配器
 *
 * @author CH
 */
@Spi("cli")
public interface CommandAttributeAdaptor {
    /**
     * 添加操作
     *
     * @param opt         短写
     * @param longOpt     长写
     * @param hasArg      是否有参数
     * @param description 描述
     */
    void addOption(final String opt, final String longOpt, final boolean hasArg, final String description);

    /**
     * 添加操作
     *
     * @param shellParam shellParam
     */
    void addOption(ShellParam shellParam);

    /**
     * 执行
     *
     * @param commandAttribute a
     * @param options          命令
     * @param pipeData         上个结果
     * @param env              环境
     * @param obj              对象
     * @return 結果
     */
    ShellResult execute(CommandAttribute commandAttribute, List<String> options, ShellResult pipeData, Map<String, Object> env, Object obj);

    /**
     * 执行
     *
     * @param commandAttribute a
     * @param options          命令
     * @param shell              shell
     * @return 結果
     */
    String execute(CommandAttribute commandAttribute, List<String> options, BaseShell shell);

    /**
     * help
     *
     * @param commandAttribute commandAttribute
     * @param shell            shell
     * @return help
     */
    String help(CommandAttribute commandAttribute, BaseShell shell);

    /**
     * usage
     *
     * @param commandAttribute a
     * @return usage
     */
    Map<String, Object> usage(CommandAttribute commandAttribute);

}
