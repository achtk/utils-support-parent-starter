package com.chua.common.support.crawler.request;

import java.net.Proxy;
import java.net.SocketAddress;

/**
 * 代理
 *
 * @author CH
 * @version 1.0.0
 */
public class PageProxy extends Proxy {

    /**
     * Creates an entry representing a PROXY connection.
     * Certain combinations are illegal. For instance, for types Http, and
     * Socks, a SocketAddress <b>must</b> be provided.
     * <p>
     * Use the {@code Proxy.NO_PROXY} constant
     * for representing a direct connection.
     *
     * @param type the {@code Type} of the proxy
     * @param sa   the {@code SocketAddress} for that proxy
     * @throws IllegalArgumentException when the type and the address are
     *                                  incompatible
     */
    public PageProxy(Type type, SocketAddress sa) {
        super(type, sa);
    }
}
