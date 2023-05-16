package com.chua.common.support.context.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * bean与值
 *
 * @author CH
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BeanValue {

    @NonNull
    private String value;

    private boolean exsit = true;
}
