package com.chua.proxy.support.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 路线事件
 *
 * @author CH
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteEvent {

    private String routeId;

    private Route route;

    private boolean delete;

}
