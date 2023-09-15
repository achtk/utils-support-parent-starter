package com.chua.proxy.support.definition;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * id路由定义
 *
 * @author CH
 */
@Data
@NoArgsConstructor
public class IdRouteDefinition {

    private String id;

    private String orgId;

    private long seqNum;

    private RouteDefinition routeDefinition;

    public IdRouteDefinition(String id, String orgId) {
        this.id = id;
        this.orgId = orgId;
    }

}
