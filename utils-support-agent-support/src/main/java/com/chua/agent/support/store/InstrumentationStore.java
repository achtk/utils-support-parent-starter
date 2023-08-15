package com.chua.agent.support.store;

import com.chua.agent.support.transform.Spec;
import com.chua.agent.support.transform.TransformerImpl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.instrument.Instrumentation;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * vm
 *
 * @author CH
 */
public class InstrumentationStore {

    private static Instrumentation instrumentation = AgentStore.instrumentation;

    /**
     * 实体化Instrumentation
     */
    public static void installInstrumentation() {
        instrumentation.addTransformer(new TransformerImpl(Spec.createSpec()), true);
    }

    /**
     * 实体化Instrumentation
     */
    public static void preInstrumentation() {
        List<Class<?>> rt = new ArrayList<>(Arrays.asList(
                FileInputStream.class,
                FileOutputStream.class,
                RandomAccessFile.class,
                Exception.class,
                ZipFile.class,
                AbstractSelectableChannel.class,
                AbstractInterruptibleChannel.class,
                FileChannel.class,
                PrintStream.class,
                AbstractSelector.class
        ));
        try {
            Class<?> aClass = Class.forName("java.net.PlainSocketImpl");
            rt.add(aClass);
        } catch (ClassNotFoundException ignored) {
        }
        try {
            instrumentation.retransformClasses(rt.toArray(new Class[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
