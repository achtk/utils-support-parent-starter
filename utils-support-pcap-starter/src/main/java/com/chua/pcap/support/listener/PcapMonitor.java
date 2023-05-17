package com.chua.pcap.support.listener;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.monitor.AbstractMonitor;
import com.chua.common.support.monitor.NotifyType;
import com.chua.common.support.monitor.session.SessionNotifyMessage;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.pcap.support.template.PcapTemplate;
import com.chua.pcap.support.template.StandardPcapTemplate;


/**
 * pcap
 */
@Spi("pcap")
public class PcapMonitor extends AbstractMonitor {

    private PcapTemplate pcapTemplate;


    @Override
    public void preStart() {
        String network = configuration.url();
        this.executorService = ThreadUtils.newSingleThreadExecutor("watch-network-" + network);
        this.pcapTemplate = new StandardPcapTemplate(network, -1);
        executorService.execute(() -> {
            try {
                pcapTemplate.filter(configuration.database(), packet -> {
                    SessionNotifyMessage message = new SessionNotifyMessage();
                    message.setType(NotifyType.CREATE).setMessage(configuration.database());
                    message.setSession(new PcapSession(packet));
                    notifyMessage(message);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterStart() {

    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        try {
            pcapTemplate.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
