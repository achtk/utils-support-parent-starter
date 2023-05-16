package com.chua.common.support.mysql;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.monitor.*;
import com.chua.common.support.mysql.event.*;
import com.chua.common.support.utils.NetAddress;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * mysql
 *
 * @author CH
 */
@Spi("mysql")
public class MysqlMonitor extends AbstractMonitor implements BinaryLogClient.EventListener {
    private final Map<Long, TableNotifyMessage> tableMapSessionMessageMap = new ConcurrentReferenceHashMap<>(512);
    private BinaryLogClient client;

    @Override
    public void preStart() {
        NetAddress netAddress = NetAddress.of(configuration.url());
        this.client = new BinaryLogClient(netAddress.getHost(), netAddress.getPort(), configuration.username(), configuration.password());
        client.setServerId(Long.parseLong(configuration.database()));
        client.setKeepAliveInterval(configuration.interval());
        client.registerEventListener(this);
    }

    @Override
    public void afterStart() {
        try {
            client.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        try {
            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEvent(Event event) {
        EventData data = event.getData();
        NotifyMessage message = createMessage(data);
        if (null == message) {
            return;
        }

        notifyMessage(message);
    }

    /**
     * 消息
     *
     * @param data 数据
     * @return 消息
     */
    private NotifyMessage createMessage(EventData data) {
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.setType(createType(data));

        if (data instanceof RotateEventData) {
            notifyMessage.setMessage(((RotateEventData) data).getBinlogFilename());
            return notifyMessage;
        }

        if (data instanceof FormatDescriptionEventData) {
            DescriptionNotifyMessage sessionMessage1 = new DescriptionNotifyMessage();
            sessionMessage1.setServerVersion(((FormatDescriptionEventData) data).getServerVersion());
            sessionMessage1.setChecksumType(((FormatDescriptionEventData) data).getChecksumType().name());
            sessionMessage1.setType(createType(data));
            return sessionMessage1;
        }

        if (data instanceof QueryEventData) {
            QueryNotifyMessage sessionMessage1 = new QueryNotifyMessage();
            sessionMessage1.setRoot(((QueryEventData) data).getDatabase());
            sessionMessage1.setThreadId(((QueryEventData) data).getThreadId());
            sessionMessage1.setMessage(((QueryEventData) data).getSql());
            sessionMessage1.setType(createType(data));
            return sessionMessage1;
        }

        if (data instanceof TableMapEventData) {
            TableNotifyMessage sessionMessage1 = new TableNotifyMessage();
            sessionMessage1.setDatabase(((TableMapEventData) data).getDatabase());
            sessionMessage1.setTableId(((TableMapEventData) data).getTableId());
            sessionMessage1.setTable(((TableMapEventData) data).getTable());
            tableMapSessionMessageMap.put(sessionMessage1.getTableId(), sessionMessage1);
            sessionMessage1.setType(createType(data));
            return sessionMessage1;
        }

        if (data instanceof WriteRowsEventData) {
            WriteNotifyMessage sessionMessage1 = new WriteNotifyMessage();
            sessionMessage1.setColumn(((WriteRowsEventData) data).getIncludedColumns());
            sessionMessage1.setModifyData(((WriteRowsEventData) data).getRows());
            sessionMessage1.setSession(tableMapSessionMessageMap.get(((WriteRowsEventData) data).getTableId()));
            sessionMessage1.setType(createType(data));
            return sessionMessage1;
        }

        if (data instanceof DeleteRowsEventData) {
            DeleteNotifyMessage sessionMessage1 = new DeleteNotifyMessage();
            sessionMessage1.setColumn(((DeleteRowsEventData) data).getIncludedColumns());
            sessionMessage1.setModifyData(((DeleteRowsEventData) data).getRows());
            sessionMessage1.setSession(tableMapSessionMessageMap.get(((DeleteRowsEventData) data).getTableId()));
            sessionMessage1.setType(createType(data));
            return sessionMessage1;
        }

        if (data instanceof UpdateRowsEventData) {
            UpdateNotifyMessage sessionMessage1 = new UpdateNotifyMessage();
            sessionMessage1.setColumn(((UpdateRowsEventData) data).getIncludedColumns());
            sessionMessage1.setBeforeData(((UpdateRowsEventData) data).getRows().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
            sessionMessage1.setModifyData(((UpdateRowsEventData) data).getRows().stream().map(Map.Entry::getValue).collect(Collectors.toList()));
            sessionMessage1.setSession(tableMapSessionMessageMap.get(((UpdateRowsEventData) data).getTableId()));
            sessionMessage1.setType(createType(data));
            return sessionMessage1;
        }

        return null;
    }

    /**
     * 创建类型
     *
     * @param data 事件数据
     * @return 监听类型
     */
    private NotifyType createType(EventData data) {
        if (data instanceof UpdateRowsEventData) {
            return NotifyType.MODIFY;
        }

        if (data instanceof WriteRowsEventData) {
            return NotifyType.CREATE;
        }

        if (data instanceof DeleteRowsEventData) {
            return NotifyType.DELETE;
        }

        if (data instanceof QueryEventData) {
            return NotifyType.QUERY;
        }

        if (data instanceof TableMapEventData) {
            return NotifyType.TABLE;
        }

        if (data instanceof FormatDescriptionEventData) {
            return NotifyType.FORMAT_DESCRIPTION;
        }

        if (data instanceof RotateEventData) {
            return NotifyType.ROTATE;
        }
        return NotifyType.OTHER;
    }
}
