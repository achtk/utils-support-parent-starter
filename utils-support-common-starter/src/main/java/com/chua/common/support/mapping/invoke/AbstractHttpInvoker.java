package com.chua.common.support.mapping.invoke;

import com.chua.common.support.mapping.MappingConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * http调用
 *
 * @author CH
 * @since 2023/09/06
 */
@Slf4j
public abstract class AbstractHttpInvoker implements HttpInvoker {

    protected MappingConfig mappingConfig;



    public AbstractHttpInvoker(MappingConfig mappingConfig) {
        this.mappingConfig = mappingConfig;
    }



}
