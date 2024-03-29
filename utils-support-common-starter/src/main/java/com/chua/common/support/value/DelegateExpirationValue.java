package com.chua.common.support.value;

import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 过期
 *
 * @author Administrator
 * @param <T> 类型
 */
public class DelegateExpirationValue<T> extends DelegateValue<T> implements ExpirationValue<T> {

    private LocalDateTime expirationTime = LocalDateTime.now();
    @Setter
    private long expire = 10;
    @Setter
    private TimeUnit timeUnit = SECONDS;

    public DelegateExpirationValue(T object) {
        super(object);
    }


    public DelegateExpirationValue(T object, long expire, TimeUnit timeUnit) {
        super(object);
        this.expire = expire;
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean isExpiration() {
        return 0 != expire && expirationTime.plusSeconds(timeUnit.toSeconds(expire)).isBefore(LocalDateTime.now());
    }

    @Override
    public boolean resetExpire() {
        expirationTime = LocalDateTime.now();
        return true;
    }
}
