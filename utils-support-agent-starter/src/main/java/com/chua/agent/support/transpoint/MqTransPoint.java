package com.chua.agent.support.transpoint;

import com.chua.agent.support.constant.Constant;
import com.chua.agent.support.json.JSONObject;
import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.HaBroker;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Producer;
import org.zbus.net.http.Message;

import java.io.IOException;

import static com.chua.agent.support.store.AgentStore.*;

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
        BrokerConfig brokerConfig = new BrokerConfig();
        brokerConfig.setBrokerAddress(UNIFORM_ADDRESS);
        try {
            if (UNIFORM_ADDRESS.contains(",")) {
                broker = new HaBroker(brokerConfig);
            } else {
                broker = new SingleBroker(brokerConfig);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MqConfig config = new MqConfig();
        config.setBroker(broker);
        config.setMq(getStringValue(Constant.TRANS_SERVER_POINT, "uniform"));
        this.producer = new Producer(config);
        try {
            producer.createMQ();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void publish(String type , String message) {
        if (null == producer) {
            return;
        }
        Message msg = new Message();
        msg.setBody(new JSONObject()
                .fluentPut("applicationName", APPLICATION_NAME)
                .fluentPut("mode", type)
                .fluentPut("message", message)
                .toString()
        );
        try {
            producer.sendAsync(msg);
        } catch (IOException ignored) {
        }
    }
}
