package com.chua.common.support.extra.el.baseutil.smc.compiler;

import com.chua.common.support.extra.el.baseutil.smc.model.ClassModel;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * In-memory compile Java source code as String.
 *
 * @author michael
 */
public class CompileHelper
{
    private final        MemoryClassLoader     memoryClassLoader;
    private final        JavaCompiler          compiler;
    private final        MemoryJavaFileManager manager;

    public CompileHelper()
    {
        this(Thread.currentThread().getContextClassLoader());
    }

    public CompileHelper(ClassLoader classLoader)
    {
        this(classLoader, null);
    }

    public CompileHelper(ClassLoader classLoader, JavaCompiler compiler)
    {
        if (compiler == null)
        {
            compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null)
            {
                throw new NullPointerException("当前处于JRE环境无法获得JavaCompiler实例。如果是在windows，可以将JDK/lib目录下的tools.jar拷贝到jre/lib目录。如果是linux，将JAVA_HOME设置为jdk的");
            }
        }
        this.compiler = compiler;
        memoryClassLoader = new MemoryClassLoader(classLoader);
        manager = new MemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));
    }

    public synchronized Class<?> compile(ClassModel classModel) throws IOException, ClassNotFoundException
    {
        try
        {
            String          source         = classModel.toStringWithLineNo();
            JavaFileObject  javaFileObject = manager.makeStringSource(classModel.fileName(), source);
            StringWriter    writer         = new StringWriter();
            CompilationTask task           = compiler.getTask(writer, manager, null, null, null, Arrays.asList(javaFileObject));
            Boolean         result         = task.call();
            if (result == null || !result.booleanValue())
            {
                throw new RuntimeException("Compilation failed.The error is \r\n" + writer.toString() + "\r\nThe source is \r\n" + source);
            }
            memoryClassLoader.addClassBytes(manager.getClassBytes());
            return memoryClassLoader.loadClass(classModel.getPackageName() + "." + classModel.className());
        }
        finally
        {
            manager.clear();
        }
    }
}
