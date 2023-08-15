package com.chua.agent.support.span;

import com.chua.agent.support.formatter.DmlFormatter;
import com.chua.agent.support.json.JSON;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 */
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
    private transient volatile Set<String> parents = new LinkedHashSet<>();


    public Span() {
        this.enterTime = new Date();
        this.threadName = Thread.currentThread().getName();
    }

    public Span(String linkId) {
        this.linkId = linkId;
        this.enterTime = new Date();
        setStack(Thread.currentThread().getStackTrace());
        this.threadName = Thread.currentThread().getName();
    }

    public Set<String> getParents() {
        return parents;
    }

    public void setParents(Set<String> parents) {
        this.parents = parents;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStack(StackTraceElement[] stackTrace) {
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

    public void setStack(List<String> stack) {
        this.stack = stack;
    }

    public List<String> getStack() {
        return stack;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTypeMethod() {
        return typeMethod;
    }

    public void setTypeMethod(String typeMethod) {
        this.typeMethod = typeMethod;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
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