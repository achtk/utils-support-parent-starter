package com.chua.influxdb.support;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import org.influxdb.InfluxDB;

/**
 * InfluxDB
 *
 * @author CH
 * @version 1.0.0
 */
public class InfluxClientProvider extends AbstractClientProvider<InfluxDB> {
    public InfluxClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return InfluxClient.class;
    }

}
