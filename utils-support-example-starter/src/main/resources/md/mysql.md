- ## MYSQL

- ### **导出**

  ```bash
  mysqldump -u<user> -p<password> --node-data --all-databases <db1> > all.sql
  ```

- 去除登录权限

  ```bash
  ./mysqld_safe --skip-grant-tables
  ```

- **You must reset your password using ALTER USER statement before executing this statement.**

  ```shell
  ALTER USER USER() IDENTIFIED BY '<password>';
  update user set host='%' where user = 'root';
  flush privileges;
  ```

- **this is incompatible with sql_mode=only_full_group_by**

  ```bash
  #新建的数据库
  SET @@global.sql_mode ='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
  #已存在的数据库
  SET sql_mode ='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
  ```

- **1418 - This function has none of DETERMINISTIC, NO SQL, or READS SQL DATA in its declaration and binary logging is
  enabled (you *might* want to use the less safe log_bin_trust_function_creators variable)**

  ```bash
  SHOW VARIABLES LIKE 'log_bin_trust_function_creators';
  SET @@global.log_bin_trust_function_creators='On';
  set global log_bin_trust_function_creators=TRUE
  ```

- **Client does not support authentication protocol requested by server; consider upgrading MySQL client**

  ```bash
  -- 更新user为root，host为% 的密码
  ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '<password>';
  
  -- 更新user为root，host为localhost 的密码为123456
  ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
  ```

- **IP address 'xx.xx.xx.xx' could not be resolved: Name or service not known**

  skip-name-resolve

- **计算空间距离**

  ```bash
  st_distance(point(lng, lat), point(-122.083235, 37.38714) ) * 111195
  ```

- **mysql-binlog**

  ```bash
  #base64-output，可以控制输出语句输出base64编码的BINLOG语句;
  #decode-rows：选项将把基于行的事件解码成一个SQL语句
  mysqlbinlog --no-defaults --database=<db>  --base64-output=decode-rows -v  binlog.000089
  #指定时间范围
  mysqlbinlog --no-defaults --database=<db>  --start-datetime='2019-04-11 00:00:00' --stop-datetime='2019-04-11 15:00:00'  binlog.000088 
  ```

- **密码安全组策略**

  ```bash
  密码安全组策略
  mysql>
  show variables like 'validate_password%';
  #只检查长度
  set global validate_password_policy=0;
  #密码长度
  set global validate_password_length=4;
  #SHOW VARIABLES LIKE 'validate_password%';
  #set global validate_password.policy=LOW;
  #set global validate_password.length=6
  
  ```

- **创建数据库**

  ```bash
  create database `<database>` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
  create user '<user>'@'%' identified by '<password>';
  grant all privileges on  `<database>`.`<table>` to '<user>'@'%';
  grant SELECT, REPLICATION SLAVE, REPLICATION CLIENT on *.* to '<user>'@'%';
  grant process on *.* to '<user>'@'%';
  flush privileges;
  ```

- **列转行**

  > CFXFBHLB: 逗号分隔的数据
  >
  > XH : ID
  >
  > ```sql
  > SELECT
  > 	a.id,
  > 	substring_index(
  > 		substring_index(
  > 			a.CFXFBHLB,
  > 			',',
  > 			b.help_topic_id + 1
  > 		),
  > 		',' ,- 1
  > 	) AS CFXFBHLB,a.XH
  > FROM
  > 	 (SELECT @rownum := @rownum+1 AS id,t.XH, t.CFXFBHLB
  >      FROM (SELECT @rownum:=0)r , t_xfcf_tq as t
  >      ) a
  > JOIN mysql.help_topic b ON b.help_topic_id < (
  > 	length(a.CFXFBHLB) - length(
  > 		REPLACE (a.CFXFBHLB, ',', '')
  > 	) + 1
  > )
  > ```

- #### 1418

- ![再次遇到这个问题](https://img-blog.csdnimg.cn/b2632aab3dc047d3b6e43657b6c0a58e.png#pic_center)

```bash
1.先查看函数功能是否开启：show variables like '%func%';
2.开启：SET GLOBAL log_bin_trust_function_creators = 1;
3.关闭：SET GLOBAL log_bin_trust_function_creators = 0;
```

