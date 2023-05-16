//package com.chua.common.support.context.process;
//
//import com.chua.common.support.annotations.SpiIgnore;
//import com.chua.common.support.bean.BeanUtils;
//import com.chua.common.support.collection.ConfigureAttributes;
//import com.chua.common.support.collection.SortedArrayList;
//import com.chua.common.support.collection.SortedList;
//import com.chua.common.support.constant.Action;
//import com.chua.common.support.context.definition.DefinitionUtils;
//import com.chua.common.support.context.definition.ObjectDefinition;
//import com.chua.common.support.context.definition.TypeDefinition;
//import com.chua.common.support.context.enums.DefinitionType;
//import com.chua.common.support.context.factory.ConfigurableBeanFactory;
//import com.chua.common.support.function.InitializingAware;
//import com.chua.common.support.lang.expression.ExpressionProvider;
//import com.chua.common.support.lang.expression.listener.DelegateRefreshListener;
//import com.chua.common.support.lang.expression.listener.RefreshListener;
//import com.chua.common.support.monitor.*;
//import com.chua.common.support.mysql.MysqlMonitor;
//import com.chua.common.support.utils.ClassUtils;
//import lombok.Data;
//
//import javax.sql.DataSource;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static com.chua.common.support.context.constant.ContextConstant.COMPARATOR;
//
///**
// * mysql
// *
// * @author CH
// */
//@SpiIgnore
//public class MysqlBeanPostProcessor implements BeanPostProcessor, InitializingAware {
//
//    private final DataSource dataSource;
//    private Monitor monitor;
//    private MetadataRepository<BeanConfiguration> metadataRepository;
//
//    protected final Map<String, SortedList<TypeDefinition>> table = new ConcurrentHashMap<>();
//
//    private final Map<String, RefreshListener> listener = new ConcurrentHashMap<>();
//
//    public MysqlBeanPostProcessor(DataSource dataSource) {
//        this(dataSource, true);
//    }
//
//    public MysqlBeanPostProcessor(DataSource dataSource, boolean isMonitor) {
//        this.dataSource = dataSource;
//        setMonitor(isMonitor);
//        afterPropertiesSet();
//    }
//
//    private void setMonitor(boolean isMonitor) {
//        if (!isMonitor) {
//            return;
//        }
//        ConfigureAttributes sourceMetadata = ConfigureAttributes.create(dataSource);
//        String url = sourceMetadata.getStringHasDefault("", "driver-url", "url", "jdbc-url");
//
//        this.monitor = new MysqlMonitor().configuration(MonitorConfiguration.newBuilder()
//                .withUrl(url)
//                .withUsername(sourceMetadata.getStringHasDefault("root", "user", "username", "jdbc-user"))
//                .withPassword(sourceMetadata.getStringHasDefault("root", "passwd", "password", "jdbc-password"))
//        );
//
//        this.monitor.addListener(new com.chua.common.support.monitor.Listener<NotifyMessage>() {
//            @Override
//            public void onEvent(NotifyMessage message) {
//                if (message.getType() == NotifyType.CREATE) {
//                    List<BeanConfiguration> beanConfigurations = BeanUtils.copyPropertiesList(((UpdateNotifyMessage) message).getModifyData(), BeanConfiguration.class);
//                    for (BeanConfiguration beanConfiguration : beanConfigurations) {
//                        createBean(beanConfiguration);
//                    }
//                    return;
//                }
//
//                if (message.getType() == NotifyType.DELETE) {
//                    List<BeanConfiguration> beanConfigurations = BeanUtils.copyPropertiesList(((UpdateNotifyMessage) message).getModifyData(), BeanConfiguration.class);
//                    for (BeanConfiguration beanConfiguration : beanConfigurations) {
//                        removeBean(beanConfiguration);
//                    }
//                    return;
//                }
//
//                if (message.getType() == NotifyType.MODIFY) {
//                    List<BeanConfiguration> beanConfigurations = BeanUtils.copyPropertiesList(((UpdateNotifyMessage) message).getModifyData(), BeanConfiguration.class);
//                    for (BeanConfiguration beanConfiguration : beanConfigurations) {
//                        if (beanConfiguration.getStatus() == 0) {
//                            removeBean(beanConfiguration);
//                            continue;
//                        }
//
//                        RefreshListener mysqlListener = listener.get(beanConfiguration.getName());
//                        if (null == mysqlListener) {
//                            updateBean(beanConfiguration);
//                            continue;
//                        }
//                        mysqlListener.refresh(beanConfiguration.content);
//
//                    }
//                }
//            }
//        });
//        monitor.start();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                monitor.stop();
//            }
//        });
//    }
//
//    @Override
//    public void processInjection(TypeDefinition definition) {
//
//    }
//
//
//    @Override
//    public <T> List<TypeDefinition<T>> postProcessInstantiation(String bean, Class<T> targetType) {
//        List rs = new LinkedList<>();
//        if (null == bean) {
//            bean = targetType.getTypeName();
//        }
//        SortedList<TypeDefinition> sortedList = table.get(bean);
//        if (null == sortedList) {
//            return rs;
//        }
//
//        for (TypeDefinition definition : sortedList) {
//            if (definition.isAssignableFrom(targetType)) {
//                rs.add(definition);
//            }
//        }
//        SortedList<TypeDefinition> sortedList1 = table.get(targetType.getTypeName());
//        if (null != sortedList1) {
//            rs.addAll(sortedList1);
//        }
//
//        return rs;
//    }
//
//    @Override
//    public <T> List<TypeDefinition<T>> postProcessInstantiation(Class<T> targetType) {
//        SortedList sortedList = Optional.ofNullable(table.get(targetType.getTypeName())).orElse(SortedList.emptyList());
//        return sortedList;
//    }
//
//    @Override
//    public boolean isValid(TypeDefinition definition) {
//        return false;
//    }
//
//    @Override
//    public void unProcessInjection(String name, DefinitionType definitionType) {
//
//    }
//
//    @Override
//    public void refresh(ConfigurableBeanFactory standardConfigurableBeanFactory) {
//
//    }
//
//    @Override
//    public List<TypeDefinition<Object>> postBeanByMethod(Class<?>[] type) {
//        List<TypeDefinition<Object>> rs = new SortedArrayList<>(COMPARATOR);
//        Collection<SortedList<TypeDefinition>> values = table.values();
//        for (SortedList<TypeDefinition> value : values) {
//            for (TypeDefinition typeDefinition : value) {
//                if (typeDefinition.hasMethodByParameterType(type)) {
//                    rs.add(typeDefinition);
//                }
//            }
//        }
//        return rs;
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        autoTable();
//        autoBean();
//    }
//
//    private void autoBean() {
//        DataSourceRepositoryFactory factory = new DataSourceRepositoryFactory(dataSource);
//
//        this.metadataRepository = factory.build(BeanConfiguration.class);
//
//        SearchResult<BeanConfiguration> query = metadataRepository.query(
//                MapBuilder.newBuilder().field(BeanConfiguration::getStatus, 1)
//                        .orderBy(BeanConfiguration::getFreer).asc()
//        );
//        register(query);
//    }
//
//    private void register(SearchResult<BeanConfiguration> query) {
//        if (null == query) {
//            return;
//        }
//
//        List<BeanConfiguration> data = query.getData();
//        for (BeanConfiguration datum : data) {
//            createBean(datum);
//        }
//    }
//
//    private void createBean(BeanConfiguration datum) {
//        String name = datum.getName();
//        if (Strings.isNullOrEmpty(name)) {
//            return;
//        }
//
//        Class<?> aClass = ClassUtils.forName(datum.getInterfaces());
//        if (null == aClass) {
//            return;
//        }
//        ExpressionProvider.ExpressionProviderBuilder builder = ExpressionProvider
//                .newSource();
//
//        DelegateRefreshListener listener = new DelegateRefreshListener(datum.content);
//        builder.scriptType(datum.getMode()).listener(listener).source(datum.content);
//        this.listener.put(datum.getName(), listener);
//
//        DefinitionUtils.register(new ObjectDefinition<>(builder.build().createProxy(aClass))
//                .addType(aClass)
//                .addBeanName(datum.name)
//                .order(datum.getFreer()), table);
//    }
//
//    private void removeBean(BeanConfiguration datum) {
//        String name = datum.getName();
//        if (Strings.isNullOrEmpty(name)) {
//            return;
//        }
//
//        Class<?> aClass = ClassUtils.forName(datum.getInterfaces());
//        if (null == aClass) {
//            listener.remove(name);
//            table.remove(name);
//        }
//    }
//
//    private void updateBean(BeanConfiguration beanConfiguration) {
//        removeBean(beanConfiguration);
//        createBean(beanConfiguration);
//    }
//
//    private void autoTable() {
//        AutoMetadata autoMetadata = AutoMetadata.builder().build();
//        MetadataExecutor metadataExecutor = autoMetadata.doExecute(BeanConfiguration.class);
//        metadataExecutor.execute(dataSource, Action.UPDATE);
//    }
//
//
//    @Data
//    public static final class BeanConfiguration {
//
//        @Id
//        private Integer id;
//
//        @Column(value = "name", comment = "名称")
//        private String name;
//
//        @Column(value = "type", comment = "FILE: 文件; SOURCE:源码")
//        private String type;
//
//        @Column(value = "content", jdbcType = JdbcType.LONGTEXT, comment = "源码/文件路径")
//        private String content;
//
//        @Column(value = "mode", comment = "编译模式; java, groovy")
//        private String mode;
//        @Column(value = "status", defaultValue = "1", comment = "0:关闭;1:开启")
//        private Integer status;
//        @Column(value = "freer", defaultValue = "1", comment = "优先级")
//        private Integer freer;
//        @Column(value = "interfaces", comment = "脚本实现的类/接口")
//        private String interfaces;
//
//    }
//
//
//}
