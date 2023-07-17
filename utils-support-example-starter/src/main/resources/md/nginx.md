# 1.安装nginx

```bash
# 1.安装gcc依赖库
sudo apt-get install build-essential
sudo apt-get install libtool

# 2.安装pcre依赖库
sudo apt-get install libpcre3 libpcre3-dev

# 3.安装zlib依赖库
sudo apt-get install zlib1g-dev

# 4.安装ssl依赖库
sudo apt-get install openssl

#下载最新版本：
wget http://nginx.org/download/nginx-1.11.3.tar.gz
#解压：
tar -zxvf nginx-1.11.3.tar.gz
#进入解压目录：
cd nginx-1.11.3
#配置：
./configure --prefix=/usr/local/nginx 
#编辑nginx：
sudo make
注意：这里可能会报错，提示“pcre.h No such file or directory”,具体详见：http://stackoverflow.com/questions/22555561/error-building-fatal-error-pcre-h-no-such-file-or-directory
需要安装 libpcre3-dev,命令为：sudo apt-get install libpcre3-dev
#安装nginx：
sudo make install
#启动nginx：
sudo /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
注意：-c 指定配置文件的路径，不加的话，nginx会自动加载默认路径的配置文件，可以通过 -h查看帮助命令。
#查看nginx进程：
ps -ef|grep nginx
```

# 2.离线安装pcre

```bash
# 1.解压pcre安装包，然后执行安装和编译安装
tar -zxvf pcre-8.43.tar.gz
cd pcre-8.43
sudo ./configure --prefix=/usr/local/pcre/
sudo make
sudo make install

# 2.安装nginx这时安装nginx时需要增加参数指定使用的pcre，但是不能指向编译之后的pcre，要指向源码pcre目录
./configure --prefix=/usr/local/nginx/ --with-pcre=源码pcre目录
sudo make && sudo make install

```

# 3.MYSQL

## 1.安装

```bash
#安装 apt 镜像
wget https://repo.mysql.com//mysql-apt-config_0.8.10-1_all.deb
#执行命令之后出现的内容两次都选择 OK
sudo dpkg -i mysql-apt-config_0.8.10-1_all.deb 
sudo apt-get update
#执行命令会出现设置密码界面
sudo apt-get install mysql-server 
#启动服务
sudo systemctl start mysql 
#开机启动
sudo systemctl enable mysql 
```

```shell
wget https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.31-linux-glibc2.12-x86_64.tar.xz
xz -d mysql-8.0.31-linux-glibc2.12-x86_64.tar.xz
tar xvf mysql-8.0.31-linux-glibc2.12-x86_64.tar
sudo mkdir /usr/local/mysql
sudo mv mysql-8.0.31-linux-glibc2.12-x86_64/* /usr/local/mysql
cd /usr/local/mysql
sudo vim /etc/my.cnf
cd bin
sudo ./mysqld –initialize
sudo cp -a ./support-files/mysql.server /etc/init.d/mysql
sudo chmod +x /etc/init.d/mysql
sudo sysv-rc-conf mysql on




```



```ini
[mysqld]
user=root
datadir=/usr/local/mysql/data
basedir=/usr/local/mysql
port=3306
max_connections=200
max_connect_errors=10
character-set-server=utf8
default-storage-engine=INNODB
default_authentication_plugin=mysql_native_password
lower_case_table_names=1
group_concat_max_len=102400
[mysql]
default-character-set=utf8
[client]
port=3306
default-character-set=utf8
```



## 2.卸载

```bash
sudo apt-get remove mysql-server mysql-client
sudo rm /var/lib/mysql/ -R
sudo rm /etc/mysql/ -R
sudo apt-get remove mysql* --purge
sudo apt-get remove apparmor

```

# 4.redis

```shell
sudo apt-get install redis-server
```



# 4.错误



### 4.1、Ubuntu18.04下出现sysv-rc-conf: command not found问题，命令找不到

#### 4.1.1、先update一下

```javascript
sudo apt-get update
```

#### **4.1.2、如果和我这里一样显示无法定位到sysv-rc-conf包的话我们去资源列表中加一句**

```shell
//先用vim编辑器打开sources.list
 sudo vim /etc/apt/sources.list
 
 //打开后在最后添加这个源
deb http://archive.ubuntu.com/ubuntu/ trusty main universe restricted multiverse
```

#### 4.1.3、现在重复1、update更新一下

```shell
sudo apt-get update
```

#### 4.1.4、安装sysv-rc-conf

```bash
sudo apt-get install sysv-rc-conf
```

