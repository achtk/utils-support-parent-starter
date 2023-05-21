package com.chua.zeromq.support.mq;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.request.DelegateRequest;
import com.chua.common.support.utils.ThreadUtils;
import org.zeromq.ZMQ;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * zero
 *
 * @author CH
 */
public class ZeromqServer extends AbstractServer {

    ZMQ.Context context;
    final AtomicBoolean status = new AtomicBoolean(false);
    private Executor executor;

    protected ZeromqServer(ServerOption serverOption) {
        super(serverOption);
    }

    @Override
    public void afterPropertiesSet() {
        super.register(new DelegateRequest());
        context = ZMQ.context(request.getIntValue("maxTotal", 1));
        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://" + request.getString("host", "*") + ":" + request.getIntValue("port", 5555));
        this.executor = (Executor) request.getObject("executor");
    }

    @Override
    protected void shutdown() {
        status.set(false);
        ThreadUtils.shutdownNow(executor);
    }

    @Override
    protected void run() {
        status.set(true);
        executor.execute(() -> {
            while (status.get()) {
                ThreadUtils.sleepSecondsQuietly(1);
            }
        });
    }
}
