package com.chua.common.support.lang.watchdog;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 超时处理
 *
 * @author CH
 */
public interface TimeoutHandler {
    /**
     * 初始化
     *
     * @return this
     */
    static TimeoutHandler defaultHandler() {
        return new SimpleTimeoutHanlder();
    }

    /**
     * 待处理对象
     *
     * @param handler 待处理对象
     */
    void handler(Object handler);

    /**
     * 销毁
     *
     * @param consumer 监听
     * @throws Exception 异常
     */
    void destroy(Consumer<Object> consumer) throws Exception;

    /**
     * 销毁
     *
     * @param consumer 监听
     * @throws Exception 异常
     */
    void destroyForce(Consumer<Object> consumer) throws Exception;

    final class SimpleTimeoutHanlder implements TimeoutHandler {

        private Object handler;


        @Override
        public void handler(Object handler) {
            this.handler = handler;
        }

        @Override
        public void destroy(Consumer<Object> consumer) throws Exception {
            if (null != consumer) {
                consumer.accept(handler);
                return;
            }

            if (handler instanceof Thread) {
                ((Thread) handler).interrupt();
                return;
            }


            if (handler instanceof AutoCloseable) {
                ((AutoCloseable) handler).close();
                return;
            }

            if (handler instanceof ExecutorService) {
                ((ExecutorService) handler).shutdownNow();
                return;
            }

            if (handler instanceof Process) {
                ((Process) handler).exitValue();
                return;
            }

            if (null != consumer) {
                consumer.accept(handler);
            }
        }

        @Override
        public void destroyForce(Consumer<Object> consumer) throws Exception {
            if (handler instanceof AutoCloseable) {
                ((AutoCloseable) handler).close();
            }

            if (handler instanceof ExecutorService) {
                ((ExecutorService) handler).shutdownNow();
            }

            if (handler instanceof Process) {
                ((Process) handler).destroy();
                ((Process) handler).destroyForcibly();
            }

            if (null != consumer) {
                consumer.accept(handler);
            }
        }
    }
}
