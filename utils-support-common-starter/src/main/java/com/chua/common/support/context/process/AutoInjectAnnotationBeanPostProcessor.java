package com.chua.common.support.context.process;

import com.chua.common.support.context.annotation.AutoService;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;

/**
 * 注解扫描器
 *
 * @author CH
 */
public class AutoInjectAnnotationBeanPostProcessor extends AbstractAnnotationBeanPostProcessor<AutoService> {

    public AutoInjectAnnotationBeanPostProcessor(ApplicationContextConfiguration contextConfiguration) {
        super(contextConfiguration);
    }


}
