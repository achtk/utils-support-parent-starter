package com.chua.common.support.discovery;

import com.chua.common.support.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * 发现实例
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@Builder
@Accessors(chain = true)
public class Discovery {
    /**
     * 发现服务目录
     */
    @Builder.Default
    private String discovery = "discovery";

    public void setDiscovery(String discovery) {
        this.discovery = StringUtils.startWithAppend(discovery, "/");
    }

    /**
     * id
     */
    private String id;
    /**
     * 权
     */
    private double weight;
    /**
     * 地址
     */
    private String address;

    public String getAddress() {
        return StringUtils.defaultString(address,  "127.0.0.1:" + port);
    }

    public void setAddress(String address) {
        this.address = address;
        try {
            URL url = new URL(address);
            this.setPort(url.getPort());
        } catch (MalformedURLException ignored) {
        }
    }

    /**
     * 端口
     */
    private int port;
    /**
     * 端口
     */
    private int sslPort;
    /**
     * 格式
     */
    private String uriSpec;
    /**
     * 数据
     */
    private Map<String, String> metadata;

}
