package com.chua.common.support.net.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.net.frame.Frame;
import com.chua.common.support.net.frame.HttpFrame;

import java.net.InetSocketAddress;

import static com.chua.common.support.http.HttpConstant.HTTP;

/**
 * @author CH
 */
@Spi("mapping")
public class DemoMappingResolver implements MappingResolver{
    @Override
    public Discovery resolve(Frame frame) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.110.31", 3000);
        Discovery.DiscoveryBuilder builder = Discovery.builder();
        builder.address(inetSocketAddress.getAddress().getHostAddress())
                .port(inetSocketAddress.getPort()).build();
        if(frame instanceof HttpFrame) {
            String uri = ((HttpFrame) frame).getUri();
            builder.protocol(HTTP);
            builder.uriSpec("/");
        }
        return builder.build();
    }

    @Override
    public int timeout() {
        return 30_000;
    }
}
