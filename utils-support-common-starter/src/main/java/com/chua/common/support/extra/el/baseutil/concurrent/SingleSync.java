package com.chua.common.support.extra.el.baseutil.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * 基础类
 *
 * @author CH
 */
public class SingleSync {
    protected Thread owner;
    protected volatile boolean finished = false;
    protected volatile boolean await = false;

    public void signal() {
        finished = true;
        if (await) {
            LockSupport.unpark(owner);
        }
    }

    public void await() {
        owner = Thread.currentThread();
        await = true;
        while (finished == false) {
            LockSupport.park();
        }
    }

    @Override
    public String toString() {
        Thread t = owner;
        if (t == null || await == false) {
            return "no waiter";
        } else {
            return "thread:" + t.getName() + " is waiting";
        }
    }

    public void reset() {
        owner = null;
        finished = false;
        await = false;
    }
}
