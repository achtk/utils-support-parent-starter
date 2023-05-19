package com.chua.influxdb.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

/**
 * influx
 *
 * @author CH
 */
public class InfluxClient extends AbstractClient<InfluxDB> {
    private String retentionPolicy;
    private InfluxDB influx;
    private String database;

    protected InfluxClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public InfluxDB getClient() {
        return influx;
    }

    @Override
    public void closeClient(InfluxDB client) {
        client.close();
    }

    @Override
    public void connectClient() {
        influx = InfluxDBFactory.connect(url, clientOption.username(), clientOption.password());
        influx.setDatabase(database).setRetentionPolicy(retentionPolicy);
        influx.setLogLevel(InfluxDB.LogLevel.parseLogLevel(MapUtils.getString(clientOption.ream(), "BASIC")));
    }

    @Override
    public void close() {
        influx.close();
    }

    @Override
    public void afterPropertiesSet() {
        this.retentionPolicy = StringUtils.defaultString(clientOption.retentionPolicy(),  "autogen");
        this.database = clientOption.database();
    }

    @Override
    protected boolean allowPooling() {
        return false;
    }


}
