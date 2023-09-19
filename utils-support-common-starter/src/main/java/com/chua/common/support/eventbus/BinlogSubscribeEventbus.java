package com.chua.common.support.eventbus;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.constant.Action;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.mysql.binlog.BinaryLogClient;
import com.chua.common.support.mysql.binlog.event.*;
import com.chua.common.support.mysql.binlog.event.deserialization.ByteArrayEventDataDeserializer;
import com.chua.common.support.mysql.binlog.event.deserialization.EventDeserializer;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CH
 */
@Slf4j
public class BinlogSubscribeEventbus extends AbstractEventbus implements InitializingAware, BinaryLogClient.EventListener {

    public String address;
    private final String user;
    private final String passwd;

    private final Map<String, Set<EventbusEvent>> temp = new HashMap<>();
    private BinaryLogClient client;

    private final Map<String, TableMapEventData> tableIds = new ConcurrentHashMap<>();

    private final Map<String, List<TableMetadata>> tableMetadata = new ConcurrentReferenceHashMap<>(16);
    private Connection connection;

    /**
     * binlog订阅事件总线
     *
     * @param address 住址
     * @param user    使用者
     * @param passwd  passwd
     */
    public BinlogSubscribeEventbus(String address, String user, String passwd) {
        this.address = address;
        this.user = user;
        this.passwd = passwd;
    }

    public BinlogSubscribeEventbus(String user, String passwd) {
        this("127.0.0.1:3306", user, passwd);
    }

    public BinlogSubscribeEventbus() {
        this("127.0.0.1:3306", "root", "root");
    }

    @Override
    public SubscribeEventbus register(EventbusEvent[] value) {
        for (EventbusEvent eventbusEvent : value) {
            String name = eventbusEvent.getName();
            if (StringUtils.isNullOrEmpty(name)) {
                continue;
            }
            temp.computeIfAbsent(name, it -> new HashSet<>()).add(eventbusEvent);
        }
        return this;
    }

    @Override
    public SubscribeEventbus unregister(EventbusEvent value) {
        if (null == value) {
            return this;
        }
        Method method1 = value.getMethod();
        if (null == method1) {
            return this;
        }
        String name = value.getName();

        Set<EventbusEvent> subscribeTasks = temp.get(name);
        if (null != subscribeTasks) {
            List<EventbusEvent> list = intoRemoveList(subscribeTasks, value);
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(subscribeTasks::remove);
            }
        }
        return this;
    }

    /**
     * 获取删除的对象
     *
     * @param source 数据源
     * @param value  比较数据
     * @return 删除的对象
     */
    private List<EventbusEvent> intoRemoveList(Collection<EventbusEvent> source, EventbusEvent value) {
        List<EventbusEvent> list = new ArrayList<>();
        Method method1 = value.getMethod();
        source.forEach(it -> {
            if (it.getBean() != value.getBean()) {
                return;
            }
            Method method = it.getMethod();
            if (!method.getName().equals(method1.getName())) {
                return;
            }
            if (!ArrayUtils.isEquals(method.getParameterTypes(), method1.getParameterTypes())) {
                return;
            }
            list.add(it);
        });
        return list;
    }

    @Override
    public SubscribeEventbus post(String name, Object message) {
        log.warn("不支持发送数据");
        return this;
    }

    @Override
    public EventbusType event() {
        return EventbusType.BINLOG;
    }

    @Override
    public void afterPropertiesSet() {
        NetAddress netAddress = NetAddress.of(address);
        this.client = new BinaryLogClient(netAddress.getHost(), netAddress.getPort(), user, passwd);
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );

        eventDeserializer.setEventDataDeserializer(EventType.DELETE_ROWS, new ByteArrayEventDataDeserializer());
        eventDeserializer.setEventDataDeserializer(EventType.UPDATE_ROWS, new ByteArrayEventDataDeserializer());
        eventDeserializer.setEventDataDeserializer(EventType.ROWS_QUERY, new ByteArrayEventDataDeserializer());
        eventDeserializer.setEventDataDeserializer(EventType.WRITE_ROWS, new ByteArrayEventDataDeserializer());
        client.setEventDeserializer(eventDeserializer);

        client.registerEventListener(this);

        executor.execute(() -> {
            try {
                client.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void build() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + address + "/", user, passwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        afterPropertiesSet();
    }


    @Override
    public void close() throws Exception {
        super.close();
        if (null != client) {
            client.disconnect();
        }
    }

    @Override
    public void onEvent(Event event) {
        EventType eventType = event.getHeader().getEventType();
        if (eventType == EventType.ROTATE || eventType == EventType.FORMAT_DESCRIPTION) {
            log.info("检测到日志文件: {}", event.getData().toString());
            return;
        }

        if (eventType == EventType.QUERY) {
            notifyQuery(event);
            return;
        }
        if (eventType == EventType.TABLE_MAP) {
            notifyTable(event);
            return;
        }

        if (eventType == EventType.EXT_UPDATE_ROWS) {
            notifyUpdate(event);
            return;
        }

        if (eventType == EventType.EXT_WRITE_ROWS) {
            notifyCreate(event);
            return;
        }

        if (eventType == EventType.EXT_DELETE_ROWS) {
            notifyDelete(event);
            return;
        }
    }

    /**
     * 通知删除
     *
     * @param event 事件
     */
    private void notifyDelete(Event event) {
        DeleteRowsEventData eventData = event.getData();
        TableMapEventData tableMapEventData = tableIds.get(eventData.getTableId() + "");
        if (null == tableMapEventData) {
            return;
        }

        String table = tableMapEventData.getTable();
        if (!containsKey(table)) {
            return;
        }

        Set<EventbusEvent> eventbusEvents = getEvent(table);
        List<TableMetadata> tableMetadata1 = getTableMeta(table);
        List<Map<String, Object>> values = createValue(tableMetadata1, eventData.getRows());

        send(eventbusEvents, values, Action.DROP);
    }

    /**
     * 通知创建
     *
     * @param event 事件
     */
    private void notifyCreate(Event event) {
        WriteRowsEventData eventData = event.getData();
        TableMapEventData tableMapEventData = tableIds.get(eventData.getTableId() + "");
        if (null == tableMapEventData) {
            return;
        }

        String table = tableMapEventData.getTable();
        if (!containsKey(table)) {
            return;
        }

        Set<EventbusEvent> eventbusEvents = getEvent(table);
        List<TableMetadata> tableMetadata1 = getTableMeta(table);
        List<Map<String, Object>> values = createValue(tableMetadata1, eventData.getRows());
        send(eventbusEvents, values, Action.CREATE);
    }

    /**
     * 创造值
     *
     * @param tableMetadata1 表元数据1
     * @param rows           行
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    private List<Map<String, Object>> createValue(List<TableMetadata> tableMetadata1, List<Serializable[]> rows) {
        List<Map<String, Object>> rs = new LinkedList<>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            Serializable[] serializables = rows.get(i);
            for (int j = 0; j < serializables.length; j++) {
                TableMetadata tableMetadata2 = null;
                try {
                    tableMetadata2 = tableMetadata1.get(j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Serializable serializable = serializables[j];
                item.put(tableMetadata2.getName(), ObjectUtils.toObject(serializable));
            }

            rs.add(item);
        }
        return rs;
    }


    private List<TableMetadata> getTableMeta(String table) {
        return tableMetadata.computeIfAbsent(table, (tableMetadata) -> {
            try {
                ResultSet resultSet1 = connection.getMetaData().getColumns(null, null, table, "%");
                int index = 1;
                if (log.isTraceEnabled()) {
                    log.trace("开始装载: {}", table);
                }
                List<TableMetadata> rs = new LinkedList<>();
                while (resultSet1.next()) {
                    rs.add(new TableMetadata(resultSet1.getString("COLUMN_NAME"),
                                    index++,
                                    resultSet1.getString("TYPE_NAME"),
                                    resultSet1.getInt("DATA_TYPE")
                            )
                    );
                }
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }

        });
    }

    /**
     * 通知更新
     *
     * @param event 事件
     */
    private void notifyUpdate(Event event) {
        UpdateRowsEventData eventData = event.getData();
        TableMapEventData tableMapEventData = tableIds.get(eventData.getTableId() + "");
        if (null == tableMapEventData) {
            return;
        }

        String table = tableMapEventData.getTable();
        if (!containsKey(table)) {
            return;
        }

        Set<EventbusEvent> eventbusEvents = getEvent(table);
        List<TableMetadata> tableMetadata1 = getTableMeta(table);
        List<Map<String, Object>> values = new LinkedList<>();
        List<Map.Entry<Serializable[], Serializable[]>> rows = eventData.getRows();
        for (Map.Entry<Serializable[], Serializable[]> row : rows) {
            values.addAll(createValue(tableMetadata1, Collections.singletonList(row.getValue())));
            values.addAll(createValue(tableMetadata1, Collections.singletonList(row.getKey())));
        }
        send(eventbusEvents, values, Action.UPDATE);


    }

    /**
     * 包含密钥
     *
     * @param table 桌子
     * @return boolean
     */
    private boolean containsKey(String table) {
        for (String s : temp.keySet()) {
            if(s.contains("*") || s.contains("?")) {
                if(PathMatcher.INSTANCE.match(s, table)) {
                    return true;
                }
                continue;
            }

            if(s.equals(table)) {
                return true;
            }

        }

        return false;
    }

    /**
     * 获取事件
     *
     * @param table 桌子
     * @return {@link Set}<{@link EventbusEvent}>
     */
    private Set<EventbusEvent> getEvent(String table) {
        for (Map.Entry<String, Set<EventbusEvent>> entry : temp.entrySet()) {
            String s = entry.getKey();
            if(s.contains("*")) {
                if(PathMatcher.INSTANCE.match(s, table)) {
                    return entry.getValue();
                }
                continue;
            }

            if(s.equals(table)) {
                return entry.getValue();
            }

        }

        return null;
    }

    /**
     * 通知表
     *
     * @param event 事件
     */
    private void notifyTable(Event event) {
        TableMapEventData eventData = event.getData();
        tableIds.put(eventData.getTableId() + "", eventData);

    }

    /**
     * 邮寄
     *
     * @param eventbusEvents eventbus事件
     * @param values         价值观
     * @param action         行动
     */
    private void send(Set<EventbusEvent> eventbusEvents, List<Map<String, Object>> values, Action action) {
        executor.execute(() -> {
            for (EventbusEvent eventbusEvent : eventbusEvents) {
                Action action1 = eventbusEvent.getAction();
                if((null != action1 && (action1 == action || action1 == Action.NONE))) {
                    send(eventbusEvent, values);
                }
            }
        });
    }

    /**
     * 邮寄
     *
     * @param eventbusEvent eventbus事件
     * @param values        价值观
     */
    private void send(EventbusEvent eventbusEvent, List<Map<String, Object>> values) {
        eventbusEvent.send(values);
    }

    /**
     * 通知查询
     *
     * @param event 事件
     */
    private void notifyQuery(Event event) {
//        System.out.println();
    }
    @Data
    @AllArgsConstructor
    class TableMetadata {


        private String name;
        private int index;
        private String type;
        private int typeIndex;
    }
}
