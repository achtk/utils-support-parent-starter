package com.chua.common.support.shell;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.spi.ServiceProvider;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 属性
 *
 * @author CH
 */
@Data
public class CommandAttribute {
    private static final int COMMAND_LIMIT = 40;
    private boolean b;
    /**
     * 分组
     */
    private String group;

    /**
     * 命令
     */
    private String name;
    /**
     * 描述
     */
    private String describe;
    /**
     * 方法
     */
    private Method method;
    /**
     * 对象
     */
    private Object bean;
    private Parameter[] parameters;
    /**
     * 例子
     */
    private List<Map<String, Object>> example = new LinkedList<>();

    private List<String> required = new LinkedList<>();
    final CommandAttributeAdaptor commandAttributeAdaptor;
    final Map<String, Object> rs = new LinkedHashMap<>();
    private Map<Parameter, ShellParam> pShell = new LinkedHashMap<>();

    public CommandAttribute(boolean b, String group, String name, String describe, Method method, Object bean) {
        this.b = b;
        this.group = group;
        this.name = name;
        this.describe = describe;
        this.method = method;
        this.bean = bean;
        this.commandAttributeAdaptor = ServiceProvider.of(CommandAttributeAdaptor.class)
                .getNewExtension("cli");
        this.doAnalysisMethod();
    }

    private void doAnalysisMethod() {
        this.parameters = method.getParameters();
        initialUsage();
    }

    private void initialUsage() {
        commandAttributeAdaptor.addOption("h", "help", false, "帮助");
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            ShellParam shellParam = parameter.getDeclaredAnnotation(ShellParam.class);
            if (null != shellParam) {
                String[] example1 = shellParam.example();
                for (String s : example1) {
                    String[] split = s.split(":", 2);
                    if (split.length != 2) {
                        continue;
                    }
                    this.example.add(ImmutableBuilder.builderOfStringMap()
                            .put("cmd", split[0])
                            .put("des", split[1])
                            .build());
                }
                String value = shellParam.value();
                commandAttributeAdaptor.addOption(shellParam);
                pShell.put(parameter, shellParam);
            }

        }
    }

    private boolean isHelp(List<String> value) {
        if (value.size() == 1 && hasRequired()) {
            return true;
        }

        for (String s : value) {
            if (s.contains("--help") || s.contains("-h") || s.contains("-?") || s.contains("--?")) {
                return true;
            }
        }

        return false;
    }

    private boolean hasRequired() {
        return !required.isEmpty();
    }

    private boolean isHelp(String[] value) {
        return isHelp(Arrays.asList(value));
    }

    /**
     * 执行方法
     *
     * @param command 参数
     * @param shell   shell
     * @param obj     对象
     * @return 结果
     * @[param pipeData 数据
     */
    public ShellResult execute(String command, ShellResult pipeData, BaseShell shell, Object obj) {
        List<String> options = Splitter.onPattern("\\s+").splitToList(command);
        if (isHelp(options)) {
            return ShellResult.text(helpInfo(shell));
        }

        Map<String, Object> env = analysisEnv(options, shell);
        return execute(options, pipeData, env, obj);
    }

    /**
     * 执行
     *
     * @param options  命令
     * @param pipeData 上个结果
     * @param env      环境
     * @param obj      对象
     * @return 結果
     */
    private ShellResult execute(List<String> options, ShellResult pipeData, Map<String, Object> env, Object obj) {
        return commandAttributeAdaptor.execute(this, options, pipeData, env, obj);
    }



    public boolean hasOptions(List<String> options) {
        for (String option : options) {
            if (option.startsWith("-")) {
                return true;
            }
        }

        return false;
    }



    /**
     * 环境
     *
     * @param options 命令
     * @param shell   shell
     * @return 环境
     */
    private Map<String, Object> analysisEnv(List<String> options, BaseShell shell) {
        Map<String, Object> env = new LinkedHashMap<>();
        env.put("shell", shell);
        env.putAll(System.getenv());
        env.putAll(shell.getEnvironment());
        return env;
    }

    private String helpInfo(BaseShell shell) {
        return commandAttributeAdaptor.help(this, shell);
    }

    /**
     * 获取命令
     *
     * @param command 命令行
     * @return 命令
     */
    private String analysisCommand(String command) {
        return command.split("\\s+")[0];
    }

    /**
     * 执行方法
     *
     * @param options 参数
     * @param shell   shell
     * @return 结果
     */
    public String execute(List<String> options, BaseShell shell) {
        return commandAttributeAdaptor.execute(this, options, shell);
    }


    public Map<String, Object> usage() {
        return commandAttributeAdaptor.usage(this);
    }


}
