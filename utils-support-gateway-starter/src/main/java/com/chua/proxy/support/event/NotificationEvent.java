package com.chua.proxy.support.event;

import com.chua.proxy.support.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 通知事件
 *
 * @author CH
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Event {

    private Notification notification;

}
