package com.chua.common.support.protocol.server.resolver;

import lombok.Data;

import java.util.Map;

/**
 * 模型
 * @author CH
 */
@Data
public class Model {


    private String view;


    private Map<String, Object> model;
}
