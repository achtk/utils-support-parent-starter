package com.chua.agent.support.transfor;

/**
 * httpclient
 *
 * @author CH
 */
public class CloseHttpClientTransfer extends HttpClientTransfer {
    @Override
    public String name() {
        return "org.apache.http.impl.client.CloseableHttpClient";
    }

}
