#! /usr/bin/bash
##redis任何版本全程自动化源码编译安装
##用法：sh redis-install.sh 4.0.10 （后面跟的是你需要的版本号，需要什么版本就写什么版本），我这里安装的4.0.10
version=$1
usage(){
echo "usage: $0 version"
}

if [ $# -ne 1 ]
then
usage
exit -1
fi

#Redis安装包下载
cd /usr/local/src
if [ ! -f redis-${version}.tar.gz ]
then
curl -o /usr/local/src/redis-${version}.tar.gz http://download.redis.io/releases/redis-${version}.tar.gz
fi

#Redis依赖包安装
yum clean all
yum makecache fast
yum -y install gcc gcc-c++ tcl

#编译Redis所需要的gcc
yum -y install centos-release-scl
yum -y install devtoolset-9-gcc devtoolset-9-gcc-c++ devtoolset-9-binutils
source /opt/rh/devtoolset-9/enable
echo "source /opt/rh/devtoolset-9/enable" >>/etc/profile
gcc --version

##内系统参数核优化
cat >> /etc/rc.d/rc.local << "EOF"

##关闭Linux的THP（内存管理系统）通过使用更大的内存页面，来减少具有大量内存的计算机上的TLB的开销
if [ -f /sys/kernel/mm/transparent_hugepage/enabled ]
then
echo never > /sys/kernel/mm/transparent_hugepage/enabled
fi

if [ -f /sys/kernel/mm/transparent_hugepage/defrag ]
then
echo never > /sys/kernel/mm/transparent_hugepage/defrag
fi
EOF
chmod u+x /etc/rc.d/rc.local

if [ -f /sys/kernel/mm/transparent_hugepage/enabled ]
then
echo never > /sys/kernel/mm/transparent_hugepage/enabled
fi

if [ -f /sys/kernel/mm/transparent_hugepage/defrag ]
then
echo never > /sys/kernel/mm/transparent_hugepage/defrag
fi

cat >> /etc/sysctl.conf << "EOF"

#Linux系统内核参数优化
net.core.somaxconn = 2048
net.ipv4.tcp_max_syn_backlog = 2048
vm.overcommit_memory = 1
EOF
sysctl -p

cat > /etc/security/limits.conf << "EOF"
root soft nofile 65535
root hard nofile 65535
* soft nofile 65535
* hard nofile 65535
EOF

#Redis编译安装
cd /usr/local/src
tar -zxvf redis-${version}.tar.gz
cd /usr/local/src/redis-${version}
make
make PREFIX=/usr/local/redis install

#Redis基础配置
mkdir -p /usr/local/redis/{etc,logs,data}
egrep -v "^$|^#" /usr/local/src/redis-${version}/redis.conf > /usr/local/redis/etc/redis.conf
#sed -i "s/bind 127.0.0.1/bind 0.0.0.0/g" /usr/local/redis/etc/redis.conf
sed -i "s/protected-mode yes/protected-mode no/g" /usr/local/redis/etc/redis.conf
sed -i "s/daemonize no/daemonize yes/g" /usr/local/redis/etc/redis.conf
sed -i "s/pidfile \/var\/run\/redis_6379.pid/pidfile \/usr\/local\/redis\/redis.pid/g" /usr/local/redis/etc/redis.conf
sed -i "s/dir \.\//dir \/usr\/local\/redis\/data/g" /usr/local/redis/etc/redis.conf
sed -i "s/logfile \"\"/logfile \"\/usr\/local\/redis\/logs\/redis.log\"/g" /usr/local/redis/etc/redis.conf
sed -i "s/dbfilename dump.rdb/dbfilename dump.rdb/g" /usr/local/redis/etc/redis.conf
sed -i "s/appendfilename \"appendonly.aof\"/appendfilename \"appendonly.aof\"/g" /usr/local/redis/etc/redis.conf

#PATH配置
echo "export PATH=${PATH}:/usr/local/redis/bin" >>/etc/profile
source /etc/profile
#启动redis服务
/usr/local/redis/bin/redis-server /usr/local/redis/etc/redis.conf
#查看redis监听端口
netstat -tanp|grep redis