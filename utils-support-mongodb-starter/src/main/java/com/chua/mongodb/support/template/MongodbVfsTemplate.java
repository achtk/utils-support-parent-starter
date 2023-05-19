package com.chua.mongodb.support.template;

import com.mongodb.client.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * mongo template
 * @author CH
 */
public class MongodbVfsTemplate extends MongoTemplate implements AutoCloseable{

    private final MongoClient client;

    public MongodbVfsTemplate(MongoClient client, String database) {
        super(client, database);
        this.client = client;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
