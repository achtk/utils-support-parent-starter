package com.chua.guava.support.retry;

import com.chua.common.support.annotations.Spi;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 重试
 *
 * @author CH
 */
@Spi("ex")
public class ExceptionOfTypeRetryHandler implements ExceptionRetryHandler {


    @Override
    public Object execute(Supplier<Object> supplier, int retry, int timeout, int exponential, Class<? extends Exception> e) throws Exception {
        RetryerBuilder<Object> strategy = RetryerBuilder.<Object>newBuilder()
                .retryIfExceptionOfType(e)
                .withStopStrategy(StopStrategies.stopAfterAttempt(retry));

        if (exponential < 1) {
            strategy.withWaitStrategy(WaitStrategies.fixedWait(timeout, TimeUnit.SECONDS));
        } else {
            strategy.withWaitStrategy(WaitStrategies.exponentialWait(timeout, exponential, TimeUnit.SECONDS));
        }
        return strategy.build().call(supplier::get);
    }

}
