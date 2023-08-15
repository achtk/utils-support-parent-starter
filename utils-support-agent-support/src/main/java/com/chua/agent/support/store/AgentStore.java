package com.chua.agent.support.store;

import com.chua.agent.support.constant.Constant;

import java.lang.instrument.Instrumentation;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import static com.chua.agent.support.utils.StringUtils.format;

/**
 * 存储器
 *
 * @author CH
 */
public class AgentStore implements Constant {

    public static Instrumentation instrumentation;
    private static final Map<String, Object> PARAMETER = new LinkedHashMap<>();
    static Level LOG_LEVEL_LEVEL = Level.SEVERE;


    /**
     * 初始化
     *
     * @param instrumentation inst
     */
    public static void install(Instrumentation instrumentation) {
        AgentStore.instrumentation = instrumentation;
    }


    /**
     * 初始化配置
     *
     * @param agentArguments 参数
     */
    public static void installConfig(String agentArguments) {
        if (agentArguments == null) {
            return;
        }

        for (String t : agentArguments.split(",")) {
            String[] split1 = t.split("=", 2);
            if (split1.length == 2) {
                PARAMETER.put(split1[0], split1[1]);
                continue;
            }
            PARAMETER.put(split1[0], true);
        }

        LOG_LEVEL_LEVEL = Level.parse(getStringValue(LOG_LEVEL, "SEVERE"));
    }


    /**
     * 初始化环境
     */
    public static void installEnvironment() {
        if (!printEnv()) {
            return;
        }

        for (Map.Entry<String, Object> entry : PARAMETER.entrySet()) {
            log(Level.INFO, "{} : {}", entry.getKey(), entry.getValue());
        }
    }

    /**
     * /**
     * 是否输出环境
     *
     * @return 是否输出环境
     */
    private static boolean printEnv() {
        return "true".equals(PARAMETER.getOrDefault(LOG_OPEN, true).toString());
    }

    public static void log(Level level, String message, Object... args) {
        if (LOG_LEVEL_LEVEL.intValue() <= level.intValue()) {
            printLog(level, DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " [" + level.getName() + "] " + message, args);
        }
    }

    /**
     * 输出日志
     *
     * @param level   级别
     * @param message 消息
     * @param args    参数
     */
    private static void printLog(Level level, String message, Object... args) {
        logger.log(level, format(message, args));
    }

    /**
     * 获取参数
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 参数
     */
    public static String getStringValue(String key, String defaultValue) {
        return PARAMETER.getOrDefault(key, defaultValue).toString();
    }

    /**
     * 获取参数
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 参数
     */
    public static int getIntegerValue(String key, int defaultValue) {
        Object orDefault = PARAMETER.getOrDefault(key, defaultValue);
        if (orDefault instanceof String) {
            return Integer.parseInt(orDefault.toString());
        }
        return (int) orDefault;
    }

    /**
     * 设置参数
     *
     * @param key   索引
     * @param value 值
     */
    private static void setIntegerValue(String key, int value) {
        PARAMETER.put(key, value);
    }


}
