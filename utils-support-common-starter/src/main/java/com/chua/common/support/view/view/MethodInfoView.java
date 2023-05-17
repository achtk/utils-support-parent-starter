package com.chua.common.support.view.view;

import com.chua.common.support.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING;


/**
 * Java方法信息控件
 *
 * @author vlinux
 * @date 15/5/9
 */
public class MethodInfoView implements View {

    private final Method method;
    private final int width;

    public MethodInfoView(Method method, int width) {
        this.method = method;
        this.width = width;
    }

    @Override
    public String draw() {
        return new TableView(new TableView.ColumnDefine[]{
                new TableView.ColumnDefine("declaring-class".length(), false, TableView.Align.RIGHT),
                // (列数-1) * 3 + 4 = 7
                new TableView.ColumnDefine(width - "declaring-class".length() - 7, false, TableView.Align.LEFT)
        })
                .addRow("declaring-class", method.getDeclaringClass().getName())
                .addRow("method-name", method.getName())
                .addRow("modifier", StringUtils.modifier(method.getModifiers(), ','))
                .addRow("annotation", drawAnnotation())
                .addRow("parameters", drawParameters())
                .addRow("return", drawReturn())
                .addRow("exceptions", drawExceptions())
                .padding(1)
                .hasBorder(true)
                .draw();
    }

    private String drawAnnotation() {

        final StringBuilder builder = new StringBuilder();
        final Annotation[] annotationArray = method.getDeclaredAnnotations();

        if (annotationArray.length > 0) {
            for (Annotation annotation : annotationArray) {
                builder.append(StringUtils.classname(annotation.annotationType())).append(",");
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
        } else {
            builder.append(EMPTY_STRING);
        }

        return builder.toString();
    }

    private String drawParameters() {
        final StringBuilder builder = new StringBuilder();
        final Class<?>[] paramTypes = method.getParameterTypes();
        for (Class<?> clazz : paramTypes) {
            builder.append(StringUtils.classname(clazz)).append("\n");
        }
        return builder.toString();
    }

    private String drawReturn() {
        final StringBuilder builder = new StringBuilder();
        final Class<?> returnTypeClass = method.getReturnType();
        builder.append(StringUtils.classname(returnTypeClass)).append("\n");
        return builder.toString();
    }

    private String drawExceptions() {
        final StringBuilder builder = new StringBuilder();
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            for (Class<?> clazz : exceptionTypes) {
                builder.append(StringUtils.classname(clazz)).append("\n");
            }
        }
        return builder.toString();
    }

}
