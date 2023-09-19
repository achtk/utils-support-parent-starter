package com.chua.common.support.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * rpc使用者配置
 *
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcConsumerConfig extends BaseRpcInterfaceConfig {

    /**
     * 检查
     */
    protected Boolean check;


    /**
     * The timeout for remote invocation in milliseconds
     */
    protected Integer timeout;

    /**
     * The retry times
     */
    protected Integer retries;

    /**
     * The load balance
     */
    protected String loadBalance;

    /**
     * Whether to async
     * note that: it is an unreliable asynchronous that ignores return values and does not block threads.
     */
    protected Boolean async;
}

