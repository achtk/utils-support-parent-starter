package com.chua.shell.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.shell.*;
import com.chua.common.support.utils.StringUtils;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * cli
 *
 * @author CH
 */
@Spi("cli")
public class CliCommandAttributeAdaptor implements CommandAttributeAdaptor {
    final Options options = new Options();

    @Override
    public void addOption(String shortName, String longName, boolean b, String desc) {
        options.addOption(shortName, longName, b, desc);
    }

    @Override
    public void addOption(ShellParam shellParam) {
        options.addOption(Option.builder()
                .longOpt(shellParam.value())
                .required(shellParam.required())
                .numberOfArgs(shellParam.numberOfArgs())
                .option(StringUtils.defaultString(shellParam.shortName(), shellParam.value().substring(0, 1)))
                .optionalArg(!shellParam.required())
                .type(shellParam.numberOfArgs() > 1 ? List.class : String.class)
                .desc(shellParam.describe())
                .argName(shellParam.value())
                .build());
    }

    @Override
    public ShellResult execute(CommandAttribute commandAttribute, List<String> options, ShellResult pipeData, Map<String, Object> env, Object obj) {
        CommandLineParser parser = new DefaultParser();
        String[] strings = options.subList(1, options.size()).toArray(new String[0]);
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(this.options, strings);
        } catch (ParseException ignored) {
            return ShellResult.error("命令解析失败");
        }

        return execute(commandAttribute, options, commandLine, pipeData, env, obj);
    }

    @Override
    public String execute(CommandAttribute commandAttribute, List<String> options, Shell shell) {
        Object[] args = create(commandAttribute, options, shell);
        commandAttribute.getMethod().setAccessible(true);
        try {
            return String.valueOf(commandAttribute.getMethod().invoke(commandAttribute.getBean(), args));
        } catch (Throwable e) {
            return "无效参数";
        }
    }

    /**
     * 参数
     *
     * @param commandAttribute commandAttribute
     * @param options          请求
     * @param shell            shell
     * @return 参数
     */
    private Object[] create(CommandAttribute commandAttribute, List<String> options, Shell shell) {
        Object[] args = new Object[commandAttribute.getParameters().length];
        if (args.length == 0) {
            return args;
        }

        try {
            CommandLineParser parser = new DefaultParser();
            String[] strings = options.subList(1, options.size()).toArray(new String[0]);
            CommandLine commandLine = parser.parse(this.options, strings);
            for (int i = 0; i < commandAttribute.getParameters().length; i++) {
                Parameter parameter = commandAttribute.getParameters()[i];
                if (parameter.getDeclaredAnnotation(ShellOriginal.class) != null) {
                    try {
                        args[i] = createValue(parameter, Joiner.on(" ").join(strings));
                    } catch (Exception ignored) {
                    }
                    continue;
                }
                try {
                    args[i] = createValue(parameter, createArg(commandAttribute, parameter, commandLine, shell));
                } catch (Exception ignored) {
                }
            }
            args = checkDefault(commandAttribute, args, commandLine);
        } catch (ParseException e) {
            return null;
        }


        return args;
    }

    private Object[] checkDefault(CommandAttribute commandAttribute, Object[] args, CommandLine commandLine) {
        Object[] rs = new Object[args.length];
        int count = 0;
        for (int i = 0; i < commandAttribute.getParameters().length; i++) {
            rs[i] = args[i];
            if (null != rs[i] && !"".equals(rs[i])) {
                continue;
            }

            Parameter parameter = commandAttribute.getParameters()[i];
            ShellParam shellParam = commandAttribute.getPShell().get(parameter);
            if (null == shellParam) {
                continue;
            }

            if (!shellParam.isDefault()) {
                continue;
            }
            List<String> argList = commandLine.getArgList();
            if (argList.size() > count) {
                rs[i] = argList.get(count++);
            }
        }

        return rs;
    }

    private Object createValue(Parameter parameter, Object arg) {
        Class<?> type = parameter.getType();
        if (null == arg || type.isAssignableFrom(arg.getClass())) {
            return arg;
        }

        if (String.class.isAssignableFrom(type)) {
            if (arg instanceof Iterable) {
                return Joiner.on(" ").join((Iterable<? extends Object>) arg);
            }

            return String.valueOf(arg);
        }
        return Converter.convertIfNecessary(arg, type);
    }

    /**
     * 参数
     *
     * @param commandAttribute commandAttribute
     * @param parameter        字段
     * @param commandLine      选项
     * @param shell            shell
     * @return 结果
     */
    private Object createArg(CommandAttribute commandAttribute, Parameter parameter, CommandLine commandLine, Shell shell) {
        if (Shell.class == parameter.getType()) {
            return shell;
        }
        ShellParam shellParam = commandAttribute.getPShell().get(parameter);
        if (null == shellParam) {
            return null;
        }

        if (shellParam.numberOfArgs() > 1) {
            return commandLine.getOptionValues(shellParam.value());
        }
        return commandLine.getOptionValue(shellParam.value(), shellParam.defaultValue());
    }

    @Override
    public String help(CommandAttribute commandAttribute, Shell shell) {
        HelpFormatter hf = new HelpFormatter();
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            hf.printHelp(printWriter, 800, commandAttribute.getName(), null, this.options, 1, 3, null, true);
            String toString = stringWriter.toString();
            if (!(shell instanceof WebShell)) {
                System.out.println(toString);
            }
            return toString;
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public Map<String, Object> usage(CommandAttribute commandAttribute) {
        Map<String, Object> rs = commandAttribute.getRs();
        if (rs.isEmpty()) {
            HelpFormatter hf = new HelpFormatter();
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                hf.printHelp(printWriter, 800, commandAttribute.getName(), null, options, 1, 1, null, true);
                String toString = stringWriter.toString();
                rs.put("title", commandAttribute.getName());
                rs.put("key", commandAttribute.getName());
                rs.put("group", commandAttribute.getGroup());
                rs.put("description", commandAttribute.getDescribe());
                rs.put("usage", toString.split("\r")[0]);
                List<Map<String, String>> ex = new LinkedList<>();
                rs.put("example", ex);
                for (Map.Entry<String, String> entry : commandAttribute.getExample().entrySet()) {
                    Map<String, String> item = new HashMap<>(2);
                    item.put("cmd", entry.getKey());
                    item.put("desc", entry.getValue());

                    ex.add(item);
                }

                return rs;
            } catch (IOException ignored) {
            }

            return Collections.emptyMap();
        }
        return rs;
    }

    /**
     * 执行
     *
     * @param commandAttribute commandAttribute
     * @param options          命令
     * @param commandLine      命令
     * @param pipeData         上个结果
     * @param env              环境
     * @param obj              obj
     * @return 結果
     */
    private ShellResult execute(CommandAttribute commandAttribute, List<String> options, CommandLine commandLine, ShellResult pipeData, Map<String, Object> env, Object obj) {
        Object[] args = analysisArgs(commandAttribute, options, commandLine, pipeData, env, obj);
        commandAttribute.getMethod().setAccessible(true);
        try {
            return (ShellResult) commandAttribute.getMethod().invoke(commandAttribute.getBean(), args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 参数
     *
     * @param commandAttribute commandAttribute
     * @param options          命令
     * @param commandLine      命令
     * @param pipeData         上个结果
     * @param env              环境
     * @param obj              obj
     * @return
     */
    private Object[] analysisArgs(CommandAttribute commandAttribute, List<String> options, CommandLine commandLine, ShellResult pipeData, Map<String, Object> env, Object obj) {
        int index = 0;
        boolean hasOption = commandAttribute.hasOptions(options);
        Object[] args = new Object[commandAttribute.getParameters().length];
        for (int i = 0; i < commandAttribute.getParameters().length; i++) {
            Parameter parameter = commandAttribute.getParameters()[i];
            if (parameter.getDeclaredAnnotation(ShellOriginal.class) != null) {
                args[i] = converter(Joiner.on(" ").join(options.subList(1, options.size())), parameter.getType(), env);
                continue;
            }

            if (parameter.getDeclaredAnnotation(ShellPipe.class) != null) {
                args[i] = pipeData;
                continue;
            }

            ShellParam shellParam = parameter.getDeclaredAnnotation(ShellParam.class);
            if (null == shellParam) {
                if (null != obj && parameter.getType().isAssignableFrom(obj.getClass())) {
                    args[i] = obj;
                    continue;
                }

                List<String> argList = commandLine.getArgList();
                lo:
                for (Object value : env.values()) {
                    if (null != value && parameter.getType().isAssignableFrom(value.getClass())) {
                        args[i] = value;
                        break lo;
                    }
                }
                if (null != args[i]) {
                    continue;
                }
                args[i] = converter(argList.get(index++ % argList.size()), parameter.getType(), env);
                continue;
            }

            int i1 = shellParam.numberOfArgs();
            if (i1 > 1) {
                if (!hasOption) {
                    args[i] = converter(commandLine.getArgList(), parameter.getType(), env);
                    continue;
                }
                args[i] = converter(commandLine.getOptionValues(shellParam.value()), parameter.getType(), env);
                continue;
            }
            if (!hasOption) {
                List<String> argList = commandLine.getArgList();
                try {
                    args[i] = converter(argList.get(index++ % argList.size()), parameter.getType(), env);
                } catch (Exception e) {
                    args[i] = converter(shellParam.defaultValue(), parameter.getType(), env);
                }
                continue;
            }
            args[i] = converter(commandLine.getOptionValue(shellParam.value(), shellParam.defaultValue()), parameter.getType(), env);
        }

        return args;
    }

    private Object converter(Object optionValue, Class<?> type, Map<String, Object> env) {
        if (optionValue instanceof String && optionValue.toString().startsWith("'") && optionValue.toString().endsWith("'")) {
            optionValue = Converter.convertIfNecessary(env.getOrDefault(optionValue.toString().substring(1, optionValue.toString().length() - 1), optionValue), String.class);
        }
        return Converter.convertIfNecessary(optionValue, type);
    }
}
