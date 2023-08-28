package com.chua.common.support.shell.mapping;

import com.chua.common.support.shell.ShellMapping;
import com.chua.common.support.shell.ShellResult;
import com.sun.management.DiagnosticCommandMBean;
import sun.management.ManagementFactoryHelper;

/**
 * 基础命令
 *
 * @author CH
 */
public class DelegateCommand {

    public static final int COMMAND_LIMIT = 60;

    DiagnosticCommandMBean diagnosticCommand = ManagementFactoryHelper.getDiagnosticCommandMBean();

//    @ShellMapping(value = {"view"}, describe = "预览")
//    public ShellResult view(
//            @ShellParam(value = "mode", describe = "模式", example = {"view --mode CLASS class: view --mode CLASS java.lang.String"}, numberOfArgs = 2) List<String> file
//    ) {
//        if (null == file || file.size() != 2) {
//            return ShellResult.builder().mode(ERROR).result("参数不正确, view --mode CLASS class: 模式").build();
//        }
//
//        if ("class".equalsIgnoreCase(file.get(0))) {
//            return ShellResult.builder().mode(TABLE).result(new ClassInfoView(ClassUtils.forName(file.get(1), ClassLoader.getSystemClassLoader()), true, 100).draw()).build();
//        }
//
//        return ShellResult.error();
//    }


    /**
     * jstack
     *
     * @return jstack
     */
    @ShellMapping(value = "jstack", describe = "jstack")
    public ShellResult stack() {
        try {
            Object res = diagnosticCommand.invoke("threadPrint", new Object[]{new String[]{}}, new String[]{String[].class.getName()});
            return ShellResult.text(res.toString());
        } catch (Exception ignored) {
        }
        return ShellResult.error("命令不存在");
    }


}
