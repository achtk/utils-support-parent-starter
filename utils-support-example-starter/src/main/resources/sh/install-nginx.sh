#!/bin/bash
#功能描述：一键源码安装Nginx软件包

#定义不同的颜色组件:可以使得提示信息看起来不那么单调哦
setcolor_failure="echo -en \\033[91m"
setcolor_success="echo -en \\033[32m"
setcolor_normal="echo -e \\033[0m"
#判断是否以管理员的身份运行此脚本
if [[ $UID -ne 0 ]];then
   $setcolor_failure
   echo -n "请以管理员的身份运行此脚本。"
   $setcolor_normal
   exit
fi

#判断系统中是否存在wget下载工具
if rpm --quiet -q wget ;then
   wget -c http://nginx.org/download/nginx-1.14.0.tar.gz
else
   $setcolor_failure
   echo -n "未找到wget,请先安装该软件。"
   $setcolor_normal
   exit
fi

#如果没有nginx账户，则脚本自动创建该账户
if ! id nginx &>/dev/null ;then
   adduser -s /sbin/nologin nginx
fi

#测试是否存在正确的源码包软件
#在源码编译安装前，请先安装相关依赖包
#gcc:C语言编译器，pcre-devel:Perl兼容的正则表达式
#zlib-devel:gzip压缩库，openssl-devel:Openssl加密库
if [[ ! -f nginx-1.14.0.tar.gz ]];then
    $setcolor_failure
    echo -n "未找到nginx源码包，请先正确下载该软件..."
    $setcolor_normal
    exit
else
   yum -y install gcc pcre-devel zlib-devel openssl-devel
   clear
   $setcolor_success
   echo -n "接下来，需要花几分钟时间编译源码安装nginx..."
   $setcolor_normal
   sleeep 6
   tar -xf nginx-1.14.0.tar.gz
#编译源码安装nginx,指定账户和组，指定安装路径，开启需要的模块，禁用不需要的模块
   cd nginx-1.14.0/
   ./configure \
   --user=nginx
   --group=nginx \
   --prefix=/data/server/nginx \
   --with-stream \
   --with-http_ssl_module \
   --with-http_stub_status_module \
   --without-http_autoindex_module \
   --without-http_ssi_module
   make
   make install
fi
if[[ -x /data/server/nginx/sbin/nginx ]];then
  clear
  $setcolor_success
  echo -n "一键部署nginx已经完成！"
  $setcolor_normal
fi
