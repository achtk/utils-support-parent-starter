package com.chua.neo4j.support;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import org.springframework.data.neo4j.core.Neo4jTemplate;

/**
 * Neo4j
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/7
 */
public class Neo4jClientProvider extends AbstractClientProvider<Neo4jTemplate> {
    public Neo4jClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return Neo4jClient.class;
    }
}
