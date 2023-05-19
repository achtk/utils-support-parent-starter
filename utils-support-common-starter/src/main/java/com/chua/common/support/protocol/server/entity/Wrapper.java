package com.chua.common.support.protocol.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装
 *
 * @author CH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Wrapper<Client> {
    /**
     * 客户端
     */
    private Client client;
    /**
     * 数据
     */
    private String data;

}
