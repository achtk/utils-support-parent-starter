package com.chua.common.support.shell;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IoUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * shell
 *
 * @author CH
 */
@Slf4j
public abstract class Shell implements InitializingAware, AutoCloseable {

    private static final String OUT = ">";
    public final Map<String, CommandAttribute> shellCommand = new ConcurrentHashMap<>();
    protected String prompt = "shell";
    protected boolean openLog;
    protected final AtomicBoolean status = new AtomicBoolean(false);


    public Shell(Object... beans) {
        for (Object bean : beans) {
            register(bean);
        }
        afterPropertiesSet();
    }

    private final Map<String, Object> env = new LinkedHashMap<>();

    /**
     * 设置环境
     *
     * @param name  名称
     * @param value 值
     */
    public void setEnvironment(String name, Object value) {
        env.put(name, value);
        return;
    }

    /**
     * 获取环境
     */
    public Map<String, Object> getEnvironment() {
        return env;
    }

    /**
     * 注册命令
     *
     * @param bean bean
     */
    public void register(Object bean) {
        if (null == bean) {
            return;
        }

        ClassUtils.doWithMethods(bean.getClass(), method -> {
            ShellMapping shellMapping = method.getDeclaredAnnotation(ShellMapping.class);
            if (null != shellMapping) {
                analysis(shellMapping, method, bean);
            }
        });
    }

    private void analysis(ShellMapping shellMapping, Method method, Object forObject) {
        if (shellMapping.value().length == 0) {
            register(new CommandAttribute(
                    shellMapping.needShort(),
                    shellMapping.group(),
                    method.getName(),
                    shellMapping.describe(), method, forObject));
            return;
        }

        for (String s : shellMapping.value()) {
            register(new CommandAttribute(
                    shellMapping.needShort(), shellMapping.group(),
                    s,
                    shellMapping.describe(), method, forObject));
        }
    }


    @SneakyThrows
    @Override
    public void afterPropertiesSet() {
        status.set(true);
    }

    /**
     * 注册命令
     *
     * @param commandAttribute 命令
     */
    private void register(CommandAttribute commandAttribute) {
        shellCommand.put(commandAttribute.getName(), commandAttribute);
    }

    /**
     * 执行命令
     *
     * @param command 命令
     * @param obj     对象
     * @return 结果
     */
    ShellResult execute(Command command, Object obj) {
        return command.execute(this, obj);
    }

    /**
     * 分析命令
     *
     * @param command 命令
     * @param obj     对象
     * @return 结果
     */
    public ShellResult handlerAnalysis(String command, Object obj) {
        List<Command> pipe = new LinkedList<>();
        String[] split = command.split("\\|");
        for (String s : split) {
            String[] split1 = s.trim().split("\\s+");
            String command1 = split1[0];
            if (!shellCommand.containsKey(command1)) {
                return ShellResult.error(command1 + " 命令不存在");
            }

            pipe.add(new Command(s, null, shellCommand.get(command1)));
        }

        ShellResult result = null;
        for (Command command1 : pipe) {
            try {
                result = execute(new Command(command1.getCommand().trim(), result, command1.getAttribute()), obj);
            } catch (Exception e) {
                return ShellResult.error(command1 + "解析失败");
            }
        }

        if (isWriterFile(command)) {
            writer(command, result);
            return ShellResult.text("");
        }
        return result;
    }

    private void writer(String command, ShellResult result) {
        String[] split = command.split(OUT, 2);
        String outFile = null;
        if (split.length == 1) {
            outFile = ".";
        } else {
            outFile = split[1];
        }

        File temp = new File(outFile.trim());
        File parentFile = temp.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }


        try {
            IoUtils.write(result.getResult(), temp, StandardCharsets.UTF_8, true);
        } catch (IOException ignored) {
        }
    }

    private boolean isWriterFile(String command) {
        return command.contains(OUT);
    }

    @Override
    public void close() throws Exception {
        status.set(false);
    }

    /**
     * 获取命令
     *
     * @return 命令
     */
    public Collection<CommandAttribute> getCommand() {
        return shellCommand.values();
    }

    /**
     * 命令
     *
     * @return 命令
     */
    public List<Map<String, Object>> usageCommand() {
        List<Map<String, Object>> rs = new ArrayList<>(shellCommand.size());
        for (CommandAttribute commandAttribute : shellCommand.values()) {
            rs.add(commandAttribute.usage());
        }

        return rs;
    }

}
