- 该文档中，jdk版本1.8，java项目为maven构建的springboot项目，并使用了定时任务来做AQ监听的重连功能，解决由于外部原因导致连接断裂之后，需要手动重启项目才能恢复连接的问题

# 一、创建队列

## 1.1.管理员登录执行

- 管理员登录，执行授权操作，oracle使用队列需要单独的授权，默认未开启，须手动开启，授权命令如下，username使用自己的用户名即可

```
GRANT EXECUTE ON SYS.DBMS_AQ to 'username';
GRANT EXECUTE ON SYS.DBMS_AQADM to 'username';
GRANT EXECUTE ON SYS.DBMS_AQ_BQVIEW to 'username';
GRANT EXECUTE ON SYS.DBMS_AQIN to 'username';
GRANT EXECUTE ON SYS.DBMS_JOB to 'username';
```

## 1.2.用户登录执行执行

### 1.2.1. 创建消息负荷payload

- 创建的此type用来封装队列所带的，根据实际需求进行创建

```
CREATE OR REPLACE TYPE TYPE_QUEUE_INFO AS OBJECT
(
  param_1             VARCHAR2(100),
  param_2             VARCHAR2(100)
)
```

### 1.2.2. 创建队列表

- 创建对列表，并指定队列数据的类型，队列表名自定义即可，数据类型使用上面刚创建的type

```
begin
  sys.dbms_aqadm.create_queue_table(
    queue_table => 'QUEUE_TABLE',
    queue_payload_type => 'TYPE_QUEUE_INFO',
    sort_list => 'ENQ_TIME',
    compatible => '10.0.0',
    primary_instance => 0,
    secondary_instance => 0);
end;
```

### 1.2.3. 创建队列并启动

- 创建名称为QUEUE_TEST的队列，并指定对列表名【同一个oracle用户下，可以有多个对列表，同一个对列表中，可以有多个队列】

```
begin
  sys.dbms_aqadm.create_queue(
    queue_name => 'QUEUE_TEST',
    queue_table => 'QUEUE_TABLE',
    queue_type => sys.dbms_aqadm.normal_queue,
    max_retries => 5,
    retry_delay => 0,
    retention_time => 0);
end;
```

- 刚创建的队列的状态默认是未开启的，需要手动开启一下，同理，存在删除、停止等操作

```
begin
  -- 启动队列
  sys.dbms_aqadm.start_queue(
      queue_name => 'QUEUE_TEST'
  );
  
  -- 暂停队列
  --sys.dbms_aqadm.STOP_QUEUE(
  --    queue_name => 'QUEUE_TEST'
  --);
  
  -- 删除队列
  --sys.dbms_aqadm.DROP_QUEUE(
  --    queue_name => 'QUEUE_TEST'
  --);
  
  -- 删除对列表
  --sys.dbms_aqadm.DROP_QUEUE_TABLE(
  --    queue_table => 'QUEUE_TABLE'
  --);
end;
```

### 1.2.4. 创建存储过程

- 储存过程的作用为把数据加载到队列中，生成的新的队列会自动添加进绑定的对列表中，等待消费者进行消费

```
CREATE OR REPLACE PROCEDURE pro_queue(param_1 VARCHAR2, param_2 VARCHAR2) as
  r_enqueue_options    DBMS_AQ.ENQUEUE_OPTIONS_T;
  r_message_properties DBMS_AQ.MESSAGE_PROPERTIES_T;
  v_message_handle     RAW(16);
  o_payload            TYPE_QUEUE_INFO;
begin
  -- 封装最终消息
  o_payload := TYPE_QUEUE_INFO(param_1, param_2);
  -- 入队操作，指定队列
  dbms_aq.enqueue(queue_name         => 'QUEUE_TEST',
                  enqueue_options    => r_enqueue_options,
                  message_properties => r_message_properties,
                  payload            => o_payload,
                  msgid              => v_message_handle);

  -- 出队操作
  --dbms_aq.enqueue(queue_name => 'QUEUE_TEST',
  --                dequeue_options => r_dequeue_options,
  --                message_properties => r_message_properties,
  --                payload => o_payload,
  --                msgid => v_message_handle);
end pro_queue;
```