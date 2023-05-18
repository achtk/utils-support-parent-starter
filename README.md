# 工具包

# 一、介绍
java工具包

| 模块                            | 说明        | 描述                                                                                                                   |
|-------------------------------|-----------|----------------------------------------------------------------------------------------------------------------------|
| utils-support-common-starter  | 基础工具包     | 1.消息总线<br />2.加解密<br />3.数据库查询<br />4.内存表<br />5.全文检索<br />6.可扩展的上下文管理器<br />7.对象解析器<br />8.下载器<br />9.文件转化器，子表，任务等等 |
| utils-support-image-starter   | 图片处理包     | 提供图片滤镜，格式转化等                                                                                                         |
| utils-support-hanlp-starter   | hanlp扩展包  | 提供分词hanlp实现                                                                                                          |
| utils-support-groovy-starter  | groovy扩展包 | 提供编译器groovy实现                                                                                                        |
| utils-support-example-starter | 部分例子      |                                                                                                                      |
| utils-support-pinyin-starter  | pinyin扩展包      | 提供pinyin4j实现                                                                                                         |
| utils-support-quartz-starter  | quartz扩展包      | 提供定时任务quartz实现                                                                                                             |

## 二、例子

## 2.1、地址解析

```java
NetAddress netAddress = NetAddress.of("http://www.baidu.com/s?wd=1");
System.out.println(netAddress);
```

## 2.2、字符/文件匹配

```java
ImageMatchingAlgorithm imageMatchingAlgorithm = new PixelSimilarityMatchingAlgorithm();
log.info("像素: {}", imageMatchingAlgorithm.match(source, target));
```

```java
String source = "232390070wd0283-2";
String target = "sdsd1a564sdsf564e65rq-2";

log.info("边界距离: {}", new EditDistanceMatchingAlgorithm().match(source, target));
log.info("余弦距离: {}", new CosineSimilarityMatchingAlgorithm().match(source, target));
```

## 2.3、上下文管理器

```java
//自动扫描注解 @AutoService
ConfigureApplicationContext applicationContext =
                ApplicationContextBuilder
                        .newBuilder()
                        .outSideContext(new OutSideApplicationContext()) //外部Bean，包括文件,脚本等
                        .build();
ApplicationService applicationService = applicationContext.getBean(ApplicationService.class);

while (true) {
    ThreadUtils.sleepSecondsQuietly(1);
    int size = applicationContext.getAnyBean(Encode.class).size();
    log.info("当前对象管理器存在对象数量: {}", size);

    if (size == 1) {
        Encode encode = applicationContext.getBean("Encode", Encode.class);
        log.info(encode.encode("asdsadasda1"));
    }
}
```

## 2.4、二维码

```java
//spi可扩展实现 {com.chua.tools.common.bar.BarCodeWriter}
//创建二维码
BarCodeBuilder.newBuilder()
    //内容
    .generate("http://www.baidu.com")
    .codeEyesPointColor("#778899")
    //码眼样式
    .codeEyesFormat(BarCodeBuilder.QrCodeEyesFormat.DR2_BORDER_C_POINT)
    //logo
    //.logo("E:\\data\\2.png")
    //背景图, 透明度
    //.bgImage("E:\\data\\bg.png", 0.5f)
    //二维码实现方式
    .transfer("zing2-code")
    //输出到文件
    .toFile("E:\\data\\2-code.png");
```

## 2.5、校验码

```java
//spi可扩展实现 {Captcha}
Captcha captcha = CaptchaBuilder.builder()
    .environment("captcha-type", AbstractCaptcha.TYPE_CHINESE)
    .environment("type", "gif").build();
captcha.toStream(new FileOutputStream(new File("E://captcha.gif")));
```

## 2.6、客户端实现

```java
//spi可扩展实现 {ClientProvider}
try (Client<Object> objectClient = ClientProvider
     .newProvider("zookeeper://127.0.0.1:2181?username=zk").create()) {
    objectClient.connect();

    OperateFactory operateFactory = objectClient.getOperateFactory();
    Object invoke = operateFactory.invoke(Operational.is().exist().node().getName(), "/s");
    System.out.println(invoke);
} catch (Exception e) {
    throw new RuntimeException(e);
}
```

## 2.7、服务端实现

```java
//spi可扩展实现 {ServerProvider}
Server<?> objectServer = ServerProvider.create("http", ServerOption.builder().build());
objectServer.start();
```

## 2.8、编译器

```java
//spi可扩展实现 {Compiler}
//Compiler compiler = ServiceProvider.of(Compiler.class).getExtension('jdk');
Compiler compiler = new JdkCompiler();
System.out.println(compiler);
```

## 2.9、转换器

```java
//spi可扩展实现 {TypeConverter}
log.info("String -> Integer => {}", Converter.convertIfNecessary("1", Integer.class));
```

## 2.10、日期

```java
LocalDateTime localDateTime = DateTime.now().withHour(17).withZeroMinute().toLocalDateTime();
DateTime dateTime = DateTime.now();
System.out.println(dateTime.duration(localDateTime));

log.info("unix => {}", dateTime.toUnixTimestamp());
log.info("当前时间 => {}", dateTime.getStringDate());
log.info("当前{}", dateTime.getDayOfWeekName());
log.info("-1d -> {}", DateTime.of("-1d").toString("yyyy-MM-dd HH:mm:ss"));
log.info("当前{}号", dateTime.getDayOfMonth());
log.info("当前一个月: {}", dateTime.getCurrentMonth());
log.info("前一天{}号", dateTime.minusDays(1).getDayOfMonth());
log.info("后一天{}号", dateTime.plusDays(1).getDayOfMonth());
log.info("获取本周一周时间: {}", dateTime.asRangeWeek());
log.info("获取本周第一天时间: {}", dateTime.firstDayOfWeek());
log.info("获取本周最后一天时间: {}", dateTime.lastDayOfWeek());
log.info("距离2022-05-01多少天: {}", dateTime.betweenOfLocalDate(DateTime.of("2022-05-01")));
log.info("获取当天12:30:01: {}", dateTime.withHour(12).withMinute(30).withSecond(1).toLocalDateTime());
log.info("获取当天12:30:01: {}", dateTime.withTime("12:30:01").toLocalDateTime());
log.info("获取当天12:30:01: {}", dateTime.withTime("12h30m1s").toLocalDateTime());
log.info("距离2021-05-01每天8:00, 11:00: {}", dateTime.betweenOfLocalDateTime(DateTime.of("2021-05-01"), "08:00", "11:00"));
log.info("判断当前时间是否是8:00 ~ 11:00: {}", dateTime.isRange("8:00", "11:00"));
log.info("判断当前时间是否是8:00: {}", dateTime.isTimeOfMinEquals("8:00"));
log.info("获取上2周 -> MONDAY: {}", dateTime.beforeDayOfCalendar(MONDAY, 2).withFirstTimeOfDay());
log.info("获取下2周 -> MONDAY: {}", dateTime.afterDayOfCalendar(MONDAY, 2).withFirstTimeOfDay());
log.info("获取上周 -> MONDAY: {}", dateTime.beforeDayOfCalendar(MONDAY).withFirstTimeOfDay());
log.info("获取下周 -> MONDAY: {}", dateTime.afterDayOfCalendar(MONDAY).withFirstTimeOfDay());
log.info("获取本周 -> MONDAY: {}", dateTime.beforeDayOfCalendar(MONDAY).withFirstTimeOfDay());
log.info("获取本周 -> MONDAY: {}", dateTime.getDayOfCalendar(MONDAY).withFirstTimeOfDay());
log.info("获取后面5天时间: {}", dateTime.afterDay(5));
log.info("获取前面5天时间: {}", dateTime.beforeDay(5));
log.info("获取前7天时间: {}", dateTime.asRangeDayUntil(-7));
log.info("获取后7天时间: {}", dateTime.asRangeDayUntil(7));
log.info("获取两个时间之间的时间段: {}", dateTime.asRange(dateTime.firstDayOfCurrentMonth(), new Date()));
log.info("获取两个时间之间天数: {}", dateTime.betweenOfDay(dateTime.firstDayOfWeek(), new Date()));
log.info("获取2020-12-21 12:12:12 => {}", dateTime.withYear(2020).withMonth(12).withDayOfMonth(21).withHour(12).withMinute(12).withSecond(12).toString("yyyy-MM-dd HH:mm:ss"));
log.info("获取2020-12-01 00:00:00 => {}", dateTime.withYear(2020).withMonth(12).withFirstDayOfMonth().toString("yyyy-MM-dd HH:mm:ss"));
log.info("获取2020-12-31 23:59:59 => {}", dateTime.withYear(2020).withMonth(12).withLastDayOfMonth().toString("yyyy-MM-dd HH:mm:ss"));

log.info("获取多个时间点: {}", dateTime.fullPoint(DAY_OF_MONTH, 1, 3, 4, 5));
log.info("获取多个时间区间: {}", dateTime.fullRange(DAY_OF_MONTH, 1, 3, 4, 5));

log.info("两个时间差: {}", Times.betweenOfDay("2021-12-29", "2021-12-1"));

int time = 10;
while (time > 0) {
    log.info("格式化持续时间: {}", Times.betweenOfFormat(time * 1000));
    ThreadUtils.sleepSecondsQuietly(1);
    time--;
}

LunarTime lunarTime = dateTime.toLunarTime();
System.out.println("农历年: " + lunarTime.getYearInGanZhi());
System.out.println("生肖年: " + lunarTime.getYearShengXiao());
System.out.println("干支月: " + lunarTime.getMonthInGanZhi());
System.out.println("农历月: " + lunarTime.getMonthInChinese());
System.out.println("生肖月: " + lunarTime.getMonthShengXiao());
System.out.println("当前节气: " + lunarTime.getJieQi());
System.out.println("下个节气: " + lunarTime.getNextJieQi() + " (" + lunarTime.getNextJieQi().getSolar().toString() + ")");
System.out.println("星座: " + lunarTime.getSolar().getXingZuo());
System.out.println(lunarTime.toFullString());
System.out.println(lunarTime.getSolar().toFullString());
```

## 2.11、定义

```java
//spi可扩展实现 {TypeDefinition}
TypeDefinition<TDemoExImpl> typeDefinition = new ClassDefinition<>(TDemoExImpl.class);
TDemoExImpl definitionObject = typeDefinition.getObject();
log.info("类型对应定义");

TypeDefinition<TDemoInfo> typeDefinition1 = new ScriptDefinition<>("classpath:TDemoInfoImpl.java", TDemoInfo.class);
TDemoInfo tDemoInfo = typeDefinition1.getObject();
log.info("脚本对应定义: tDemoInfo.getUuid() => {}", tDemoInfo.getUuid());


CompressDefinition typeDefinition2 = new UrlCompressDefinition<>(
    "https://archiva-maven-storage-prod.oss-cn-beijing.aliyuncs.com/repository/central/com/github/wywuzh/commons-dbutils/2.6.10.1.RELEASE/commons-dbutils-2.6.10.1.RELEASE.jar?Expires=1669088603&OSSAccessKeyId=LTAIfU51SusnnfCC&Signature=8rY5ysbsRZksiCrOs0A6pDqOjNk%3D");

TypeDefinition<List> definition = typeDefinition2.getDefinition(List.class);
```

## 2.12、发现服务

```java
//spi可扩展实现 {ServiceDiscovery}
ServiceDiscovery serviceDiscovery = ServiceProvider.of(ServiceDiscovery.class).getExtension("zookeeper");
serviceDiscovery.start();
serviceDiscovery.register(Discovery.builder().name("demo").address("172.16.137.61").port(8080).build());
serviceDiscovery.register(Discovery.builder().name("demo").address("180.101.49.11").port(80).build());

Discovery discovery1 = serviceDiscovery.discovery("demo");
Discovery discovery2 = serviceDiscovery.discovery("demo");
Discovery discovery3 = serviceDiscovery.discovery("demo");
System.out.println();
```

## 2.13、编排

```java
//spi可扩展实现 {Arrange}
public static void main(String[] args) throws Exception {
    //测试 1 -> 2 -> 3
    System.out.println("======================================================1 -> 2 -> 3");
    testOrder();
    //测试 1/2 -> 3
    System.out.println("======================================================1/2 -> 3");
    test1And2After3Order();
    //测试 1/2
    System.out.println("======================================================1/2");
    test1And2Order();
    //测试 1/2 1 -> 3 2 -> 4
    System.out.println("======================================================1/2 1 -> 3 2 -> 4");
    test1And2After3Or4Order();
    System.out.println("====================================================== 1 -> 3/4 -> 2");
    //测试  1 -> 3/4 -> 2
    test1After3And4OrderOr2();
}

private static void test1After3And4OrderOr2() throws Exception {
    DisruptorProvider<TDemoInfoEntity> disruptorProvider = DisruptorProvider
        .builder(TDemoInfoEntity.class)
        .build();


    if (disruptorProvider.isArrange()) {
        Arrange<TDemoInfoEntity> arrange = disruptorProvider.newArrange();
        ArrangeGroup<TDemoInfoEntity> arrangeGroup = arrange.group();
        arrangeGroup.handleEventsWith(new DisruptorExample.SimpleActor("h1", 1000));
        arrangeGroup.after("h1").handleEventsWith(new DisruptorExample.SimpleActor("h3", 3000), new DisruptorExample.SimpleActor("h4", 10000));
        arrangeGroup.after("h3", "h4").handleEventsWith(new DisruptorExample.SimpleActor("h2"));
    }
    disruptorProvider.start();
    disruptorProvider.publish(entity -> {
        entity.setUuid(UUID.randomUUID().toString());
    });
    System.out.println(Thread.currentThread().getName() + " 生产者发布一条数据");
    disruptorProvider.close();
}

private static void test1And2After3Or4Order() throws Exception {

    DisruptorProvider<TDemoInfoEntity> disruptorProvider = DisruptorProvider
        .builder(TDemoInfoEntity.class)
        .build();


    if (disruptorProvider.isArrange()) {
        Arrange<TDemoInfoEntity> arrange = disruptorProvider.newArrange();
        ArrangeGroup<TDemoInfoEntity> arrangeGroup = arrange.group();
        arrangeGroup.handleEventsWith(new DisruptorExample.SimpleActor("h1", 1000), new DisruptorExample.SimpleActor("h2", 1500));
        arrangeGroup.after("h1").handleEventsWith(new DisruptorExample.SimpleActor("h3"));
        arrangeGroup.after("h2").handleEventsWith(new DisruptorExample.SimpleActor("h4"));
    }
    disruptorProvider.start();
    disruptorProvider.publish(entity -> {
        entity.setUuid(UUID.randomUUID().toString());
    });
    System.out.println(Thread.currentThread().getName() + " 生产者发布一条数据");
    disruptorProvider.close();
}

private static void test1And2Order() throws Exception {
    DisruptorProvider<TDemoInfoEntity> disruptorProvider = DisruptorProvider
        .builder(TDemoInfoEntity.class)
        .build();


    if (disruptorProvider.isArrange()) {
        Arrange<TDemoInfoEntity> arrange = disruptorProvider.newArrange();
        ArrangeGroup<TDemoInfoEntity> arrangeGroup = arrange.group();
        arrangeGroup.handleEventsWith(new DisruptorExample.SimpleActor("h1", 1000), new DisruptorExample.SimpleActor("h2", 1500));
    }
    disruptorProvider.start();
    disruptorProvider.publish(entity -> {
        entity.setUuid(UUID.randomUUID().toString());
    });
    System.out.println(Thread.currentThread().getName() + " 生产者发布一条数据");
    disruptorProvider.close();
}

/**
     * <img class="marble"  src="doc/parallel.svg"/>
     *
     * @throws Exception
     */
private static void test1And2After3Order() throws Exception {
    DisruptorProvider<TDemoInfoEntity> disruptorProvider = DisruptorProvider
        .builder(TDemoInfoEntity.class)
        .build();


    if (disruptorProvider.isArrange()) {
        Arrange<TDemoInfoEntity> arrange = disruptorProvider.newArrange();
        ArrangeGroup<TDemoInfoEntity> arrangeGroup = arrange.group();
        arrangeGroup.handleEventsWith(new DisruptorExample.SimpleActor("h1", 1000), new DisruptorExample.SimpleActor("h2", 1500));
        arrangeGroup.after("h1", "h2").handleEventsWith(new DisruptorExample.SimpleActor("h3"));
    }
    disruptorProvider.start();
    disruptorProvider.publish(entity -> {
        entity.setUuid(UUID.randomUUID().toString());
    });
    System.out.println(Thread.currentThread().getName() + " 生产者发布一条数据");
    disruptorProvider.close();
}

private static void testOrder() throws Exception {
    DisruptorProvider<TDemoInfoEntity> disruptorProvider = DisruptorProvider
        .builder(TDemoInfoEntity.class)
        .build();


    if (disruptorProvider.isArrange()) {
        Arrange<TDemoInfoEntity> arrange = disruptorProvider.newArrange();
        ArrangeGroup<TDemoInfoEntity> arrangeGroup = arrange.group();
        arrangeGroup.handleEventsWith(new DisruptorExample.SimpleActor("h1", 1000));
        arrangeGroup.after("h1").handleEventsWith(new DisruptorExample.SimpleActor("h2", 1000));
        arrangeGroup.after("h2").handleEventsWith(new DisruptorExample.SimpleActor("h3"));
    }
    disruptorProvider.start();
    disruptorProvider.publish(entity -> {
        entity.setUuid(UUID.randomUUID().toString());
    });
    System.out.println(Thread.currentThread().getName() + " 生产者发布一条数据");
    disruptorProvider.close();
}
```

## 2.14、数据下发

```java
//spi可扩展实现 {Disruptor}
 
DisruptorProvider<TDemoInfoEntity> disruptorProvider = DisruptorProvider
     .builder(TDemoInfoEntity.class)
     //.entityFactory(TDemoInfoEntity::new)
     //.disruptor(JdkDisruptor.class)
     .build();


SimpleActor h1 = new SimpleActor("1");
SimpleActor h2 = new SimpleActor("2");
SimpleActor h3 = new SimpleActor("3");

disruptorProvider.register(h1);
disruptorProvider.register(h2);
disruptorProvider.register(h3);

disruptorProvider.start();
Cost cost = Cost.mill();

for (int i = 1; i <= 10_0; i++) {
    disruptorProvider.publish(entity -> {
        entity.setUuid(UUID.randomUUID().toString());
    });
    System.out.println(Thread.currentThread().getName() + " 生产者发布一条数据:" + i + " 订单ID：" + i);
}
disruptorProvider.close();
cost.console();
```

## 2.15、下载器

```java
Downloader downloader = Downloader.newBuilder().buffer(2 * 1024 * 1024).threads(8).build();
downloader.download("http://download.geonames.org/export/dump/CN.zip");
```

## 2.16、动态对象

```java
//spi可扩展实现 {DynamicBean}

DynamicBean demoInfoDynamicBean = DynamicStringBean
    .newBuilder()
    .method("getUuid", new BiFunction<String, Method, String>() {
        @Override
        public String apply(String s, Method method) {
            return UUID.randomUUID().toString();
        }
    })
    .build();

Object tDemoInfo1 = DynamicStringBean.newBuilder()
    .name("test")
    .field("name233", String.class).build().createBean(Object.class);

TDemoInfo tDemoInfo = demoInfoDynamicBean.createBean(TDemoInfo.class);
System.out.println(tDemoInfo.getUuid());

//        DynamicBean dynamicBean = DynamicScriptBean.newBuilder()
//                .name("classpath:TDemoInfoImpl.java")
//                .build();
//
//        TDemoInfo dynamicBeanBean = dynamicBean.createBean(TDemoInfo.class);
//        System.out.println(dynamicBeanBean.getUuid());
//        System.out.println(dynamicBeanBean.getUuid());
```

## 2.17、emoji

```java
System.out.println("=================================");
String emoji = EmojiFactory.of(UNICODE).parseToUnicode();
System.out.println(emoji);
String emoji1 = EmojiFactory.of(emoji).parseFromUnicode();
System.out.println(emoji1);
String emoji2 = EmojiFactory.of(emoji).parseToHtmlHex();
System.out.println(emoji2);
String emoji3 = EmojiFactory.of(emoji).parseToHtmlDecimal();
System.out.println(emoji3);
```

## 2.18、全文搜索

```java
//spi可扩展实现 {FullTextEngine}
FullTextEngine<Product> engine = Engine.fulltext(Product.class);
engine.addAll(MockUtils.createForList(Product.class));
List<Product> query1 = engine.search("a");
System.out.println(query1);


FullTextEngine<Product> engine1 = Engine.fulltext("lucene", Product.class);
engine.addAll(MockUtils.createForList(Product.class));
List<Product> query11 = engine1.search("id:1*");
System.out.println(query11);
```

## 2.20、表达式

```java
//spi可扩展实现 {ExpressionProvider}
public static void main(String[] args) throws IOException {
    testGroovyExpress();
    testJavaExpress();
    testJavaSourceExpress();
    testJsExpress();

}

private static void testGroovyExpress() {
    ExpressionProvider provider = ExpressionProvider.newBuilder(null).build();
    ExpressionProxy<TDemoInfo> newProxy = provider.newProxy(TDemoInfo.class, "run:**/TDemoInfoImpl.groovy");
    TDemoInfo proxy = newProxy.createProxy();
    log.info(proxy.getId());
}

private static void testJavaSourceExpress() throws IOException {
    ExpressionProvider provider = ExpressionProvider.newBuilder("java").build();
    ExpressionProxy<OutSideContext> newProxy = provider
        .newProxy(OutSideContext.class, IoUtils.toString(
            ResourceProvider.of("run:**/OutSideApplicationContext.java").getResource().getUrl(), UTF_8), ClassUtils.getDefaultClassLoader());
    OutSideContext outSideContext = newProxy.createProxy();
    outSideContext.refresh();
    System.out.println(newProxy);
}

private static void testJavaExpress() {
    ExpressionProvider provider = ExpressionProvider.newBuilder(null).build();
    ExpressionProxy<OutSideContext> newProxy = provider.newProxy(OutSideContext.class, "run:**/OutSideApplicationContext.java");
    OutSideContext outSideContext = newProxy.createProxy();
    outSideContext.refresh();
}


private static void testJsExpress() {
    ExpressionProvider provider = ExpressionProvider.newBuilder(null).build();
    ExpressionProxy<TDemoInfo> newProxy = provider.newProxy(TDemoInfo.class, "classpath:TDemoInfoImpl.js");

    TDemoInfo tDemoInfo = newProxy.createProxy();
    while (true) {
        ThreadUtils.sleepSecondsQuietly(1);
        System.out.println(tDemoInfo.getId());
    }

}
```

## 2.21、资源文件

```java
//spi可扩展实现 {ResourceFile}
ResourceFile resourceTar = ResourceFileBuilder.builder().open("D:\\work\\apache-maven-3.8.6-bin.tar.gz");
log.info("是否是文件: {}", resourceTar.isFile());
log.info("获取文件: ResourceFile.toFile -> {}", resourceTar.toFile());
if (resourceTar instanceof CompressFile) {
    log.info("文件列表: ResourceFile.printTree -> {}", ((CompressFile) resourceTar).printTree());
}


ResourceFile resourceFolder = ResourceFileBuilder.builder().open("E:\\home");
log.info("是否是文件: {}", resourceFolder.isFile());
```

## 2.22、导出文件

```java
//spi可扩展实现 {ExportFile}
public static void main(String[] args) throws Exception {
    testJsonExportFile();
    testCsvExportFile();
    testTsvExportFile();
    testDbfExportFile();
    testXmlExportFile();
    testXlsExportFile();
}

private static void testJsonExportFile() throws IOException {
    ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.json")))
        .header(Export.class)
        .type(JSON)
        .doRead(MockUtils.createForList(Export.class));
}

private static void testXlsExportFile() throws IOException {
    ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.xls")))
        .header(Export.class)
        .type("xls")
        .doRead(MockUtils.createForList(Export.class));
}

private static void testXmlExportFile() throws IOException {
    ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.xml")))
        .header(Export.class)
        .type("xml")
        .doRead(MockUtils.createForList(Export.class));
}

private static void testDbfExportFile() throws IOException {
    ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.dbf")))
        .header(Export.class)
        .type("dbf")
        .doRead(MockUtils.createForList(Export.class));
}

private static void testCsvExportFile() throws Exception {
    ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.csv")))
        .header(Export.class)
        .charset("gbk")
        .type("csv")
        .doRead(MockUtils.createForList(Export.class));

}

private static void testTsvExportFile() throws Exception {
    ExportFileBuilder.read(Files.newOutputStream(Paths.get("D:\\1\\test.tsv")))
        .header(Export.class)
        .type("tsv")
        .doRead(MockUtils.createForList(Export.class));

}
```

## 2.23、转化文件

```java
//spi可扩展实现 {FileConverter}
MediaConverter mediaConverter = MediaConverter.of("1.xls");
mediaConverter.convert("pdf", Files.newOutputStream(Paths.get("2.pdf")));
```

## 2.24、GEO

```java
Point source = Point.builder().longitude(120.644049).latitude(31.285887).build();
Point point = GCJ02.converterTo(WGS84).transform(source);
log.info("{} -> {}", source, point.toString());
```

## 2.26、http客户端

```java
//spi可扩展实现 {HttpClientInvoker}
List<String> months = new ArrayList<>();
for (int i = 1; i < 13; i++) {
    months.add("2022" + StringUtils.repeatBefore("0", 2, i + ""));
}

//可以通过spi扩展 com.chua.tools.common.http.builder.HttpClientBuilder
//创建okhttp的客户端
// HttpClientBuilder clientBuilder = HttpProvider.of("okhttp")
//创建默认【默认为jdk, 当存在httpclient则为httpclient】客户端get请求
HttpClientInvoker invoker = HttpClient.get()
    //http地址
    .url("https://api.apihubs.cn/holiday/get")
    //参数
    .body("field", "year,month,date,week,workday,holiday")
    .body("cn", 1)
    .body("year", 2022)
    .body("month", Joiner.on(",").join(months))
    .body("size", 500)
    .newInvoker();
HttpResponse response = invoker.execute();

Holiday holiday = response.content(Holiday.class);
```

## 2.27、图片

```java
//spi可扩展实现 {ResourceFile}
ResourceFile resourceFile = ResourceFileBuilder.builder().open("D:\\home\\微信图片_20221203112301.jpg");
if (resourceFile.isImageFile()) {

    try (FileOutputStream fileOutputStream = new FileOutputStream("E:\\data\\temp.jpg")) {

        ImageFile imageFile = (ImageFile) resourceFile;
        ImageEditFile imageEditFile = imageFile.toEditFile();
        ExifFile exifFile = imageFile.toExifFile();
        exifFile.removeExif(fileOutputStream);
    }
    System.out.println();
}
```

## 2.28、压缩流

```java
//spi可扩展实现 {CompressInputStream}
ResourceProvider provider = ResourceProvider.of("classpath:**/" + CN);
Resource resource = provider.getResource();
CompressInputStream compressInputStream = new CompressInputStream(resource.getUrl(), "CN.txt");
String string = IoUtils.toString(compressInputStream, StandardCharsets.UTF_8);
```

## 2.29、压缩流

```java
//spi可扩展实现 {CompressInputStream}
ResourceProvider provider = ResourceProvider.of("classpath:**/" + CN);
Resource resource = provider.getResource();
CompressInputStream compressInputStream = new CompressInputStream(resource.getUrl(), "CN.txt");
String string = IoUtils.toString(compressInputStream, StandardCharsets.UTF_8);



 try (CompressInputStream compressInputStream = new CompressInputStream(ResourceProvider.of("classpath:area.zip").getResource(), "area.txt")) {
     List<String> strings = IoUtils.readLines(new InputStreamReader(compressInputStream, UTF_8));
     System.out.println();
 } catch (IOException e) {
     throw new RuntimeException(e);
 }
```

## 2.30、JsonApi

```java
//spi可扩展实现 {}
 OpenFactory openFactory = OpenBuilder.newBuilder()
                .writerWithDefaultPrettyPrinter()
                .addDataSource(DataSourceUtils.createDefaultMysqlDataSource("jdbc:mysql://localhost:3306/xxl_job"))
                .addDataSource(DataSourceUtils.createDefaultMysqlDataSource("jdbc:mysql://localhost:3306/db_201907"))
                .addDataSource(DataSourceUtils.createDefaultMysqlDataSource("jdbc:mysql://localhost:3306/db_201906"))
                .addDataSource(DataSourceUtils.createMysqlDataSource("jdbc:mysql://192.168.110.100:3306/yunshang_auditoria?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true", "yunshang_auditoria", "yunshang_auditoria(iFto-?2=u"))
                .build();

```

```http
	* <pre>
       #get         查询数据
       #head        统计数据
       #post        保存数据
       #put         更新数据
       #delete      删除数据
 
       可以采用http
       e.g.
  </pre>
  单数据查询
  <p>
  =========================================================
  <pre>
 
  {
       "XxlJobInfo":{}
  }
 
  多数据查询
  =========================================================
 
  {
       "XxlJobInfo[]":{
       }
  }
 
  条件查询
  =========================================================
  {
       "XxlJobInfo":{
           "@count":1,                                     //每页数量
           "@page": 2                                      //第几页
           "id": 1                                         //ID = 1
           "@group": "id"                                  //根据id分组
           "@order": "id"                                  //根据id排序
           "/XxlJobInfo2/id": "id"                         //根据id关联XxlJobInfo2/id     [@->,@-<]
       },
       "XxlJobInfo2":{
       }
  }
  支持多数据库查询
  =========================================================
  支持多数据库分表查询
  =========================================================
  {
       "TOrder[]":{
           "sysTime": "[2019-06-14, 2019-08-14]"           //区间查询
       }
  }
  </pre>
```

## 2.31、加密

```java
Kit kit = Kit.newBuilder("C:\\Program Files\\Go")
    .excludeDepends("*.yml")
    .from("D:\\xxx.jar")
    .build("E:\\xxx-v1.0.jar");

kit.compile(true, false);

```

## 2.32、监听

```java
Monitor monitor = Monitor.create();
monitor.addObserver(new ListenerConfiguration());
monitor.redefine(null, null);

while (true) {
    ThreadUtils.sleepSecondsQuietly(1);
    //获取系统里的文件句柄
    JsonArray handles = monitor.getHandles();
    System.out.println(handles);
}

```

```java
public class ListenerConfiguration {
    @Listener(value = "E:\\data", action = DIRECT)
    public void listenerDirector(WatcherEvent watcherEvent) {
        System.out.println(watcherEvent);
    }

    @Listener(value = "", action = LOG)
    public void listenerLog(WatcherEvent watcherEvent) {
        System.out.println("----------------------------------------" + watcherEvent);
    }

    @Listener(value = "", action = TRACE)
    public void listenerTrace(WatcherEvent watcherEvent) {
        System.out.println("----------------------------------------" + watcherEvent);
    }

    @Listener(value = "E:\\data\\1.txt", action = LINE_FILE)
    public void listenerFile(WatcherEvent watcherEvent) {
        System.out.println(watcherEvent);
    }

    @Listener(value = "mysql://root:root@127.0.0.1:3306", action = MYSQL)
    public void listenerMysql(WatcherEvent watcherEvent) {
        System.out.println(watcherEvent);
    }
}
```

## 2.33、Lock

```java
//可Spi扩展{Lock}
//Lock lock = new FileLock("test");
Lock lock = ServiceProvider.of(Lock.class).getNewExtension("file", "test");
AtomicBoolean run = new AtomicBoolean(true);
List<String> index = new ArrayList<>();
ExecutorService executorService = ThreadUtils.newFixedThreadExecutor(3, "test", () -> {
    return new Runnable() {
        @Override
        public void run() {
            while (run.get()) {
                if (!lock.lock()) {
                    try {
                        index.add(Thread.currentThread().getName());
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    };
});
ThreadUtils.sleepSecondsQuietly(1);
run.set(false);
ThreadUtils.closeQuietly(executorService);

System.out.println(index.contains("test-0-1"));

```

## 2.34、接口映射

```java
//可Spi扩展{MappingResolver}
Idiom idiom = MappingProxy.create(Idiom.class);
List<IdiomQuery> query = idiom.query(1, 1000, "两");
System.out.println(query);
```

```java
/**
 * 成语
 *
 * @author CH
 */
@MappingAddress("https://route.showapi.com")
public interface Idiom {
    /**
     * 查询成语
     *
     * @param page 页码
     * @param row  每页数量
     * @param name 成语
     * @return 成语
     */
    @MappingResponse(value = "$.showapi_res_body.data", target = IdiomQuery.class)
    @MappingRequest("GET /1196-1?keyword=${name}&page=${page}&rows=${row}&showapi_appid=1191705&showapi_sign=882fe29fdbee429d9ac55e8d234ffa40")
    List<IdiomQuery> query(@MappingParam("page") int page, @MappingParam("row") int row, @MappingParam("name") String name);
}
```

## 2.35、类型描述

```java
//可Spi扩展{MethodAnnotationPostProcessor}{ParameterAnnotationPostProcessor}
 //创建制造器
Marker marker = Marker.of(new TDemoExImpl());
//获取方法
Bench checkWatch = marker.createBench(MethodDescribe.builder().name("timeout1").build());
log.info("@checkWatch {}", checkWatch.execute().getValue());
//获取方法
Bench checkDemo = marker.createBench(MethodDescribe.builder().name("checkDemo").build());
log.info("@checkDemo {}", checkDemo.execute(new TDemoExImpl()).getValue());
//获取方法
Bench testDefaultValue = marker.createBench(MethodDescribe.builder().name("testDefaultValue").build());
log.info("@DefaultValue {}", testDefaultValue.execute().getValue());
//获取方法
Bench bench = marker.createBench(MethodDescribe.builder().name("throwable").build());
//获取结果
log.info("@Retry {}", bench.execute(4).getValue(Integer.class));

//导入包
marker.imports(UUID.class.getTypeName());
//添加方法
marker.create(MethodDescribe.builder().name("uuid").body(BodyDescribe.builder()
                                                         .addReturnLine("UUID.randomUUID().toString()")
                                                         .build()).build());
//新建对象制造器
Marker marker1 = marker.ofMarker();
//获取方法
Bench bench1 = marker1.createBench(MethodDescribe.builder().name("uuid").build());
//获取结果
Value<Object> execute1 = bench1.execute();
System.out.println(execute1);
```

## 2.36、匹配器

```java
//可Spi扩展{PathMatcher}
String v = "https://xingzhengquhua.bmcx.com/330700000000__xingzhengquhua/";
String p = "*";
PathMatcher pathMatcher = new AntPathMatcher();
log.info("{} -> {} = {}", v, p, new ApachePathMatcher().match(v, v));
```

## 2.37、拼音

```java
//可Spi扩展{PinyinFactory}
String word = "单";
//通过spi获取 PinyinFactory 的实现
ServiceProvider<PinyinFactory> provider = ServiceProvider.of(PinyinFactory.class);
//获取实现
PinyinFactory pinyinFactory = provider.single();
//转化为拼音
System.out.println(word + "全拼：" + pinyinFactory.transfer(word));
```

## 2.38、占位符

```java

PlaceholderSupport placeholderSupport = new PlaceholderSupport();

PropertyResolver placeholderResolver = new StringValuePropertyResolver(placeholderSupport);
log.info(placeholderResolver.resolvePlaceholders("${spring.server:233}"));
log.info(placeholderResolver.resolvePlaceholders("${user.home:233}"));
```

## 2.39、对象池

```java
Pool<TestEntity> pool = new BoundBlockingPool<>(new ReflectObjectFactory<>(TestEntity.class));
```

## 2.40、配置文件

```java
//spi可扩展{ProfileResolver}
Profile profile = ProfileBuilder.newBuilder().build();
profile.addProfile("log4j.properties")
    .addProfile("log4j2.xml")
    .addProfile("application.yml")
    .addProfile("resources.tar")
    .addProfile("resources.zip");

log.info("server.port -> {}", profile.getShortValue("server.port"));
System.out.println();
```

## 2.41、进度条

```java
int max = 100000;
ConsoleProgressBar consoleProgressBar = new ConsoleProgressBar("测试", max);

int i = 0;
while (i < max) {
    consoleProgressBar.increment(1);
    ThreadUtils.sleepMillisecondsQuietly(50);
    i++;
}
consoleProgressBar.end();
```

## 2.42、资源查找器

```java
//spi可扩展{ResourceFactory}
List<Resource> resources1 = ResourceProvider.of("classpath*:**/*.*").getResources();
log.info("获取类加载器下的[所有]文件: {}", resources1.size());
List<Resource> resources11 = ResourceProvider.of("classpath:**/*.*").getResources();
log.info("获取类加载器下的文件: {}", resources11.size());

List<Resource> resources2 = ResourceProvider.of("filesystem*:**/*.*").getResources();
log.info("获取系统磁盘下的[所有]文件: {}", resources2.size());
List<Resource> resources21 = ResourceProvider.of("filesystem:**/*.*").getResources();
log.info("获取系统磁盘下的文件: {}", resources21.size());

System.out.println();
```

## 2.43、 任务调度

```java
 //通过spi扩展 {@link com.chua.tools.common.scheduler2.TimeScheduler}

//创建一个任务调度, 默认采用quartz
TimeScheduler timeScheduler = TimeScheduler
    //创建 jdk实现的任务调度
    .builder("quartz")
    .build();

//注册一个任务对象, 方式1
//注册的对象的方法, 必须包含{@link com.chua.tools.common.scheduler.Schedule}注解
//注解可以设备调度的名称, 默认以方法名作为调度名称, 调度名称用于控制调度
//  timeScheduler.register(new EntityDefinition(new TestSchedule()));
//注册一个任务对象, 方式2
//注册的对象的方法, 必须包含{@link com.chua.tools.common.scheduler.Schedule}注解
//注解可以设备调度的名称, 默认以方法名作为调度名称, 调度名称用于控制调度
timeScheduler.register(new TestSchedule());
//注册一个脚本
//参数1: 脚本对应的接口
//脚本对应的接口, 必须包含{@link com.chua.tools.common.scheduler.Schedule}注解
//参数3: 脚本的URL
//注解可以设备调度的名称, 默认以方法名作为调度名称, 调度名称用于控制调度
//        timeScheduler.register(new ScriptDefinition(TDemoInfo.class, ClassUtils.getDefaultClassLoader(), ResourceProvider.of("classpath:TDemoInfoImpl.java").getResource().getUrl()));

ThreadUtils.sleepSecondsQuietly(10);
//改变test任务的时间
timeScheduler.change("test", "0/1 * * * * ?");
ThreadUtils.sleepSecondsQuietly(20);
//改变test任务的时间
timeScheduler.change("test", "0/3 * * * * ?");
ThreadUtils.sleepSecondsQuietly(10);
//停止调度
timeScheduler.stop();
```

## 2.44、SPI

```java
//默认包含自定义加载器[META-INF/extensions], ServiceLoader加载器, 以及子包加载器
ServiceProvider<Encode> serviceProvider = ServiceProvider.of(Encode.class);
//获取所有实例
System.out.println(serviceProvider.list());
```

## 2.45、SQL

```java
TableFactory tableFactory = new CalciteTableFactory();
registerMemory(tableFactory);
registerFile(tableFactory);
registerDatasource(tableFactory);

Set set = tableFactory.tableNames();

List list01 = tableFactory.executeQuery("select * from area");
List list011 = tableFactory.executeQuery("select * from city");
List list0 = tableFactory.executeQuery("select * from ds");
List list = tableFactory.executeQuery("select t.* from ds left join xxl_job.xxl_job_log t on ds.id = t.id");
List list1 = tableFactory.executeQuery("select * from xxl_job.xxl_job_log");
List list2 = tableFactory.executeQuery("select * from db_201907.t_order_20190715");


private static void registerFile(TableFactory tableFactory) {        			      tableFactory.register(ResourceProvider.of("file:C:\\Users\\Administrator\\Desktop\\city.dbf").getResource().getUrlPath());
}

private static void registerMemory(TableFactory tableFactory) {
    tableFactory.register("ds", Holiday.class, MockUtils.createForList(Holiday.class));
}

private static void registerDatasource(TableFactory tableFactoryr) {
    HikariDataSource hikariDataSource = new HikariDataSource();
    hikariDataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/xxl_job");
    hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    hikariDataSource.setUsername("root");
    hikariDataSource.setPassword("root");
    tableFactoryr.register("xxl_job", hikariDataSource);

    HikariDataSource hikariDataSource1 = new HikariDataSource();
    hikariDataSource1.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/db_201907");
    hikariDataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
    hikariDataSource1.setUsername("root");
    hikariDataSource1.setPassword("root");
    tableFactoryr.register("db_201907", hikariDataSource1);
}
```

## 2.46、子表查询

```java
//spi可扩展
SubTableFactory tableFactory = SubTableBuilder.newBuilder()
    .env("sql.show", true)
    .addLogicTable(LogicTable.builder()
                   .logicTable("t_order")
                   .strategy(new ColumnSubTableStrategy("sys_time", Strategy.TABLE)).build())
    .register(createDefaultMysqlDataSource(localMysqlUrl("db_201907")))
    .build();

//        List<Date> dates = DateTime.of("2019-06-14").betweenDate(DateTime.of("2019-06-30"));
//        for (Date date : dates) {
//            try {
//                tableFactory.createSubTable("t_order_20190614", "db_201906.t_order_" + DateTime.of(date).toString("yyyyMMdd"));
//            } catch (Exception ignored) {
//            }
//        }

DataSource dataSource = tableFactory.dataSource();
QueryRunner queryRunner = new QueryRunner(dataSource);
Database database = Database.newBuilder().datasource(dataSource).build();

JdbcInquirer jdbcInquirer = database.createJdbcInquirer();
List<Map<String, Object>> query = jdbcInquirer.query("SELECT  *  FROM  db_201907.t_order  WHERE  1 = 1 AND t_order.sys_time = ? ", "2019-07-14");
log.info("{}", query);
}
```

## 2.47、订阅发布

```java
//通过spi扩展 {@link com.chua.tools.common.subscribe.event.SubscribeEvent}
//创建自定义订阅发布器
SubscribeProvider provider = SubscribeProvider.create();
//创建自定义订阅发布器, 并注入自定义线程池
SubscribeProvider subscribeProvider = SubscribeProvider.create(ThreadUtils.newFixedThreadExecutor(3, "sub"));
//添加监听的对象
//监听的对象方法必须是单一参数, 并且包含注解 {@link com.chua.tools.common.subscribe.annotations.Subscribe}
//注解可以自定义监听的管道类型 {@link Subscribe#type}
subscribeProvider.register(new SubscribeEntity());
subscribeProvider.register(new SubscribeEntity1());
subscribeProvider.register(new SubscribeEntity2());
ThreadUtils.sleepSecondsQuietly(1);
AtomicBoolean run = new AtomicBoolean(true);
SecureRandom random = new SecureRandom();
ThreadUtils.newThread(new Runnable() {
    @Override
    public void run() {
        ThreadUtils.sleepSecondsQuietly(20);
        run.set(false);
    }
}).start();
while (run.get()) {
    //下发参数
    subscribeProvider.post("demo", random.nextInt(Integer.MAX_VALUE));
}
ThreadUtils.sleepSecondsQuietly(1);
//关闭订阅器
subscribeProvider.close();
```

## 2.48、任务

```java
//通过spi扩展
//创建自定义订阅发布器
SubscribeProvider provider = SubscribeProvider.create();
//创建自定义订阅发布器, 并注入自定义线程池
SubscribeProvider subscribeProvider = SubscribeProvider.create(ThreadUtils.newFixedThreadExecutor(3, "sub"));
//添加监听的对象
//监听的对象方法必须是单一参数, 并且包含注解 {@link com.chua.tools.common.subscribe.annotations.Subscribe}
//注解可以自定义监听的管道类型 {@link Subscribe#type}
subscribeProvider.register(new SubscribeEntity());
subscribeProvider.register(new SubscribeEntity1());
subscribeProvider.register(new SubscribeEntity2());
ThreadUtils.sleepSecondsQuietly(1);
AtomicBoolean run = new AtomicBoolean(true);
SecureRandom random = new SecureRandom();
ThreadUtils.newThread(new Runnable() {
    @Override
    public void run() {
        ThreadUtils.sleepSecondsQuietly(20);
        run.set(false);
    }
}).start();
while (run.get()) {
    //下发参数
    subscribeProvider.post("demo", random.nextInt(Integer.MAX_VALUE));
}
ThreadUtils.sleepSecondsQuietly(1);
//关闭订阅器
subscribeProvider.close();
```

## 2.49、线程

```java
ThreadProvider threadProvider = ThreadProvider.empty();
ExecutorCounter executorCounter = threadProvider.newCachedThreadPool();
Cost cost = Cost.mill();
for (int i = 0; i < 1000000; i++) {
    int finalI = i;
    executorCounter.combine(new Runnable() {
        @Override
        public void run() {
            ThreadUtils.sleepMillisecondsQuietly(1);
        }
    });
}

executorCounter.allOfComplete();
cost.console();
```

## 等等
