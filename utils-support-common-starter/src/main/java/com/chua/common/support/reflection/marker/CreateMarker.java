package com.chua.common.support.reflection.marker;

import com.chua.common.support.utils.JavassistUtils;
import com.chua.common.support.utils.StringUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;


/**
 * 构建起
 *
 * @author CH
 */
public class CreateMarker extends AppendMarker {
    public static final String SOURCE = "INSTANCE$SOURCE";
    public static final String SOURCE_MAP = "INSTANCE$SOURCEMAP";

    public CreateMarker(Object entity) {
        super(entity);
    }


    /**
     * 方法
     *
     * @param ctClass     类型
     * @param classPool   类池
     * @param isInterface 是否是接口
     */
    @Override
    protected void doAnalysisMethod(CtClass ctClass, ClassPool classPool, boolean isInterface) {
        methodDescribes.forEach(methodDescribe -> {
            String type = methodDescribe.returnType();
            if (Strings.isNullOrEmpty(type)) {
                type = String.class.getTypeName();
            }

            try {
                CtMethod ctMethod = CtNewMethod.make(methodDescribe.modifiers(),
                        classPool.get(type),
                        methodDescribe.name(),
                        JavassistUtils.toCtClass(methodDescribe.parameterTypes(), classPool),
                        JavassistUtils.toCtClass(methodDescribe.exceptionTypes(), classPool),
                        StringUtils.defaultString(methodDescribe.body(), "{return try{((Method)" + SOURCE_MAP + ".get(\"" + methodDescribe.name() + Joiner.on('#').join(methodDescribe.parameterTypes()) + "\")).invoke($args);}catch(Exception e){}}"),
                        ctClass
                );
                renderMethodAnnotation(ctMethod, ctClass, methodDescribe.annotationTypes());
                ctClass.addMethod(ctMethod);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
