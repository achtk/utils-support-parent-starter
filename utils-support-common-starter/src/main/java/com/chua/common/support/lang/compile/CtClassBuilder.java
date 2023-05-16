package com.chua.common.support.lang.compile;

import javassist.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.*;


/**
 * @author CH
 * @version 1.0.0
 */
public class CtClassBuilder {

    private String className;

    private String superClassName = "java.lang.Object";

    private final List<String> imports = new ArrayList<>();

    private final Map<String, String> fullNames = new HashMap<>();

    private final List<String> ifaces = new ArrayList<>();

    private final List<String> constructors = new ArrayList<>();

    private final List<String> fields = new ArrayList<>();

    private final List<String> methods = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = getQualifiedClassName(superClassName);
    }

    public List<String> getImports() {
        return imports;
    }

    public void addImports(String pkg) {
        int pi = pkg.lastIndexOf('.');
        if (pi > 0) {
            String pkgName = pkg.substring(0, pi);
            this.imports.add(pkgName);
            if (!pkg.endsWith(SYMBOL_DOT_ASTERISK)) {
                fullNames.put(pkg.substring(pi + 1), pkg);
            }
        }
    }

    public List<String> getInterfaces() {
        return ifaces;
    }

    public void addInterface(String iface) {
        this.ifaces.add(getQualifiedClassName(iface));
    }

    public List<String> getConstructors() {
        return constructors;
    }

    public void addConstructor(String constructor) {
        this.constructors.add(constructor);
    }

    public List<String> getFields() {
        return fields;
    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public List<String> getMethods() {
        return methods;
    }

    public void addMethod(String method) {
        this.methods.add(method);
    }

    /**
     * get full qualified class name
     *
     * @param className super class name, maybe qualified or not
     */
    protected String getQualifiedClassName(String className) {
        if (className.contains(SYMBOL_DOT)) {
            return className;
        }

        if (fullNames.containsKey(className)) {
            return fullNames.get(className);
        }

        return forName(imports.toArray(new String[0]), className).getName();
    }

    /**
     * 通过包+类名获取类
     *
     * @param packages  包
     * @param className 类名
     * @return 类
     */
    private Class<?> forName(String[] packages, String className) {
        try {
            return forNameNoClassLoader(className);
        } catch (ClassNotFoundException e) {
            if (null == packages) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            for (String pkg : packages) {
                try {
                    return forNameNoClassLoader(pkg + "." + className);
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
        return null;
    }

    /**
     * 通过默认类加载器转化类名为类
     *
     * @param className 类名
     * @return 类
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private Class<?> forNameNoClassLoader(String className) throws ClassNotFoundException {
        try {
            return arrayForName(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            } catch (Exception e1) {
                if (className.indexOf(SYMBOL_DOT_CHAR) != -1) {
                    throw e;
                }
                try {
                    return arrayForName("java.lang." + className);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 获取数组类类
     *
     * @param className 数组类类名
     * @return 类
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private static Class<?> arrayForName(String className) throws ClassNotFoundException {
        return Class.forName(className.endsWith("[]")
                ? "[L" + className.substring(0, className.length() - 2) + ";"
                : className, true, Thread.currentThread().getContextClassLoader());
    }

    /**
     * build CtClass object
     */
    public CtClass build(ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        ClassPool pool = new ClassPool(true);
        pool.appendClassPath(new LoaderClassPath(classLoader));

        // create class
        CtClass ctClass = pool.makeClass(className, pool.get(superClassName));

        // add imported packages
        imports.forEach(pool::importPackage);

        // add implemented interfaces
        for (String iface : ifaces) {
            ctClass.addInterface(pool.get(iface));
        }

        // add constructors
        for (String constructor : constructors) {
            ctClass.addConstructor(CtNewConstructor.make(constructor, ctClass));
        }

        // add fields
        for (String field : fields) {
            try {
                ctClass.addField(CtField.make(field, ctClass));
            } catch (CannotCompileException e) {
                int before = field.indexOf("<");
                int after = field.indexOf(">");
                field = field.substring(0, before) + field.substring(after + 1);
                ctClass.addField(CtField.make(field, ctClass));
            }
        }

        // add methods
        for (String method : methods) {
            try {
                ctClass.addMethod(CtNewMethod.make(method, ctClass));
            } catch (CannotCompileException e) {
                method = method.replace("...", "[]");
                ctClass.addMethod(CtNewMethod.make(method, ctClass));
            }
        }

        return ctClass;
    }

}