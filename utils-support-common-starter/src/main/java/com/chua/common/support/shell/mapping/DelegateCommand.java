package com.chua.common.support.shell.mapping;

import com.chua.common.support.ansi.AnsiOutput;
import com.chua.common.support.shell.ShellMapping;
import com.chua.common.support.shell.ShellMode;
import com.chua.common.support.shell.ShellParam;
import com.chua.common.support.shell.ShellResult;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.Md5Utils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.view.view.ClassInfoView;
import com.sun.management.DiagnosticCommandMBean;
import sun.management.ManagementFactoryHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.chua.common.support.ansi.AnsiColor.RED;
import static com.chua.common.support.utils.DigestUtils.MD5;

/**
 * 基础命令
 *
 * @author CH
 */
public class DelegateCommand {

    public static final int COMMAND_LIMIT = 60;
    private static final String BASE64 = "base64";

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
     * 编码
     *
     * @param str str
     * @return {@link ShellResult}
     */
    @ShellMapping(value = {"encode"}, describe = "加密")
    public ShellResult encode(
            @ShellParam(value = "type", describe = "加密方式, md5, base64", example = {"md5 -t md5 -s xxx: 加密"}) String type,
            @ShellParam(value = "str", describe = "字符串", example = {"md5 -s xxx: 加密"}) String str) {
        if(StringUtils.isEmpty(type)) {
            type = "md5";
        }

        if (null != str) {
            if(MD5.equalsIgnoreCase(type)) {
                File file = new File(str);
                if(file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        return ShellResult.ansi(AnsiOutput.toString(RED, Md5Utils.getInstance().getMd5String(IoUtils.toByteArray(fis))));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return ShellResult.ansi(AnsiOutput.toString(RED, Md5Utils.getInstance().getMd5String(str)));
            }

            if(BASE64.equalsIgnoreCase(type)) {
                return ShellResult.ansi(AnsiOutput.toString(RED, Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8))));
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
