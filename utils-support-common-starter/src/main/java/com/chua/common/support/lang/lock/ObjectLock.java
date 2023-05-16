package com.chua.common.support.lang.lock;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据锁
 *
 * @author CH
 */
public class ObjectLock implements Lock {

    private static final Map<String, LockInfo> INFO_MAP = new ConcurrentHashMap<>();

    private static final String DEFAULT_LOCK = "default";

    private static final long TIME_OUT = 60 * 1000;


    private String name = DEFAULT_LOCK;
    private long timeout = TIME_OUT;

    public ObjectLock() {
    }

    public ObjectLock(String name) {
        this.name = name;
    }

    public ObjectLock(String name, long timeout) {
        this.name = name;
        this.timeout = timeout;
    }

    @Override
    public boolean lock() {
        LockInfo lockInfo = null;
        if (null != (lockInfo = INFO_MAP.get(name))) {
            synchronized (this) {
                if (null != (lockInfo = INFO_MAP.get(name))) {
                    if (System.currentTimeMillis() - lockInfo.getPoint() > timeout) {
                        INFO_MAP.remove(name);
                    } else {
                        return true;
                    }
                }
            }
        }
        INFO_MAP.put(name, new LockInfo());
        return true;
    }

    @Override
    public boolean lock(int timeout) {
        return false;
    }

    @Override
    public void unlock() {
        INFO_MAP.remove(name);
    }

    @Override
    public boolean tryLock() {
        return false;
    }


    @Getter
    private class LockInfo {

        private long point = System.currentTimeMillis();
    }
}
