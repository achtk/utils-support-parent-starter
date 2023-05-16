package com.chua.common.support.lang.compile;

import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * javassist 编译器
 *
 * @author CHTK
 */
@Slf4j
public class JavassistCompiler implements Compiler {

    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([\\w.*]+);");

    private static final Pattern EXTENDS_PATTERN = Pattern.compile("\\s+extends\\s+([\\w.]+)[^{]*\\{");

    private static final Pattern IMPLEMENTS_PATTERN = Pattern.compile("\\s+implements\\s+([\\w.]+)\\s*\\{");

    private static final Pattern METHODS_PATTERN = Pattern.compile("(private|public|protected)\\s+");
    private static final Pattern METHODS_ANNOTATION_PATTERN = Pattern.compile("@(.*?)\\n");

    private static final Pattern FIELD_PATTERN = Pattern.compile("[^\n]+=[^\n]+;");
    private static final Pattern FIELD_OTHER_PATTERN = Pattern.compile("([\\w.*]+)\\s+[^\n]+;");

    @Override
    public Class<?> doCompile(String name, String source) throws Throwable {
        CtClassBuilder builder = new CtClassBuilder();
        builder.setClassName(name);

        // process imported classes
        Matcher matcher = IMPORT_PATTERN.matcher(source);
        while (matcher.find()) {
            builder.addImports(matcher.group(1).trim());
        }

        // process extended super class
        matcher = EXTENDS_PATTERN.matcher(source);
        if (matcher.find()) {
            try {
                builder.setSuperClassName(matcher.group(1).trim());
            } catch (Exception ignored) {
            }
        }

        // process implemented interfaces
        matcher = IMPLEMENTS_PATTERN.matcher(source);
        if (matcher.find()) {
            String[] ifaces = matcher.group(1).trim().split(",");
            Arrays.stream(ifaces).forEach(i -> builder.addInterface(i.trim()));
        }

        source = source.replace("@Override", "");
        // process constructors, fields, methods
        String body = source.substring(source.indexOf('{') + 1, source.length() - 1);
        String[] methods = METHODS_PATTERN.split(body);
        List<String> newMethods = new ArrayList<>();
        for (String method : methods) {
            Matcher matcher1 = METHODS_ANNOTATION_PATTERN.matcher(method);
            if (matcher1.find()) {
                while ((matcher1 = METHODS_ANNOTATION_PATTERN.matcher(method)).find()) {
                    method = method.replace(matcher1.group(), "");
                }
                newMethods.add(method);
            } else {
                newMethods.add(method);
            }
        }

        String className = getSimpleClassName(name);
        newMethods.stream().map(String::trim).filter(m -> !m.isEmpty()).forEach(method -> {
            if (method.startsWith(className)) {
                builder.addConstructor("public " + method);
            } else if (FIELD_PATTERN.matcher(method).matches() || FIELD_OTHER_PATTERN.matcher(method).matches()) {
                builder.addField("private " + method);
            } else {
                builder.addMethod("public " + method);
            }
        });

        // compile
        ClassLoader classLoader = getClass().getClassLoader();
        CtClass cls = builder.build(classLoader);
        return cls.toClass(classLoader, JavassistCompiler.class.getProtectionDomain());
    }

    private String getSimpleClassName(String qualifiedName) {
        if (null == qualifiedName) {
            return null;
        }

        int i = qualifiedName.lastIndexOf('.');
        return i < 0 ? qualifiedName : qualifiedName.substring(i + 1);
    }
}
