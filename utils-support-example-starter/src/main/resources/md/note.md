**1、启动FirewallD服务**命令：

```bash
systemctl start firewalld.service #开启服务
systemctl enable firewalld.service #设置开机启动
```

**2、查看FirewallD防火墙状态**：

```shell
systemctl status firewalld
```

## FirewallD 常用的命令：

> ```bash
> firewall-cmd --state ##查看防火墙状态，是否是running
> systemctl status firewalld.service ##查看防火墙状态
> systemctl start firewalld.service ##启动防火墙
> systemctl stop firewalld.service ##临时关闭防火墙
> systemctl enable firewalld.service ##设置开机启动防火墙
> systemctl disable firewalld.service ##设置禁止开机启动防火墙
> firewall-cmd --permanent --query-port=80/tcp ##查看80端口有没开放
> firewall-cmd --reload ##重新载入配置，比如添加规则之后，需要执行此命令
> firewall-cmd --get-zones ##列出支持的zone
> firewall-cmd --get-services ##列出预定义的服务
> firewall-cmd --query-service ftp ##查看ftp服务是否放行，返回yes或者no
> firewall-cmd --add-service=ftp ##临时开放ftp服务
> firewall-cmd --add-service=ftp --permanent ##永久开放ftp服务
> firewall-cmd --remove-service=ftp --permanent ##永久移除ftp服务
> firewall-cmd --add-port=80/tcp --permanent ##永久添加80端口
> firewall-cmd --zone=public --remove-port=80/tcp --permanent ##移除80端口
> iptables -L -n ##查看规则，这个命令是和iptables的相同的
> man firewall-cmd ##查看帮助
> 参数含义：
> --zone #作用域
> --permanent #永久生效，没有此参数重启后失效
> ```
>
>

**ipv4 端口转发**

> ```shell
> echo "net.ipv4.ip_forward = 1" >> /etc/sysctl.conf
> 
> sysctl -p
> ```
>
>

**需求一（内网服务器之间端口转发）**

**将本地的 192.168.20.3:6666 端口 转发 至 192.168.20.4:22 端口**

- **具体操作**

> ```shell
> firewall-cmd --list-all
> firewall-cmd --add-forward-port=port=33060:proto=tcp:toport=3306:toaddr=127.0.0.1 --zone=public --permanent
> 
> firewall-cmd --remove-forward-port=port=33060:proto=tcp:toport=3306:toaddr=127.0.0.1 --zone=public --permanent
> 
> firewall-cmd --reload
> firewall-cmd --list-all
> ```
>
>

- **端口测试**

  > 原来，是用 ssh 连接 server-2 192.168.20.4 的 22 端口
  >
  > ```shell
  > ssh root@192.168.20.4
  > ```
  >
  > 现在，通过 ssh 远程 server-1 192.168.20.3 的 6666 端口，连接 server-2 192.168.20.4
  >
  > ```shell
  > ssh -p 6666 root@192.168.20.3
  > ```

- **需求二（内网服务器之间端口转发）**

  将本地 192.168.20.3:9999 端口 转发 至 192.168.20.5:80 端口

    - **具体操作**

> ```shell
  > firewall-cmd --list-all
  > firewall-cmd --add-forward-port=port=33060:proto=tcp:toport=3306:toaddr=127.0.0.1 --zone=public --permanent
  > firewall-cmd --reload
  > firewall-cmd --list-all
  > ```

- **端口测试**

- > 原来，浏览器访问 server-3 192.168.20.5 的 80 端口
  >
  > http://192.168.20.5/
  >
  > 配置端口转发后，浏览器访问 server-1 192.168.20.3 的 9999 端口
  > http://192.168.20.3:9999/

- **FirewallD is not running**

- 开启防火墙