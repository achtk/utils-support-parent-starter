#!/bin/bash

#停止mysqld服务
service mysqld stop
#删除用户
userdel -r  mysql

#删除数据目录和base目录
rm -rf /usr/local/mysql
rm -rf /data/
rm -rf /etc/init.d/mysqld
