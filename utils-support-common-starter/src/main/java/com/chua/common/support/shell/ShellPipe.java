package com.chua.common.support.shell;

import java.lang.annotation.*;

/**
 * 接受原始数据
 *
 * @author CH
 */
@Target(ElementType.PARAMETER)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ShellPipe {

}
