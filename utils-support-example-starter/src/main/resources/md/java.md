**1.jpda**

```shell
XX:+DisableAttachMechanism
-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=15050
```

```shell
-Xms1024m
-Xmx2048m
-XX:ReservedCodeCacheSize=240m
-XX:+UseCompressedOops
```

-Xms设置JVM的初始分配内存

-Xmx设置JVM的最大内存限制

-XX:ReservedCodeCacheSize是为了避免频繁编译导致代码缓存过多

-XX:+UseCompressedOops则表示使用压缩指针技术，以减小内存占用
