package com.chua.common.support.reflection.reflections.scanners;

import com.chua.common.support.reflection.reflections.ReflectionsException;
import com.chua.common.support.reflection.reflections.util.AbstractClasspathHelper;
import com.chua.common.support.reflection.reflections.util.JavassistHelper;
import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.expr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * scan methods/constructors/fields usage
 *
 * @author Administrator
 */
public class MemberUsageResourceScanner implements ResourceScanner {
    private Predicate<String> resultFilter = s -> true;
    private final ClassLoader[] classLoaders;
    private volatile ClassPool classPool;

    public MemberUsageResourceScanner() {
        this(AbstractClasspathHelper.classLoaders());
    }

    public MemberUsageResourceScanner(ClassLoader[] classLoaders) {
        this.classLoaders = classLoaders;
    }

    @Override
    public List<Map.Entry<String, String>> scan(ClassFile classFile) {
        List<Map.Entry<String, String>> entries = new ArrayList<>();
        CtClass ctClass = null;
        try {
            ctClass = getClassPool().get(classFile.getName());
            for (CtBehavior member : ctClass.getDeclaredConstructors()) {
                scanMember(member, entries);
            }
            for (CtBehavior member : ctClass.getDeclaredMethods()) {
                scanMember(member, entries);
            }
        } catch (Exception e) {
            throw new ReflectionsException("Could not scan method usage for " + classFile.getName(), e);
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return entries;
    }

    public ResourceScanner filterResultsBy(Predicate<String> filter) {
        this.resultFilter = filter;
        return this;
    }

    private void scanMember(CtBehavior member, List<Map.Entry<String, String>> entries) throws CannotCompileException {
        //key contains this$/val$ means local field/parameter closure
        final String key = member.getDeclaringClass().getName() + "." + member.getMethodInfo().getName() +
                "(" + parameterNames(member.getMethodInfo()) + ")";
        member.instrument(new ExprEditor() {
            @Override
            public void edit(NewExpr e) {
                try {
                    add(entries, e.getConstructor().getDeclaringClass().getName() + "." + "<init>" +
                            "(" + parameterNames(e.getConstructor().getMethodInfo()) + ")", key + " #" + e.getLineNumber());
                } catch (NotFoundException e1) {
                    throw new ReflectionsException("Could not find new instance usage in " + key, e1);
                }
            }

            @Override
            public void edit(MethodCall m) {
                try {
                    add(entries, m.getMethod().getDeclaringClass().getName() + "." + m.getMethodName() +
                            "(" + parameterNames(m.getMethod().getMethodInfo()) + ")", key + " #" + m.getLineNumber());
                } catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + m.getClassName() + " in " + key, e);
                }
            }

            @Override
            public void edit(ConstructorCall c) {
                try {
                    add(entries, c.getConstructor().getDeclaringClass().getName() + "." + "<init>" +
                            "(" + parameterNames(c.getConstructor().getMethodInfo()) + ")", key + " #" + c.getLineNumber());
                } catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + c.getClassName() + " in " + key, e);
                }
            }

            @Override
            public void edit(FieldAccess f) {
                try {
                    add(entries, f.getField().getDeclaringClass().getName() + "." + f.getFieldName(), key + " #" + f.getLineNumber());
                } catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + f.getFieldName() + " in " + key, e);
                }
            }
        });
    }

    private void add(List<Map.Entry<String, String>> entries, String key, String value) {
        if (resultFilter.test(key)) {
            entries.add(entry(key, value));
        }
    }

    public static String parameterNames(MethodInfo info) {
        return String.join(", ", JavassistHelper.getParameters(info));
    }

    private ClassPool getClassPool() {
        if (classPool == null) {
            synchronized (this) {
                if (classPool == null) {
                    classPool = new ClassPool();
                    for (ClassLoader classLoader : classLoaders) {
                        classPool.appendClassPath(new LoaderClassPath(classLoader));
                    }
                }
            }
        }
        return classPool;
    }
}
