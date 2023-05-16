//package com.chua.common.support.context.outside;
//
//import com.chua.common.support.constant.Action;
//import com.chua.common.support.context.definition.ObjectDefinition;
//import com.chua.common.support.context.factory.ApplicationContextConfiguration;
//import com.chua.common.support.context.factory.ConfigureApplicationContext;
//import com.chua.common.support.database.AutoMetadata;
//import com.chua.common.support.database.Database;
//import com.chua.common.support.database.annotation.Column;
//import com.chua.common.support.database.annotation.Id;
//import com.chua.common.support.database.annotation.Table;
//import com.chua.common.support.database.entity.JdbcType;
//import com.chua.common.support.database.executor.MetadataExecutor;
//import com.chua.common.support.database.inquirer.SubstanceInquirer;
//import com.chua.common.support.describe.Marker;
//import com.chua.common.support.describe.describe.FieldDescribe;
//import com.chua.common.support.expression.ExpressionProvider;
//import com.chua.common.support.function.DisposableAware;
//import com.chua.common.support.function.InitializingAware;
//import com.chua.common.support.monitor.*;
//import com.chua.common.support.mysql.MysqlMonitor;
//import com.chua.common.support.net.NetAddress;
//import com.chua.common.support.spi.SpiInitial;
//import com.chua.common.support.utils.ClassUtils;
//import com.chua.common.support.utils.FileUtils;
//import com.google.common.base.Strings;
//import lombok.Data;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.io.Serializable;
//import java.nio.file.WatchEvent;
//import java.sql.Connection;
//import java.util.List;
//
//import static com.chua.common.support.constant.CommonConstant.FILE;
//import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
//
///**
// * 外部数据库上下文
// *
// * @author CH
// */
//@SpiInitial(value = false)
//public class OutSideMysqlApplicationContext implements OutSideContext, InitializingAware, Listener<NotifyMessage>, DisposableAware {
//
//    private ApplicationContextConfiguration configuration;
//    private ConfigureApplicationContext context;
//
//    private DataSource dataSource;
//
//    private final Monitor mysqlMonitor;
//
//    public OutSideMysqlApplicationContext(DataSource dataSource) {
//        this.dataSource = dataSource;
//        Marker marker = Marker.of(dataSource);
//        String password = marker.createBench(FieldDescribe.builder().name("password").build()).execute().getStringValue();
//        if (Strings.isNullOrEmpty(password)) {
//            password = marker.createBench(FieldDescribe.builder().name("jdbcPassword").build()).execute().getStringValue();
//        }
//
//        String username = marker.createBench(FieldDescribe.builder().name("username").build()).execute().getStringValue();
//        if (Strings.isNullOrEmpty(password)) {
//            username = marker.createBench(FieldDescribe.builder().name("jdbcUser").build()).execute().getStringValue();
//        }
//        try (Connection connection = dataSource.getConnection()) {
//            NetAddress netAddress = NetAddress.of(connection.getMetaData().getURL());
//            mysqlMonitor = new MysqlMonitor();
//            mysqlMonitor.configuration(MonitorConfiguration.newBuilder().withUrl(netAddress.getAddress()).withUsername(username).withPassword(password));
//            mysqlMonitor.addListener(this);
//            mysqlMonitor.start();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public OutSideContext configuration(ConfigureApplicationContext context) {
//        this.context = context;
//        this.configuration = context.getApplicationContextConfiguration();
//        if (null == this.dataSource) {
//            this.dataSource = context.getBean(DataSource.class);
//        }
//        return this;
//    }
//
//    @Override
//    public void refresh() {
//        doInitialRefresh();
//    }
//
//    /**
//     * 初始化目录
//     */
//    private void doInitialRefresh() {
//        Database database = Database.newBuilder().datasource(dataSource).build();
//        AutoMetadata autoMetadata = AutoMetadata.builder().build();
//        MetadataExecutor metadataExecutor = autoMetadata.doExecute(BeanConfigurationInfo.class, null);
//        metadataExecutor.execute(dataSource, Action.UPDATE);
//
//        SubstanceInquirer<BeanConfigurationInfo> inquirer = database.createEntityInquirer(BeanConfigurationInfo.class, true);
//        try {
//            BeanConfigurationInfo tBeanConfigurationInfo1 = new BeanConfigurationInfo();
//            tBeanConfigurationInfo1.setEnable(1);
//            List<BeanConfigurationInfo> query = inquirer.query(tBeanConfigurationInfo1);
//            for (BeanConfigurationInfo tBeanConfigurationInfo : query) {
//                createBean(tBeanConfigurationInfo);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            try {
//                inquirer.close();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    private void createBean(BeanConfigurationInfo tBeanConfigurationInfo) {
//        Class<?> aClass = ClassUtils.forName(tBeanConfigurationInfo.getTarget());
//        if (null == aClass) {
//            return;
//        }
//
//        if (null == tBeanConfigurationInfo.getEnable() || 0 == tBeanConfigurationInfo.getEnable()) {
//            return;
//        }
//
//        ExpressionProvider provider = null;
//        Integer order = tBeanConfigurationInfo.getOrder();
//        if (FILE.equals(tBeanConfigurationInfo.getFormat())) {
//            provider = ExpressionProvider.newScript().script(tBeanConfigurationInfo.getSource()).build();
//        } else {
//            provider = ExpressionProvider.newScript().source(tBeanConfigurationInfo.getSource()).build();
//        }
//
//        context.registerBean(new ObjectDefinition(aClass)
//                .setObject(provider.create(aClass))
//                .order(null == order ? 0 : order)
//                .addBeanName(tBeanConfigurationInfo.getBean())
//                .setProxy(true));
//    }
//
//
//    /**
//     * 初始化对象
//     *
//     * @param temp 文件
//     * @param kind 类型
//     */
//    private void refresh(File temp, WatchEvent.Kind kind) {
//        String baseName = FileUtils.getName(temp.getParent());
//        if (!ClassUtils.isPresent(baseName)) {
//            return;
//        }
//
//
//        Class<?> aClass = ClassUtils.forName(baseName);
//        if (kind == ENTRY_CREATE) {
//            ExpressionProvider provider = ExpressionProvider.newScript()
//                    .script(temp.getAbsolutePath().replace(File.separator, "/"))
//                    .build();
//            context.registerBean(new ObjectDefinition(aClass)
//                    .setObject(provider.createProxy(aClass))
//                    .addBeanName(temp.getAbsolutePath(), FileUtils.getBaseName(temp))
//                    .setProxy(true));
//            return;
//        }
//
//        context.removeBean(temp.getAbsolutePath());
//    }
//
//    @Override
//    public void destroy() {
//        mysqlMonitor.stop();
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//
//        refresh();
//    }
//
//
//    /**
//     * 创建数据
//     *
//     * @param modifyData 数据
//     */
//    private void createBean(List<Serializable[]> modifyData) {
//        for (Serializable[] modifyDatum : modifyData) {
//            BeanConfigurationInfo tBeanConfigurationInfo = new BeanConfigurationInfo();
//            tBeanConfigurationInfo.setBean(String.valueOf(modifyDatum[1]));
//            tBeanConfigurationInfo.setSource(new String((byte[]) (modifyDatum[2])));
//            tBeanConfigurationInfo.setFormat(String.valueOf(modifyDatum[3]));
//            tBeanConfigurationInfo.setTarget(String.valueOf(modifyDatum[4]));
//            tBeanConfigurationInfo.setEnable(Integer.valueOf(String.valueOf(modifyDatum[5])));
//            tBeanConfigurationInfo.setOrder(Integer.valueOf(String.valueOf(modifyDatum[6])));
//            createBean(tBeanConfigurationInfo);
//        }
//    }
//
//    /**
//     * 删除数据
//     *
//     * @param modifyData 数据
//     */
//    private void deleteBean(List<Serializable[]> modifyData) {
//        for (Serializable[] modifyDatum : modifyData) {
//            try {
//                context.removeBean(String.valueOf(modifyDatum[1]));
//            } catch (Exception ignored) {
//            }
//            return;
//        }
//    }
//
//    @Override
//    public void onEvent(NotifyMessage message) {
//        if (message.getType() == NotifyType.MODIFY) {
//            UpdateNotifyMessage updateNotifyMessage = (UpdateNotifyMessage) message;
//            List<Serializable[]> modifyData = updateNotifyMessage.getModifyData();
//            List<Serializable[]> modifyData1 = updateNotifyMessage.getBeforeData();
//            deleteBean(modifyData1);
//            createBean(modifyData);
//            return;
//        }
//
//        if (message.getType() == NotifyType.DELETE) {
//            DeleteNotifyMessage message1 = (DeleteNotifyMessage) message;
//            deleteBean(message1.getModifyData());
//            return;
//        }
//
//        if (message.getType() == NotifyType.CREATE) {
//            WriteNotifyMessage message1 = (WriteNotifyMessage) message;
//            List<Serializable[]> modifyData = message1.getModifyData();
//            createBean(modifyData);
//            return;
//        }
//    }
//
//    @Data
//    @Table("T_BEAN_CONFIGURATION_INFO")
//    public static class BeanConfigurationInfo {
//
//        @Id
//        private Integer id;
//        @Column(comment = "bean")
//        private String bean;
//        @Column(comment = "源码", jdbcType = JdbcType.LONGTEXT)
//        private String source;
//        @Column(comment = "类型; file; source")
//        private String format;
//        @Column(comment = "目标类")
//        private String target;
//        @Column(comment = "优先级")
//        private Integer order;
//        @Column(comment = "1:启用", defaultValue = "1")
//        private Integer enable;
//    }
//}
