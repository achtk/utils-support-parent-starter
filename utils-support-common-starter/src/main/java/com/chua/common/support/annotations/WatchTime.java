package com.chua.common.support.annotations;

import com.chua.common.support.lang.StopWatch;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 耗时检测
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WatchTime {
    /**
     * 缓存名称
     *
     * @return 缓存名称
     */
    String value() default "";

    /**
     * 时间单位
     *
     * @return 时间单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    /**
     * 最终处理
     *
     * @return 最终处理
     */
    Class<?> handler() default PrintWatchHandler.class;

    /**
     * 接口处理
     */
    public static interface WatchHandler {
        /**
         * 处理
         *
         * @param stopWatch 监视器
         * @param watchTime 注解
         */
        void handler(StopWatch stopWatch, WatchTime watchTime);
    }

    /**
     * 打印
     */
    @Slf4j
    public static class PrintWatchHandler implements WatchHandler {

        @Override
        public void handler(StopWatch stopWatch, WatchTime watchTime) {
            if (log.isDebugEnabled()) {
                log.debug("\r\n{}", stopWatch.prettyPrint(watchTime.unit()));
            }
        }
    }

}
