package com.chua.common.support.view.view;

import com.chua.common.support.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.CodeSource;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING;

/**
 * Java类信息控件
 *
 * @author vlinux
 * @date 15/5/7
 */
public class ClassInfoView implements View {

    private final Class<?> clazz;
    private final boolean isPrintField;
    private final int width;

    public ClassInfoView(Class<?> clazz, boolean isPrintField, int width) {
        this.clazz = clazz;
        this.isPrintField = isPrintField;
        this.width = width;
    }

    @Override
    public String draw() {
        return drawClassInfo();
    }

    private String getCodeSource(final CodeSource cs) {
        if (null == cs
                || null == cs.getLocation()
                || null == cs.getLocation().getFile()) {
            return EMPTY_STRING;
        }

        return cs.getLocation().getFile();
    }

    private String drawClassInfo() {
        final CodeSource cs = clazz.getProtectionDomain().getCodeSource();

        final TableView view = new TableView(new TableView.ColumnDefine[]{
                new TableView.ColumnDefine("isAnonymousClass".length(), false, TableView.Align.RIGHT),
                // (列数-1) * 3 + 4 = 7
                new TableView.ColumnDefine(width - "isAnonymousClass".length() - 7, false, TableView.Align.LEFT)
        })
                .addRow("class-info", StringUtils.classname(clazz))
                .addRow("code-source", getCodeSource(cs))
                .addRow("name", StringUtils.classname(clazz))
                .addRow("isInterface", clazz.isInterface())
                .addRow("isAnnotation", clazz.isAnnotation())
                .addRow("isEnum", clazz.isEnum())
                .addRow("isAnonymousClass", clazz.isAnonymousClass())
                .addRow("isArray", clazz.isArray())
                .addRow("isLocalClass", clazz.isLocalClass())
                .addRow("isMemberClass", clazz.isMemberClass())
                .addRow("isPrimitive", clazz.isPrimitive())
                .addRow("isSynthetic", clazz.isSynthetic())
                .addRow("simple-name", clazz.getSimpleName())
                .addRow("modifier", StringUtils.modifier(clazz.getModifiers(), ','))
                .addRow("annotation", drawAnnotation())
                .addRow("interfaces", drawInterface())
                .addRow("super-class", drawSuperClass())
                .addRow("class-loader", drawClassLoader());

        if (isPrintField) {
            view.addRow("fields", drawField());
        }

        return view.hasBorder(true).padding(1).draw();
    }


    private String drawField() {

        final StringBuilder builder = new StringBuilder();

        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            final KVView kvView = new KVView(new TableView.ColumnDefine(TableView.Align.RIGHT), new TableView.ColumnDefine(50, false, TableView.Align.LEFT))
                    .add("modifier", StringUtils.modifier(field.getModifiers(), ','))
                    .add("type", StringUtils.classname(field.getType()))
                    .add("name", field.getName());


            final StringBuilder stringBuilder = new StringBuilder();
            final Annotation[] annotationArray = field.getAnnotations();
            if (null != annotationArray && annotationArray.length > 0) {
                for (Annotation annotation : annotationArray) {
                    stringBuilder.append(StringUtils.classname(annotation.annotationType())).append(",");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                kvView.add("annotation", stringBuilder);
            }


            if (Modifier.isStatic(field.getModifiers())) {
                final boolean isAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    kvView.add("value", StringUtils.objectToString(field.get(null)));
                } catch (IllegalAccessException e) {
                    //
                } finally {
                    field.setAccessible(isAccessible);
                }
            }//if

            builder.append(kvView.draw()).append("\n");

        }//for

        return builder.toString();
    }

    private String drawAnnotation() {
        final StringBuilder builder = new StringBuilder();
        final Annotation[] annotationArray = clazz.getDeclaredAnnotations();

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

    private String drawInterface() {
        final StringBuilder builder = new StringBuilder();
        final Class<?>[] interfaceArray = clazz.getInterfaces();
        if (interfaceArray.length == 0) {
            builder.append(EMPTY_STRING);
        } else {
            for (Class<?> i : interfaceArray) {
                builder.append(i.getName()).append(",");
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return builder.toString();
    }

    private String drawSuperClass() {
        final LadderView ladderView = new LadderView();
        Class<?> superClass = clazz.getSuperclass();
        if (null != superClass) {
            ladderView.addItem(StringUtils.classname(superClass));
            while (true) {
                superClass = superClass.getSuperclass();
                if (null == superClass) {
                    break;
                }
                ladderView.addItem(StringUtils.classname(superClass));
            }//while
        }
        return ladderView.draw();
    }


    private String drawClassLoader() {
        final LadderView ladderView = new LadderView();
        ClassLoader loader = clazz.getClassLoader();
        if (null != loader) {
            ladderView.addItem(loader.toString());
            while (true) {
                loader = loader.getParent();
                if (null == loader) {
                    break;
                }
                ladderView.addItem(loader.toString());
            }
        }
        return ladderView.draw();
    }

}
