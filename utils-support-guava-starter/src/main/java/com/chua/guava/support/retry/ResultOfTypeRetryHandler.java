package com.chua.guava.support.retry;

import com.chua.common.support.annotations.Spi;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 重试
 *
 * @author CH
 */
@Spi("result")
public class ResultOfTypeRetryHandler implements ResultRetryHandler {

    @Override
    public Object execute(Supplier<Object> supplier, int retry, int timeout, int exponential, Predicate<Object> predicate) throws Exception {
        RetryerBuilder<Object> strategy = RetryerBuilder.<Object>newBuilder()
                .retryIfResult(new com.google.common.base.Predicate<Object>() {
                    @Override
                    public boolean apply(Object input) {
                        return predicate.test(input);
                    }
                })
                .withStopStrategy(StopStrategies.stopAfterAttempt(retry));

        if (exponential < 1) {
            strategy.withWaitStrategy(WaitStrategies.fixedWait(timeout, TimeUnit.SECONDS));
        } else {
            strategy.withWaitStrategy(WaitStrategies.exponentialWait(timeout, exponential, TimeUnit.SECONDS));
        }
        return strategy.build().call(supplier::get);
    }
}
