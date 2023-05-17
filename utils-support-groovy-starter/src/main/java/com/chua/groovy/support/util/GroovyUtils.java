package com.chua.groovy.support.util;

import com.chua.common.support.utils.IoUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * groovy工具
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/1/11
 */
public class GroovyUtils {
    /**
     * Groovy Shell
     */
    private static final GroovyShell GROOVY_SHELL = new GroovyShell();

    /**
     * 执行脚本
     *
     * @param groovyScript 脚本
     * @return 信息
     */
    public static Object evalGroovyScript(String groovyScript) {
        return GROOVY_SHELL.evaluate(groovyScript);
    }


    /**
     * 执行脚本
     *
     * @param groovyScriptReader 脚本文件
     * @return 信息
     */
    public static Object evalGroovyScriptFile(Reader groovyScriptReader) throws IOException {
        return evalGroovyScriptFile(groovyScriptReader, Collections.emptyMap());
    }

    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @return 信息
     */
    public static Object evalGroovyScriptFile(String groovyScriptFile) throws IOException {
        return evalGroovyScriptFile(new File(groovyScriptFile), Collections.emptyMap());
    }


    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @param params           参数
     * @return 信息
     */
    public static Object evalGroovyScriptFile(String groovyScriptFile, Map<String, Object> params) throws IOException {
        return evalGroovyScriptFile(new File(groovyScriptFile), params);
    }

    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @return 信息
     */
    public static Object evalGroovyScriptFile(URL groovyScriptFile) throws IOException {
        return evalGroovyScriptFile(groovyScriptFile, Collections.emptyMap());
    }


    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @param params           参数
     * @return 信息
     */
    public static Object evalGroovyScriptFile(URL groovyScriptFile, Map<String, Object> params) throws IOException {
        try (InputStreamReader isr = IoUtils.toInputStreamReader(groovyScriptFile)) {
            return evalGroovyScriptFile(isr, params);
        }
    }

    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @return 信息
     */
    public static Object evalGroovyScriptFile(File groovyScriptFile) throws IOException {
        return evalGroovyScriptFile(groovyScriptFile, Collections.emptyMap());
    }

    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @param params           参数
     * @return 信息
     */
    public static Object evalGroovyScriptFile(File groovyScriptFile, Map<String, Object> params) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(groovyScriptFile), StandardCharsets.UTF_8)) {
            return evalGroovyScriptFile(isr, params);
        }
    }

    /**
     * 执行脚本
     *
     * @param groovyScriptFile 脚本文件
     * @param params           参数
     * @return 信息
     */
    public static Object evalGroovyScriptFile(Reader groovyScriptFile, Map<String, Object> params) throws IOException {
        // 调用带参数的groovy shell时，使用bind绑定数据
        Binding binding = new Binding();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            binding.setProperty(entry.getKey(), entry.getValue());
        }
        GroovyShell groovyShell = new GroovyShell(binding);
        return groovyShell.evaluate(groovyScriptFile);
    }
}
