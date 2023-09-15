package com.chua.proxy.support.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 认购关系
 *
 * @author CH
 */
@Getter
@Setter
public class SubscriptionRelationship {

    private String routeId;

    private List<AppDefinition> appDefinitions;

    private long opSeq;

}
