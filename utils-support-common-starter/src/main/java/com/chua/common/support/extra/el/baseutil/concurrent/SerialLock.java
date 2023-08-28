package com.chua.common.support.extra.el.baseutil.concurrent;




import io.github.karlatemp.unsafeaccessor.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;

public class SerialLock<T>
{
    private final static Unsafe UNSAFE;
    private final static long   NEXT_OFF;
    private final static SerialNode TERMINATION_NODE = new SerialNode(null);

    static
    {
        try
        {
            UNSAFE = Unsafe.getUnsafe();
            Field nextField = SerialNode.class.getDeclaredField("next");
            NEXT_OFF = UNSAFE.objectFieldOffset(nextField);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    private ConcurrentMap<T, SerialNode> store = new ConcurrentHashMap<T, SerialNode>();

    public void exec(T key, Runnable task)
    {
        SerialNode ns = new SerialNode(task);
        SerialNode cs = store.putIfAbsent(key, ns);
        if (cs == null)
        {
            cs = ns;
            processCs(cs, key);
        }
        else
        {
            SerialNode next;
            boolean exec = false;
            do
            {
                next = cs.next;
                if (next == null)
                {
                    if (cs.casNext(ns))
                    {
                        while (ns.current == false)
                        {
                            LockSupport.park();
                        }
                        exec = true;
                        break;
                    }
                    else
                    {
                    }
                }
                else if (next == TERMINATION_NODE)
                {
                    if (store.replace(key, cs, ns))
                    {
                        exec = true;
                        break;
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    cs = next;
                }
            }
            while (true);
            if (exec)
            {
                processCs(ns, key);
            }
            else
            {
                exec(key, task);
            }
        }
    }

    void processCs(SerialNode cs, T key)
    {
        cs.current = true;
        try
        {
            cs.runnable.run();
        }
        catch (Throwable e)
        {
        }
        SerialNode next = cs.next;
        if (next == null)
        {
            if (cs.casTermination())
            {
                store.remove(key, cs);
                return;
            }
            else
            {
                next = cs.next;
            }
        }
        if (store.replace(key, cs, next) == false)
        {
            throw new UnsupportedOperationException();
        }
        next.current = true;
        next.wakeup();
    }

    static class SerialNode
    {
        final    Runnable   runnable;
        final    Thread     owner;
        volatile SerialNode next;
        volatile boolean    current = false;

        SerialNode(Runnable runnable)
        {
            this.runnable = runnable;
            this.owner = Thread.currentThread();
        }

        boolean casTermination()
        {
            return UNSAFE.compareAndSetReference(this, NEXT_OFF, null, TERMINATION_NODE);
        }

        void wakeup()
        {
            LockSupport.unpark(owner);
        }

        boolean casNext(SerialNode ns)
        {
            return UNSAFE.compareAndSetReference(this, NEXT_OFF, null, ns);
        }
    }
}
