package com.chua.agent.support.transpoint;

import com.chua.agent.support.constant.Constant;
import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.HaBroker;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Producer;
import org.zbus.net.http.Message;

import java.io.IOException;

import static com.chua.agent.support.store.AgentStore.getStringValue;

/**
 * 数据传输
 *
 * @author CH
 */
public class MqTransPoint implements TransPoint {
    private Producer producer;
    private Broker broker = null;

    @Override
    public void connect() {
        //创建Broker代表
        String address = getStringValue(Constant.TRANS_SERVER_ADDRESS, "127.0.0.1:15555");
        BrokerConfig brokerConfig = new BrokerConfig();
        brokerConfig.setBrokerAddress(address);
        try {
            if (address.contains(",")) {
                broker = new HaBroker(brokerConfig);
            } else {
                broker = new SingleBroker(brokerConfig);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MqConfig config = new MqConfig();
        config.setBroker(broker);
        config.setMq(getStringValue(Constant.TRANS_SERVER_POINT, "trans.point"));
        this.producer = new Producer(config);
        try {
            producer.createMQ();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void publish(String message) {
        if (null == producer) {
            return;
        }
        Message msg = new Message();
        msg.setBody(message);
        try {
            producer.sendAsync(msg);
        } catch (IOException ignored) {
        }
    }
}
