package com.chua.common.support.lang.compile;

import com.chua.common.support.constant.CommonConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编译器
 *
 * @author CHTK
 */
public interface Compiler {

    Pattern PARENT_PATTERN = Pattern.compile("extends\\s+([a-zA-z][$_a-zA-z0-9.]*)");
    Pattern INTERFACE_PATTERN = Pattern.compile("implements\\s+([a-zA-z][$_a-zA-z0-9.]*)");
    Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([a-zA-z][$_a-zA-z0-9.]*)");
    Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-z][$_a-zA-z0-9]*)");
    Pattern IMPORT_PATTERN = Pattern.compile("import\\s+(.*);");
    Pattern FIELD_PATTERN = Pattern.compile("(private|public|protect)\\s+(.*);");
    Pattern METHOD_PATTERN = Pattern.compile("(private|public|protect)\\s+(([a-zA-z][$_a-zA-z0-9.]*)(<(.*?)>)*)\\s+([a-zA-z][$_a-zA-z0-9.]*)(\\s+)*\\((.*)\\)(\\s+)*\\{((.*?)|\n)*}");

    /**
     * 編譯器
     *
     * @param code 源码
     * @return 类
     */
    default Class<?> compiler(String code) {
        return compiler(code, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 編譯器
     *
     * @param code        源码
     * @param classLoader 类加载器
     * @return 类
     */
    default Class<?> compiler(String code, final ClassLoader classLoader) {
        return compiler(code, classLoader, "");
    }

    /**
     * 編譯器
     *
     * @param code        源码
     * @param classLoader 类加载器
     * @param suffix      类名后缀
     * @return 类
     */
    default Class<?> compiler(String code, final ClassLoader classLoader, final String suffix) {
        code = code.trim();
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        String pkg;
        if (matcher.find()) {
            pkg = matcher.group(1);
        } else {
            pkg = "";
        }
        matcher = CLASS_PATTERN.matcher(code);
        String cls;
        if (matcher.find()) {
            cls = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No such class name in " + code);
        }
        String className = (pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls) + suffix;
        try {
            return Class.forName(className, true, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            if (!code.endsWith(CommonConstant.SYMBOL_RIGHT_BIG_PARENTHESES)) {
                throw new IllegalStateException("The java code not endsWith \"}\", code: \n" + code + "\n");
            }
            try {
                return doCompile(className, code);
            } catch (RuntimeException t) {
                throw t;
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to compile class, cause: " + t.getMessage() + ", class: " + className + ", code: \n" + code);
            }
        }
    }

    /**
     * 编译
     *
     * @param name   名称
     * @param source 源码
     * @return Class
     * @throws Throwable Throwable
     */
    Class<?> doCompile(String name, String source) throws Throwable;

    /**
     * 获取类名
     *
     * @param code 源码
     * @return 类名
     */
    default String getClassName(String code) {
        //获取类名
        Matcher matcher1 = CLASS_PATTERN.matcher(code);
        if (matcher1.find()) {
            return matcher1.group(1);
        } else {
            throw new IllegalArgumentException("No such class name in \n" + code);
        }
    }

    /**
     * 获取包名
     *
     * @param code 源码
     * @return 包名
     */
    default String getPkg(String code) {
        //获取包名
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
