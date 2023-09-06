package com.chua.agent.support.transform;

import com.chua.agent.support.plugin.ExceptionPlugin;
import javassist.*;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import static net.bytebuddy.jar.asm.ClassReader.SKIP_FRAMES;
import static net.bytebuddy.jar.asm.Opcodes.ASM6;

/**
 * @author Kohsuke Kawaguchi
 */
public class TransformerImpl implements ClassFileTransformer {

    private final List<String> names1 = new ArrayList<String>() {

        {
            add(URL.class.getName().replace(".", "/"));
            add(PrintStream.class.getName().replace(".", "/"));
            add(Exception.class.getName().replace(".", "/"));

        }

    };

    private final List<String> names = new ArrayList<String>() {
        {
            add(FilterOutputStream.class.getName().replace(".", "/"));
            add(FileInputStream.class.getName().replace(".", "/"));
            add(FileOutputStream.class.getName().replace(".", "/"));
            add(RandomAccessFile.class.getName().replace(".", "/"));
            add("java.net.PlainSocketImpl".replace(".", "/"));
            add(ZipFile.class.getName().replace(".", "/"));
            add(AbstractSelectableChannel.class.getName().replace(".", "/"));
            add(AbstractInterruptibleChannel.class.getName().replace(".", "/"));
            add(FileChannel.class.getName().replace(".", "/"));
            add(AbstractSelector.class.getName().replace(".", "/"));

        }
    };
    private ClassLoader loader;

    private final Map<String, ClassTransformSpec> specs = new HashMap<String, ClassTransformSpec>();

    public TransformerImpl(Collection<ClassTransformSpec> specs) {
        for (ClassTransformSpec spec : specs) {
            this.specs.put(spec.name, spec);
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        this.loader = loader;
        return transform(className, classBeingRedefined, classfileBuffer);
    }

    public byte[] transform(String className, Class<?> classBeingRedefined, byte[] classfileBuffer) {
        if (names.contains(className)) {
            final ClassTransformSpec cs = specs.get(className);
            if (cs == null) {
                return classfileBuffer;
            }
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(/*ClassWriter.COMPUTE_FRAMES|*/ClassWriter.COMPUTE_MAXS);
            cr.accept(new ClassVisitor(ASM6, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor base = super.visitMethod(access, name, desc, signature, exceptions);

                    MethodTransformSpec ms = cs.methodSpecs.get(name + desc);
                    if (ms == null) {
                        ms = cs.methodSpecs.get(name + "*");
                    }
                    if (ms == null) {
                        return base;
                    }

                    return ms.newAdapter(base, access, name, desc, signature, exceptions);
                }
            }, SKIP_FRAMES);

            return cw.toByteArray();
        }

        if (names1.contains(className)) {

//            if (className.equals(Exception.class.getTypeName().replace(".", "/"))) {
//                return createThrowable(className, classBeingRedefined, classfileBuffer);
//            }
        }

        return classfileBuffer;
    }

    private byte[] createThrowable(String className, Class<?> classBeingRedefined, byte[] classfileBuffer) {
        ClassPool classPool = ClassPool.getDefault();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        classPool.insertClassPath(new LoaderClassPath(contextClassLoader));
        try {
            contextClassLoader.loadClass(ExceptionPlugin.class.getTypeName());
            classPool.importPackage(Method.class.getTypeName());
            classPool.importPackage(System.class.getTypeName());
            classPool.importPackage(ExceptionPlugin.class.getTypeName());
            CtClass ctClass = classPool.get(classBeingRedefined.getTypeName());

//            CtConstructor declaredConstructor0 = ctClass.getDeclaredConstructor(new CtClass[0]);
//            declaredConstructor0.insertAfter("System.out.println(\"---结束执行1---\");");
//
            CtConstructor declaredConstructor1 = ctClass.getDeclaredConstructor(new CtClass[]{classPool.get(String.class.getTypeName())});
            declaredConstructor1.insertAfter("{" +
                    "Class type = ClassLoader.getSystemClassLoader().loadClass(\"com.chua.agent.support.plugin.ExceptionAgentPlugin\");" +
                    "Method method = type.getDeclaredMethod(\"register\", new Class[]{Object.class});" +
                    "method.setAccessible(true);" +
                    "method.invoke(null, new Object[]{this});}");

            CtConstructor declaredConstructor2 = ctClass.getDeclaredConstructor(new CtClass[]{classPool.get(String.class.getTypeName()), classPool.get(Throwable.class.getTypeName())});
            declaredConstructor2.insertAfter("{" +
                    "Class type = ClassLoader.getSystemClassLoader().loadClass(\"com.chua.agent.support.plugin.ExceptionAgentPlugin\");" +
                    "Method method = type.getDeclaredMethod(\"register\", new Class[]{Object.class});" +
                    "method.setAccessible(true);" +
                    "method.invoke(null, new Object[]{this});}");

            CtConstructor declaredConstructor11 = ctClass.getDeclaredConstructor(new CtClass[]{classPool.get(Throwable.class.getTypeName())});
            declaredConstructor1.insertAfter("{" +
                    "Class type = ClassLoader.getSystemClassLoader().loadClass(\"com.chua.agent.support.plugin.ExceptionAgentPlugin\");" +
                    "Method method = type.getDeclaredMethod(\"register\", new Class[]{Object.class});" +
                    "method.setAccessible(true);" +
                    "method.invoke(null, new Object[]{this});}");
            return ctClass.toBytecode();
        } catch (Exception e) {
            return classfileBuffer;
        }

    }

    private byte[] createPrintStream(String className, Class<?> classBeingRedefined, byte[] classfileBuffer) {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        classPool.importPackage(Logger.class.getTypeName());
        classPool.importPackage(Method.class.getTypeName());
        classPool.importPackage(Class.class.getTypeName());
        classPool.importPackage(InterruptedIOException.class.getTypeName());
        classPool.importPackage(IOException.class.getTypeName());
        try {
            CtClass ctClass = classPool.get(classBeingRedefined.getTypeName());
            CtMethod writeString = ctClass.getDeclaredMethod("write", new CtClass[]{classPool.get(String.class.getTypeName())});
            writeString.setBody(
                    "{  " +
                            "       try {\n" +
                            "            synchronized (this) {\n" +
                            "   ensureOpen();\n" +
                            "   textOut.write($1);\n" +
                            "   textOut.flushBuffer();\n" +
                            "   charOut.flushBuffer();\n" +
                            "   if (autoFlush && ($1.indexOf('\\n') >= 0)) {\n" +
                            "       out.flush();\n" +
                            "   }\n" +
                            "  try{\n" +
                            "      Class aClass = ClassLoader.getSystemClassLoader().loadClass(\"com.chua.tools.agentv2.transform.Listener\");\n" +
                            "      Method registerIntoSlf4jPlugin = aClass.getDeclaredMethod(\"registerIntoSlf4jPlugin\", new Class[]{Object.class, Object.class});\n" +
                            "      registerIntoSlf4jPlugin.setAccessible(true);\n" +
                            "      registerIntoSlf4jPlugin.invoke(null, new Object[]{this, $1});" +
                            "  }catch(Exception ignore) {}\n" +
                            "            }\n" +
                            "        }\n" +
                            "        catch (InterruptedIOException x) {\n" +
                            "            Thread.currentThread().interrupt();\n" +
                            "        }\n" +
                            "        catch (IOException x) {\n" +
                            "            trouble = true;\n" +
                            "        }" +
                            "}");

            CtMethod writeInt = ctClass.getDeclaredMethod("write", new CtClass[]{classPool.get(int.class.getTypeName())});
            writeInt.setBody(
                    "{  " +
                            "       try {\n" +
                            "            synchronized (this) {\n" +
                            "   ensureOpen();\n" +
                            "   out.write($1);\n" +
                            "   if (($1 == '\\n') && autoFlush){\n" +
                            "       out.flush();\n" +
                            "   }\n" +
                            "  try{\n" +
                            "      Class aClass = ClassLoader.getSystemClassLoader().loadClass(\"com.chua.tools.agentv2.transform.Listener\");\n" +
                            "      Method registerIntoSlf4jPlugin = aClass.getDeclaredMethod(\"registerIntoSlf4jPlugin\", new Class[]{Object.class, Object.class});\n" +
                            "      registerIntoSlf4jPlugin.setAccessible(true);\n" +
                            "      registerIntoSlf4jPlugin.invoke(null, new Object[]{this, $1});" +
                            "  }catch(Exception ignore) {}\n" +
                            "            }\n" +
                            "        }\n" +
                            "        catch (InterruptedIOException x) {\n" +
                            "            Thread.currentThread().interrupt();\n" +
                            "        }\n" +
                            "        catch (IOException x) {\n" +
                            "            trouble = true;\n" +
                            "        }" +
                            "}");

            CtMethod writeCharArray = ctClass.getDeclaredMethod("write", new CtClass[]{classPool.get(char[].class.getTypeName())});
            writeCharArray.setBody(
                    "{  " +
                            "       try {\n" +
                            "            synchronized (this) {\n" +
                            "   ensureOpen();\n" +
                            "   textOut.write($1);\n" +
                            "   textOut.flushBuffer();\n" +
                            "   charOut.flushBuffer();\n" +
                            "   if (autoFlush) {\n" +
                            "       for (int i = 0; i < $1.length; i++)\n" +
                            "           if ($1[i] == '\\n')\n" +
                            "               out.flush();\n" +
                            "   }\n" +
                            "  try{\n" +
                            "      Class aClass = ClassLoader.getSystemClassLoader().loadClass(\"com.chua.tools.agentv2.transform.Listener\");\n" +
                            "      Method registerIntoSlf4jPlugin = aClass.getDeclaredMethod(\"registerIntoSlf4jPlugin\", new Class[]{Object.class, Object.class});\n" +
                            "      registerIntoSlf4jPlugin.setAccessible(true);\n" +
                            "      registerIntoSlf4jPlugin.invoke(null, new Object[]{this, $1});" +
                            "  }catch(Exception ignore) {}\n" +
                            "            }\n" +
                            "        }\n" +
                            "        catch (InterruptedIOException x) {\n" +
                            "            Thread.currentThread().interrupt();\n" +
                            "        }\n" +
                            "        catch (IOException x) {\n" +
                            "            trouble = true;\n" +
                            "        }" +
                            "}");
            return ctClass.toBytecode();
        } catch (Exception e) {
            return classfileBuffer;
        }
    }
}
