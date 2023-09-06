package com.chua.agent.support.transform;

import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;

import static net.bytebuddy.jar.asm.Opcodes.*;

/**
 * Convenience method to generate bytecode.
 *
 * @author Kohsuke Kawaguchi
 */
public class CodeGenerator extends MethodVisitor {
    public CodeGenerator(MethodVisitor mv) {
        super(ASM6, mv);
    }

    public void println(String msg) {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        ldc(msg);
        invokeVirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
    }

    public void doNull() {
        super.visitInsn(ACONST_NULL);
    }

    public void newArray(String type, int size) {
        iconst(size);
        super.visitTypeInsn(ANEWARRAY, type);
    }

    public void iconst(int i) {
        if (i <= 5) {
            super.visitInsn(ICONST_0 + i);
        } else {
            super.visitLdcInsn(i);
        }
    }

    public void dup() {
        super.visitInsn(DUP);
    }

    public void aastore() {
        super.visitInsn(AASTORE);
    }

    public void aload(int i) {
        super.visitIntInsn(ALOAD, i);
    }

    public void astore(int i) {
        super.visitIntInsn(ASTORE, i);
    }

    public void pop() {
        super.visitInsn(POP);
    }

    public void ldc(Object o) {
        if (o.getClass() == Class.class) {
            o = Type.getType((Class<?>) o);
        }
        super.visitLdcInsn(o);
    }

    public void invokeVirtual(String owner, String name, String desc) {
        super.visitMethodInsn(INVOKEVIRTUAL, owner, name, desc, false);
    }

    /**
     * Invokes a static method on the class in the system classloader.
     * <p>
     * This is used for instrumenting classes in the bootstrap classloader,
     * which cannot see the classes in the system classloader.
     */
//    public void invokeAppStatic(String userClassName, String userMethodName, Class[] argTypes, int[] localIndex) {
//        visitMethodInsn(INVOKESTATIC,"java/lang/ClassLoader","getSystemClassLoader","()Ljava/lang/ClassLoader;");
//        ldc(userClassName);
//        invokeVirtual("java/lang/ClassLoader","loadClass","(Ljava/lang/String;)Ljava/lang/Class;");
//        ldc(userMethodName);
//        newArray("java/lang/Class",0);
////        for (int i = 0; i < argTypes.length; i++)
////            storeConst(i, argTypes[i]);
//
//        invokeVirtual("java/lang/Class","getDeclaredMethod","(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
//        pop();
//    }
    public void invokeAppStatic(Class<?> userClass, String userMethodName, Class<?>[] argTypes, int[] localIndex) {
        invokeAppStatic(userClass.getName(), userMethodName, argTypes, localIndex);
    }

    public void invokeAppStatic(String userClassName, String userMethodName, Class<?>[] argTypes, int[] localIndex) {
        Label s = new Label();
        Label e = new Label();
        Label h = new Label();
        Label tail = new Label();
        visitTryCatchBlock(s, e, h, "java/lang/Exception");
        visitLabel(s);
        // [RESULT] m = ClassLoader.getSystemClassLoadeR().loadClass($userClassName).getDeclaredMethod($userMethodName,[...]);
        visitMethodInsn(INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;", false);
        ldc(userClassName);
        invokeVirtual("java/lang/ClassLoader", "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
        ldc(userMethodName);
        newArray("java/lang/Class", argTypes.length);
        for (int i = 0; i < argTypes.length; i++) {
            storeConst(i, argTypes[i]);
        }

        invokeVirtual("java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");

        // [RESULT] m.invoke(null,new Object[]{this,file})
        doNull();
        newArray("java/lang/Object", argTypes.length);

        for (int i = 0; i < localIndex.length; i++) {
            dup();
            iconst(i);
            aload(localIndex[i]);
            aastore();
        }

        invokeVirtual("java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        pop();
        doGoto(tail);

        visitLabel(e);
        visitLabel(h);

        // [RESULT] catch(e) { e.printStackTrace(System.out); }
        visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        invokeVirtual("java/lang/Exception", "printStackTrace", "(Ljava/io/PrintStream;)V");

        visitLabel(tail);
    }

    /**
     * When the stack top is an array, store a constant to the known index of the array.
     * <p>
     * ..., array => ..., array
     */
    private void storeConst(int idx, Object type) {
        dup();
        iconst(idx);
        ldc(type);
        aastore();
    }

    public void doGoto(Label l) {
        visitJumpInsn(GOTO, l);
    }

    public void ifFalse(Label label) {
        visitJumpInsn(IFEQ, label);
    }

    public void athrow() {
        visitInsn(ATHROW);
    }
}
