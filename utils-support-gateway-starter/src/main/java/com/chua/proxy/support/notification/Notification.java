package com.chua.proxy.support.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 通知
 *
 * @author CH
 */
@Getter
@Setter
@ToString(callSuper = true)
public class Notification {

    private boolean apiUpdated;

    private boolean subscriptionUpdated;

    private boolean environmentUpdated;


}
