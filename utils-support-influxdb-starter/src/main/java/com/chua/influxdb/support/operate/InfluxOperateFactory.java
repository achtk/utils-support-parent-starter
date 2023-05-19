package com.chua.influxdb.support.operate;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * operate
 * @author CH
 */
public class InfluxOperateFactory {
    private final InfluxDB influx;
    private String database;
    private String retentionPolicy;

    public InfluxOperateFactory(InfluxDB influx, String database, String retentionPolicy) {
        this.influx = influx;
        this.database = database;
        this.retentionPolicy = retentionPolicy;
    }

    /**
     * 创建自定义保留策略
     *
     * @param policyName  策略名
     * @param days        保存天数
     * @param replication 保存副本数量
     * @param isDefault   是否设为默认保留策略
     */
    public void createRetentionPolicy(String dataBaseName, String policyName, int days, int replication,
                                      Boolean isDefault) {
        String sql = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %sd REPLICATION %s ", policyName,
                dataBaseName, days, replication);
        if (isDefault) {
            sql = sql + " DEFAULT";
        }
        query(sql);
    }

    /**
     * 创建默认的保留策略
     * 策略名：hour，保存天数：30天，保存副本数量：1,设为默认保留策略
     */
    public void createDefaultRetentionPolicy() {
        String command = String
                .format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT", "hour", database,
                        "30d", 1);
        this.query(command);
    }

    /**
     * 创建数据库
     *
     * @param database 数据库
     */
    public void createDatabase(String database) {
        influx.query(new Query(Query.encode(String.format("CREATE DATABASE \"%s\"", database))));
    }

    /**
     * 删除数据库
     *
     * @param database 数据库
     */
    public void deleteDatabase(String database) {
        influx.query(new Query(Query.encode(String.format("DROP DATABASE \"%s\"", database))));
    }

    /**
     * 插入
     *
     * @param table  表
     * @param tags   标签
     * @param fields 字段
     */
    public void insert(String table,
                       Map<String, String> tags,
                       Map<String, Object> fields) {
        insert(table, tags, fields, 0, TimeUnit.SECONDS);
    }

    /**
     * 插入
     *
     * @param table    表
     * @param tags     标签
     * @param fields   字段
     * @param time     时间
     * @param timeUnit 单位
     */
    public void insert(String table,
                       Map<String, String> tags,
                       Map<String, Object> fields,
                       long time,
                       TimeUnit timeUnit) {
        Point.Builder builder = Point.measurement(table);
        builder.tag(tags);
        builder.fields(fields);
        if (0 != time) {
            builder.time(time, timeUnit);
        }
        influx.write(database, retentionPolicy, builder.build());
    }

    /**
     * 批量写入数据
     *
     * @param retentionPolicy 保存策略
     * @param consistency     一致性
     * @param timeUnit        单位
     * @param records         要保存的数据（调用BatchPoints.lineProtocol()可得到一条record）
     */
    public void batchInsert(final String retentionPolicy, final InfluxDB.ConsistencyLevel consistency,
                            TimeUnit timeUnit, final List<String> records) {
        influx.write(database, retentionPolicy, consistency, timeUnit, records);
    }

    /**
     * 删除
     *
     * @param command 删除语句
     * @return 返回错误信息
     */
    public String deleteMeasurementData(String command) {
        QueryResult result = influx.query(new Query(command, database));
        return result.getError();
    }


    /**
     * 测试连接是否正常
     *
     * @return true 正常
     */
    public boolean ping() {
        boolean isConnected = false;
        Pong pong;
        try {
            pong = influx.ping();
            if (pong != null) {
                isConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    /**
     * 查询
     * <p>SELECT * FROM measurement where name = '大脑补丁'  order by time desc limit 1000</p>
     *
     * @param command 查询语句
     * @return QueryResult
     */
    public QueryResult query(String command) {
        return influx.query(new Query(command, database));
    }

}
