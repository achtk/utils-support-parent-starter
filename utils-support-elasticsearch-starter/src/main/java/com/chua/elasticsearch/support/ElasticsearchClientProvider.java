package com.chua.elasticsearch.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.elasticsearch.support.collector.ElasticSearchCollector;
import com.chua.elasticsearch.support.collector.SimpleElasticSearchCollector;
import com.google.common.base.Splitter;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import java.io.IOException;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_RIGHT_BIG_PARENTHESES;


/**
 * .elasticsearch
 * @author CH
 */
@SuppressWarnings("ALL")
public class ElasticsearchClientProvider extends AbstractClientProvider<JestClient> {

    public ElasticsearchClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return ElasticsearchClient.class;
    }

    public static class ElasticsearchClient extends AbstractClient<JestClient> {

        private static final String TYPE = "doc";
        private static final String ID = "id";
        private static final String QUERY_STRING = "{\n" +
                "    \"query\": {\n" +
                "        \"query_string\" : {\n" +
                "            \"query\" : \"%s\"\n" +
                "        }\n" +
                "    }\n" +
                SYMBOL_RIGHT_BIG_PARENTHESES;

        private static final String QUERY_STRING_PAGE = "{\n" +
                "    \"from\" : %s," +
                "    \"size\" : %s," +
                "    \"sort\" : %s," +
                "    \"query\": {\n" +
                "        \"query_string\" : {\n" +
                "            \"query\" : \"%s\"\n" +
                "        }\n" +
                "    }\n" +
                SYMBOL_RIGHT_BIG_PARENTHESES;


        protected JestClientFactory factory;
        protected HttpClientConfig httpClientConfig;

        protected ElasticsearchClient(ClientOption clientOption) {
            super(clientOption);
        }

        @Override
        public JestClient getClient() {
            return factory.getObject();
        }

        @Override
        public void closeClient(JestClient client) {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void connectClient() {
            httpClientConfig = new HttpClientConfig
                    .Builder(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(url))
                    .multiThreaded(true)
                    .defaultMaxTotalConnectionPerRoute(clientOption.maxTotal())
                    .maxTotalConnection(clientOption.maxIdle())
                    .readTimeout(clientOption.sessionTimeoutMillis())
                    .connTimeout(clientOption.connectionTimeoutMillis())
                    .build();

            this.factory = new JestClientFactory();
            factory.setHttpClientConfig(httpClientConfig);
        }

        @Override
        public void close() {
        }

        @Override
        public void afterPropertiesSet() {

        }

        /**
         * 采集器
         * @return 采集器
         */
        public ElasticSearchCollector newCollector() {
            return new SimpleElasticSearchCollector(this.getClient());
        }
    }
}
