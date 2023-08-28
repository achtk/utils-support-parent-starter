package com.chua.common.support.extra.el.baseutil;

/**
 * 基础类
 * @author CH
 */
public class CodeLocation {
    /**
     * 调用该方法所在的行数信息
     *
     * @return
     */
    public static String getCodeLocation() {
        return getCodeLocation(3);
    }

    /**
     * 获取方法调用的信息.1代表调用这个方法所在的行,2代表再上一层
     *
     * @param deep deep
     * @return
     */
    public static String getCodeLocation(int deep) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[deep];
        int index = stackTraceElement.getClassName().lastIndexOf(".") + 1;
        return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getClassName().substring(index) + ".java:" + stackTraceElement.getLineNumber() + ")";
    }

}
