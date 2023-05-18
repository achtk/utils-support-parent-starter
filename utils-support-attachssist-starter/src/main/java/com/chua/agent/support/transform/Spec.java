package com.chua.agent.support.transform;

import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.commons.LocalVariablesSorter;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;

import static net.bytebuddy.jar.asm.Opcodes.*;

/**
 * @author CH
 * @since 2022-02-11
 */
public class Spec {

    public static List<ClassTransformSpec> createSpec() {
        return Arrays.asList(
                newSpec(FileOutputStream.class, "(Ljava/io/File;Z)V"),
                newSpec(FileInputStream.class, "(Ljava/io/File;)V"),
                newSpec(RandomAccessFile.class, "(Ljava/io/File;Ljava/lang/String;)V"),
                newSpec(ZipFile.class, "(Ljava/io/File;I)V"),

                /*
                 * Detect the files opened via FileChannel.open(...) calls
                 */
                new ClassTransformSpec(FileChannel.class,
                        new ReturnFromStaticMethodInterceptor("open",
                                "(Ljava/nio/file/Path;Ljava/util/Set;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/channels/FileChannel;", 4, "open_filechannel", FileChannel.class, Path.class)),
                /*
                 * Detect new Pipes
                 */
                new ClassTransformSpec(AbstractSelectableChannel.class,
                        new ConstructorInterceptor("(Ljava/nio/channels/spi/SelectorProvider;)V", "openPipe")),
                /*
                 * AbstractInterruptibleChannel is used by FileChannel and Pipes
                 */
                new ClassTransformSpec(AbstractInterruptibleChannel.class,
                        new CloseInterceptor("close")),

                /**
                 * Detect selectors, which may open native pipes and anonymous inodes for event polling.
                 */
                new ClassTransformSpec(AbstractSelector.class,
                        new ConstructorInterceptor("(Ljava/nio/channels/spi/SelectorProvider;)V", "openSelector"),
                        new CloseInterceptor("close")),

            /*
                java.net.Socket/ServerSocket uses SocketImpl, and this is where FileDescriptors
                are actually managed.

                SocketInputStream/SocketOutputStream does not maintain a separate FileDescritor.
                They just all piggy back on the same SocketImpl instance.
             */
                new ClassTransformSpec("java/net/PlainSocketImpl",
                        // this is where a new file descriptor is allocated.
                        // it'll occupy a socket even before it gets connected
                        new OpenSocketInterceptor("create", "(Z)V"),

                        // When a socket is accepted, it goes to "accept(SocketImpl s)"
                        // where 's' is the new socket and 'this' is the server socket
                        new AcceptInterceptor("accept", "(Ljava/net/SocketImpl;)V"),

                        // file descriptor actually get closed in socketClose()
                        // socketPreClose() appears to do something similar, but if you read the source code
                        // of the native socketClose0() method, then you see that it actually doesn't close
                        // a file descriptor.
                        new CloseInterceptor("socketClose")
                ),
                // Later versions of the JDK abstracted out the parts of PlainSocketImpl above into a super class
                new ClassTransformSpec("java/net/AbstractPlainSocketImpl",
                        new OpenSocketInterceptor("create", "(Z)V"),
                        new AcceptInterceptor("accept", "(Ljava/net/SocketImpl;)V"),
                        new CloseInterceptor("socketClose")
                ),
                new ClassTransformSpec("sun/nio/ch/SocketChannelImpl",
                        new OpenSocketInterceptor("<init>", "(Ljava/nio/channels/spi/SelectorProvider;Ljava/io/FileDescriptor;Ljava/net/InetSocketAddress;)V"),
                        new OpenSocketInterceptor("<init>", "(Ljava/nio/channels/spi/SelectorProvider;)V"),
                        new CloseInterceptor("kill")
                )
        );
    }

    /**
     * Creates {@link ClassTransformSpec} that intercepts
     * a constructor and the close method.
     */
    private static ClassTransformSpec newSpec(final Class<?> c, String constructorDesc) {
        final String binName = c.getName().replace('.', '/');
        return new ClassTransformSpec(binName,
                new ConstructorOpenInterceptor(constructorDesc, binName),
                new CloseInterceptor("close")
        );
    }

    /**
     * Intercepts a void method used to close a handle and calls  in the end.
     */
    private static class CloseInterceptor extends MethodAppender {
        public CloseInterceptor(String methodName) {
            super(methodName, "()V");
        }

        @Override
        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class, "close",
                    new Class[]{Object.class},
                    new int[]{0});
        }
    }

    /**
     * Intercepts a constructor invocation and calls the given method on {@link Listener} at the end of the constructor.
     */
    private static class ConstructorInterceptor extends MethodAppender {
        private final String listenerMethod;

        public ConstructorInterceptor(String constructorDesc, String listenerMethod) {
            super("<init>", constructorDesc);
            this.listenerMethod = listenerMethod;
        }

        @Override
        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class, listenerMethod,
                    new Class[]{Object.class},
                    new int[]{0});
        }
    }

    private static class OpenSocketInterceptor extends MethodAppender {
        public OpenSocketInterceptor(String name, String desc) {
            super(name, desc);
        }

        @Override
        public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
            return new OpenInterceptionAdapter(b, access, desc) {
                @Override
                protected boolean toIntercept(String owner, String name) {
                    return "socketCreate".equals(name);
                }
            };
        }

        @Override
        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class, "openSocket",
                    new Class[]{Object.class},
                    new int[]{0});
        }
    }

    /**
     * Used to intercept
     * java.net.PlainSocketImpl#accept(SocketImpl)
     */
    private static class AcceptInterceptor extends MethodAppender {
        public AcceptInterceptor(String name, String desc) {
            super(name, desc);
        }

        @Override
        public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
            return new OpenInterceptionAdapter(b, access, desc) {
                @Override
                protected boolean toIntercept(String owner, String name) {
                    return "socketAccept".equals(name);
                }
            };
        }

        @Override
        protected void append(CodeGenerator g) {
            // the 's' parameter is the new socket that will own the socket
            g.invokeAppStatic(Listener.class, "openSocket",
                    new Class[]{Object.class},
                    new int[]{1});
        }
    }

    /**
     * Rewrites a method that includes a call to a native method that actually opens a file descriptor
     * (therefore it can throw "too many open files" exception.)
     * <p>
     * surround the call with try/catch, and if "too many open files" exception is thrown
     * call {@link Listener#outOfDescriptors()}.
     */
    private static abstract class OpenInterceptionAdapter extends MethodVisitor {
        private final LocalVariablesSorter lvs;
        private final MethodVisitor base;

        private OpenInterceptionAdapter(MethodVisitor base, int access, String desc) {
            super(ASM5);
            lvs = new LocalVariablesSorter(access, desc, base);
            mv = lvs;
            this.base = base;
        }

        /**
         * Decide if this is the method that needs interception.
         */
        protected abstract boolean toIntercept(String owner, String name);

        protected Class<? extends Exception> getExpectedException() {
            return IOException.class;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (toIntercept(owner, name)) {
                Type exceptionType = Type.getType(getExpectedException());

                CodeGenerator g = new CodeGenerator(mv);
                Label s = new Label(); // start of the try block
                Label e = new Label();  // end of the try block
                Label h = new Label();  // handler entry point
                Label tail = new Label();   // where the execution continue

                g.visitTryCatchBlock(s, e, h, exceptionType.getInternalName());
                g.visitLabel(s);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                g.doGoto(tail);

                g.visitLabel(e);
                g.visitLabel(h);
                // [RESULT]
                // catch(E ex) {
                //    boolean b = ex.getMessage().contains("Too many open files");
                int ex = lvs.newLocal(exceptionType);
                g.dup();
                base.visitVarInsn(ASTORE, ex);
                g.invokeVirtual(exceptionType.getInternalName(), "getMessage", "()Ljava/lang/String;");
                g.ldc("Too many open files");
                g.invokeVirtual("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");

                // too many open files detected
                //    if (b) { Listener.outOfDescriptors() }
                Label rethrow = new Label();
                g.ifFalse(rethrow);

                g.invokeAppStatic(Listener.class, "outOfDescriptors",
                        new Class[0], new int[0]);

                // rethrow the FileNotFoundException
                g.visitLabel(rethrow);
                base.visitVarInsn(ALOAD, ex);
                g.athrow();

                // normal execution continues here
                g.visitLabel(tail);
            } else
            // no processing
            {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    /**
     * Intercepts the this.open(...) call in the constructor.
     */
    private static class ConstructorOpenInterceptor extends MethodAppender {
        /**
         * Binary name of the class being transformed.
         */
        private final String binName;

        public ConstructorOpenInterceptor(String constructorDesc, String binName) {
            super("<init>", constructorDesc);
            this.binName = binName;
        }

        @Override
        public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
            return new OpenInterceptionAdapter(b, access, desc) {
                @Override
                protected boolean toIntercept(String owner, String name) {
                    return owner.equals(binName) && name.startsWith("open");
                }

                @Override
                protected Class<? extends Exception> getExpectedException() {
                    return FileNotFoundException.class;
                }
            };
        }

        @Override
        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class, "open",
                    new Class[]{Object.class, File.class},
                    new int[]{0, 1});
        }
    }

    private static class ReturnFromStaticMethodInterceptor extends MethodAppender {
        private final String listenerMethod;
        private final Class<?>[] listenerMethodArgs;
        private final int returnLocalVarIndex;

        public ReturnFromStaticMethodInterceptor(String methodName, String methodDesc, int returnLocalVarIndex,
                                                 String listenerMethod, Class<?>... listenerMethodArgs) {
            super(methodName, methodDesc);
            this.returnLocalVarIndex = returnLocalVarIndex;
            this.listenerMethod = listenerMethod;
            if (listenerMethodArgs.length == 0) {
                this.listenerMethodArgs = new Class[]{Object.class};
            } else {
                this.listenerMethodArgs = listenerMethodArgs;
            }
        }

        @Override
        protected void append(CodeGenerator g) {
            int[] index = new int[listenerMethodArgs.length];
            // first parameter is from the additional local variable, that holds
            // the return value of the intercepted method
            index[0] = returnLocalVarIndex;
            // remaining parameters
            for (int i = 1; i < index.length; i++) {
                index[i] = i - 1;
            }

            Label start = new Label();
            Label end = new Label();
            g.visitLocalVariable("result", "java/lang/Object", null, start, end, returnLocalVarIndex);
            g.visitLabel(start);

            // return value is currently on top of the stack
            // result = {return value}
            g.astore(returnLocalVarIndex);

            g.invokeAppStatic(Listener.class, listenerMethod, listenerMethodArgs, index);

            g.visitLabel(end);

            // restore the stack so that the ARETURN has something to return
            g.aload(returnLocalVarIndex);
        }
    }
}
