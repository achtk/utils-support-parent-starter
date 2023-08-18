package com.chua.agent.support.span;

import com.chua.agent.support.formatter.DmlFormatter;
import com.chua.agent.support.json.JSON;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 */
@Data
public class Span implements Serializable {

    private String linkId;  //链路ID
    private String id;  //链路ID
    private String pid;  //链路ID
    private Date enterTime; //方法进入时间
    private long costTime;//耗时
    private String message;
    private List<String> stack;
    private List<String> header;
    private String method;
    private String typeMethod;
    private String type;
    private boolean title;
    private String ex;
    private String error;
    private String db;
    private String model;
    private String threadName;
    private String from;

    private List<Span> children;
    private transient volatile Set<String> parents = new LinkedHashSet<>();


    public Span() {
        this.enterTime = new Date();
        this.threadName = Thread.currentThread().getName();
    }

    public Span(String linkId) {
        this.linkId = linkId;
        this.enterTime = new Date();
        setStackTrace(Thread.currentThread().getStackTrace());
        this.threadName = Thread.currentThread().getName();
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        List<String> rs = new LinkedList<>();
        for (int i = 0, stackTraceLength = stackTrace.length; i < stackTraceLength; i++) {
            StackTraceElement element = stackTrace[i];
            String className = element.getClassName();
            if (className.startsWith("com.chua.agent.support")) {
                continue;
            }

            String string = element.toString();
            rs.add(string);
            if (i < 1 || string.startsWith("sun") || string.contains("$") || string.contains("<init>") || string.startsWith("java")) {
                continue;
            }

            int i1 = string.indexOf("(");
            parents.add(i1 > -1 ? string.substring(0, i1) : string);
        }
        this.stack = rs;
    }


    public String toJson() {
        return JSON.toJSONString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Span span = (Span) o;
        return costTime == span.costTime &&
                title == span.title &&
                Objects.equals(linkId, span.linkId) &&
                Objects.equals(id, span.id) &&
                Objects.equals(pid, span.pid) &&
                Objects.equals(enterTime, span.enterTime) &&
                Objects.equals(message, span.message) &&
                Objects.equals(stack, span.stack) &&
                Objects.equals(method, span.method) &&
                Objects.equals(typeMethod, span.typeMethod) &&
                Objects.equals(type, span.type) &&
                Objects.equals(ex, span.ex) &&
                Objects.equals(db, span.db);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkId, id, pid, enterTime, costTime, message, stack, method, typeMethod, type, title, ex, db);
    }

    static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String toLog() {
        //[2023-02-28 17:04:43] [INFO ] [] [main-EventThread]
        //[31m[2023-02-28 17:26:38][0;39m [34m[INFO ][0;39m [34m[][0;39m [31m[dubbo-protocol-18889-thread-9][0;39m [1;35m[org.apache.ibatis.logging.slf4j.BorenSlf4jImpl:17][0;39m -
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\u001B[31m[").append(FORMATTER.format(enterTime)).append("]\u001B[0;39m ");
        stringBuffer.append("\u001B[34m[").append(threadName).append("]\u001B[0;39m ");
        stringBuffer.append("\u001B[31m[").append(type).append(".").append(method).append("]\u001B[0;39m ");
        if ("sql".equals(model)) {
            String format = new DmlFormatter().format(message);
//            format = "<br />" + format
//                    .replace(" ", "<span style='margin-left:4px'></span>")
//                    .replace("\r\n", "<br />");
            stringBuffer.append("\u001B[1;35m").append(format).append("\u001B[0;39m");
        } else {
            stringBuffer.append("\u001B[1;35m").append(message).append("\u001B[0;39m");
        }
        return stringBuffer.toString();
    }
}