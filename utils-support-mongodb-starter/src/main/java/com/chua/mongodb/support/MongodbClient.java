package com.chua.mongodb.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.mongodb.support.template.MongodbVfsTemplate;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.SneakyThrows;

/**
 * Mongodb
 * @author CH
 */
public class MongodbClient extends AbstractClient<MongodbVfsTemplate> {
    private String url;
    private long timeout;
    private MongodbVfsTemplate mongoTemplate;
    private MongoClient mongoClients;

    protected MongodbClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public MongodbVfsTemplate getClient() {
        return new MongodbVfsTemplate(MongoClients.create(url), new ConnectionString(url).getDatabase());
    }

    @Override
    public void closeClient(MongodbVfsTemplate client) {
        try {
            client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connectClient() {
        this.mongoTemplate = new MongodbVfsTemplate(MongoClients.create(url), new ConnectionString(url).getDatabase());
    }

    @SneakyThrows
    @Override
    public void close() {
        mongoTemplate.close();
    }

    @Override
    public void afterPropertiesSet() {

    }
}
