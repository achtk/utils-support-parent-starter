package com.chua.common.support.shell.mapping;

import com.chua.common.support.shell.ShellMapping;
import com.chua.common.support.shell.ShellMode;
import com.chua.common.support.shell.ShellParam;
import com.chua.common.support.shell.ShellResult;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.view.view.ClassInfoView;
import com.sun.management.DiagnosticCommandMBean;
import sun.management.ManagementFactoryHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 基础命令
 *
 * @author CH
 */
public class DelegateCommand {

    public static final int COMMAND_LIMIT = 60;

    DiagnosticCommandMBean diagnosticCommand = ManagementFactoryHelper.getDiagnosticCommandMBean();

    @ShellMapping(value = {"view"}, describe = "预览")
    public ShellResult view(
            @ShellParam(value = "file", describe = "获取类型结构", example = {"view -f xxx: 预览"}) String file) {
        if (null != file) {
            Class<?> aClass = ClassUtils.forName(file);
            if (null != aClass) {
                return ShellResult.builder().mode(ShellMode.CODE).result(new ClassInfoView(aClass, true, 200).draw()).build();
            }

            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(file);
            if (null != systemResourceAsStream) {
                try {
                    return ShellResult.builder().mode(ShellMode.CODE).result(
                            IoUtils.toString(systemResourceAsStream, StandardCharsets.UTF_8)).build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ShellResult.error();
    }


    /**
     * jstack
     *
     * @return jstack
     */
    @ShellMapping(value = "jstack", describe = "jstack")
    public ShellResult stack() {
        try {
            Object res = diagnosticCommand.invoke("threadPrint", new Object[]{new String[]{}}, new String[]{String[].class.getName()});
            return ShellResult.builder().mode(ShellMode.CODE).result(res.toString()).build();
        } catch (Exception ignored) {
        }
        return ShellResult.error("命令不存在");
    }


}
