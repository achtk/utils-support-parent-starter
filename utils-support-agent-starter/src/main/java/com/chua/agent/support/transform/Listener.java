package com.chua.agent.support.transform;

import com.chua.agent.support.plugin.LogPlugin;
import com.chua.agent.support.span.Span;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * Intercepted JDK calls land here.
 *
 * @author Kohsuke Kawaguchi
 */
public class Listener {


    /**
     * Files that are currently open, keyed by the owner object (like {@link FileInputStream}.
     */
    private static Map<Object, Span> TABLE = new WeakHashMap<>();

    /**
     * Trace the open/close op
     */
    public static PrintWriter TRACE = null;

    /**
     * Trace the "too many open files" error here
     */
    public static PrintWriter ERROR = new PrintWriter(System.err);

    /**
     * Allows to provide stacktrace-lines which cause the element to be excluded
     */
    public static final List<String> EXCLUDES = new ArrayList<String>();

    /**
     * Tracing may cause additional files to be opened.
     * In such a case, avoid infinite recursion.
     */
    private static boolean tracing = false;

    /**
     * If the table size grows beyond this, report the table
     */
    public static int THRESHOLD = 999999;

    /**
     * Is the agent actually transforming the class files?
     */
    /*package*/ static boolean AGENT_INSTALLED = false;

    /**
     * Returns true if the leak detector agent is running.
     */
    public static boolean isAgentInstalled() {
        return AGENT_INSTALLED;
    }

    public static synchronized void makeStrong() {
        TABLE = new LinkedHashMap<Object, Span>(TABLE);
    }

    /**
     * Called when a new file is opened.
     *
     * @param o {@link FileInputStream}, {@link FileOutputStream}, {@link RandomAccessFile}, or {@link ZipFile}.
     * @param f File being opened.
     */
    public static synchronized void open(Object o, File f) {
        Span span = new Span();
        span.setMessage("Opened " + f + " by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
        put1(o, span);
    }

    public static synchronized void openPipe(Object o) {
        if (o instanceof Pipe.SourceChannel) {
            Span span = new Span();
            span.setMessage("Opened Pipe Source Channel by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
            put1(o, span);
        }
        if (o instanceof Pipe.SinkChannel) {
            Span span = new Span();
            span.setMessage("Opened Pipe Sink Channel by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
            put1(o, span);
        }
    }

    public static synchronized void open_filechannel(FileChannel fileChannel, Path path) {
        open(fileChannel, path.toFile());
    }

    public static synchronized void openSelector(Object o) {
        Span span = new Span();
        if (o instanceof Selector) {
            span.setMessage("Opened selector by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
            put1(o, span);
        }
    }

    /**
     * Called when a socket is opened.
     */
    public static synchronized void openSocket(Object o) {
        // intercept when
        if (o instanceof SocketImpl) {
            try {
                // one of the following must be true
                SocketImpl si = (SocketImpl) o;
                Socket s = (Socket) SOCKETIMPL_SOCKET.get(si);
                if (s != null) {
                    Span span = new Span();
                    span.setMessage("Opened socket to " + s.getRemoteSocketAddress().toString() + "  by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
                    put1(o, span);
                }
                ServerSocket ss = (ServerSocket) SOCKETIMPL_SERVER_SOCKET.get(si);
                if (ss != null) {
                    Span span = new Span();
                    span.setMessage("Opened socket at " + ss.getLocalSocketAddress().toString() + "  by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
                    put1(o, span);
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
        if (o instanceof SocketChannel) {
            Span span = new Span();
            span.setMessage("Opened socket channel by thread:" + Thread.currentThread().getName() + " on " + format(System.currentTimeMillis()));
            put1(o, span);
        }
    }

    public static synchronized List<Span> getCurrentOpenFiles() {
        return new ArrayList<Span>(TABLE.values());
    }

    private static synchronized void put1(Object o, Span r) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        List<StackTraceElement> item = new LinkedList<>();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (
                    (null != stackTraceElement.getFileName()
                            && "Listener.java".equals(stackTraceElement.getFileName())) ||
                            (null != stackTraceElement.getClassName() &&
                                    (stackTraceElement.getClassName().startsWith("com.chua.tools.agentv2") ||
                                            stackTraceElement.getClassName().startsWith("net.bytebuddy")))
            ) {
                continue;
            }
            item.add(stackTraceElement);
        }
        r.setStackTrace(item.toArray(new StackTraceElement[0]));
        TABLE.put(o, r);
        if (TABLE.size() > THRESHOLD) {
            THRESHOLD = 999999;
            dump(ERROR);
        }
        if (TRACE != null && !tracing) {
            tracing = true;
            dump("Opened ", TRACE, r);
            tracing = false;
        }
    }

    public static void dump(String prefix, PrintWriter pw, Span r) {
        Exception stackTrace = new Exception();
        StackTraceElement[] trace = stackTrace.getStackTrace();
        int i = 0;
        // skip until we find the Method.invoke() that called us
        for (; i < trace.length; i++) {
            if ("java.lang.reflect.Method".equals(trace[i].getClassName())) {
                i++;
                break;
            }
        }
        // print the rest
        for (; i < trace.length; i++) {
            pw.println("\tat " + trace[i]);
        }
        pw.flush();
    }

    /**
     * Called when a file is closed.
     * <p>
     * This method tolerates a double-close where a close method is called on an already closed object.
     *
     * @param o {@link FileInputStream}, {@link FileOutputStream}, {@link RandomAccessFile}, {@link Socket}, {@link ServerSocket}, or {@link ZipFile}.
     */
    public static synchronized void close(Object o) {
        Span r = TABLE.remove(o);
        if (r != null && TRACE != null && !tracing) {
            tracing = true;
            dump("Closed ", TRACE, r);
            tracing = false;
        }
    }


    /**
     * Dumps all files that are currently open.
     */
    public static synchronized void dump(OutputStream out) {
        dump(new OutputStreamWriter(out));
    }

    public static final String[] IGNORES = new String[]{
            "sun.",
            "com.sun.",
            "org.java",
            "org.apache.",
            "ch.qos.logback.",
            "org.slf4j.",
            "org.springframework.",
            "java.",
    };

    /**
     * Dumps all files that are currently open.
     */
    public static synchronized String title() {
        StringBuilder sb = new StringBuilder();
        Span[] records = TABLE.values().toArray(new Span[0]);
        for (Span r1 : records) {
            String message = r1.getMessage();
            int index = message.indexOf(" by ");
            if (index > -1) {
                sb.append(message.substring(0, index).replace("Opened", "").trim());
            } else {
                sb.append(message);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Dumps all files that are currently open.
     */
    public static synchronized String dump() {
        StringBuilder sb = new StringBuilder();
        Span[] records = TABLE.values().toArray(new Span[0]);

        sb.append(" <h3>发现句柄数: <span style='color:red;'>").append(records.length).append("</span></h3>----\r\n").append(records.length).append(" descriptors are open----\r\n");

        for (Span r1 : records) {
            List<String> stack = r1.getStack();
            sb.append(
                            r1.getMessage()
                                    .replace("Opened ", "Opend <span style='color:red;'>")
                                    .replace(" by ", " </span>by ")
                                    .replace(" thread:", " thread:[<span style='color:blue;'>")
                                    .replace(" on ", "</span>]  on ")
                    )
                    .append("\r\n");
            for (int i = 0; i < stack.size(); i++) {
                sb.append("\tat ").append(analysis(stack.get(i))).append("\r\n");
            }
            sb.append("----\r\n");
        }

        return sb.toString();

    }

    private static String analysis(String s) {
        for (String ignore : IGNORES) {
            if (s.startsWith(ignore)) {
                return s;
            }
        }
        return "<span style='color:red'>" + s + "</span>";
    }

    public static synchronized void dump(Writer w) {
        PrintWriter pw = new PrintWriter(w);
        Span[] records = TABLE.values().toArray(new Span[0]);

        pw.println(records.length + " descriptors are open");
        int i = 0;
        for (Span r1 : records) {
            List<String> stack = r1.getStack();
            for (; i < stack.size(); i++) {
                pw.println("\tat " + stack.get(0));
            }
            pw.println("----");
        }
        pw.flush();
    }

    /**
     * Called when the system has too many open files.
     */
    public static synchronized void outOfDescriptors() {
        if (ERROR != null && !tracing) {
            tracing = true;
            ERROR.println("Too many open files");
            dump(ERROR);
            tracing = false;
        }
    }

    public static String format(long time) {
        try {
            return new Date(time).toString();
        } catch (Exception e) {
            return Long.toString(time);
        }
    }

    private static Field SOCKETIMPL_SOCKET, SOCKETIMPL_SERVER_SOCKET;

    static {
        try {
            SOCKETIMPL_SOCKET = SocketImpl.class.getDeclaredField("socket");
            SOCKETIMPL_SERVER_SOCKET = SocketImpl.class.getDeclaredField("serverSocket");
            SOCKETIMPL_SOCKET.setAccessible(true);
            SOCKETIMPL_SERVER_SOCKET.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }


    private static void registerIntoSlf4jPlugin(Object thisObject, Object args) {
        String type = getType(Thread.currentThread().getStackTrace());
        if (args instanceof String) {
            if (!((String) args).startsWith("\u001B[32m")) {
                args = args.toString().replace(" ", "<span style='margin-left: 10px'></span>");
            }
        }

        if (!"".equals(type) && !type.contains("Banner")) {
            LogPlugin.registerSlf4j(new Object[]{"\u001b[38;5;35m" + type + "\u001b[0;m"});
        }
        LogPlugin.registerSlf4j(new Object[]{args});
    }

    private static String getType(StackTraceElement[] stackTrace) {
        boolean isFind = false;
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            if ("java.io.PrintStream".equals(className)) {
                isFind = true;
                continue;
            }

            if (!isFind) {
                continue;
            }
            String methodName = stackTraceElement.getMethodName();
            if (methodName.contains("$")) {
                methodName = methodName.substring(0, methodName.indexOf("$"));
            }
            return className + "." + methodName + ":" + stackTraceElement.getLineNumber();
        }
        return "";
    }
}
