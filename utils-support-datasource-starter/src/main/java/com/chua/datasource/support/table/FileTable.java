package com.chua.datasource.support.table;

import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.reflection.dynamic.DynamicScriptBean;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;
import com.chua.datasource.support.file.FileSupport;
import com.chua.datasource.support.rule.AbstractFilterQueryTable;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;

import java.util.List;
import java.util.Map;

/**
 * file
 *
 * @author CH
 */
public class FileTable extends AbstractFilterQueryTable {
    private final String suffix;
    private final FileSupport fileSupport;

    /**
     * Creates a RuleTable.
     *
     * @param profile 配置
     */
    public FileTable(Profile profile) {
        String directory = profile.getString("directory");
        this.suffix = FileUtils.getSimpleExtension(directory);
        ServiceProvider<FileSupport> serviceProvider = ServiceProvider.of(FileSupport.class);
        Map<String, Class<FileSupport>> stringClassMap = serviceProvider.listType();
        for (Map.Entry<String, Class<FileSupport>> entry : stringClassMap.entrySet()) {
            Class<FileSupport> value = entry.getValue();
            serviceProvider.register(entry.getKey() + ".gz", DynamicScriptBean.newBuilder()
                    .name("java")
                    .source("public class " + value.getSimpleName() + "$GZ" + " extends " + value.getTypeName() + "{" +
                            "public " + value.getSimpleName() + "$GZ(com.chua.common.support.collection.ConfigureAttributes configureAttributes, String file, String suffix){super(configureAttributes, file, suffix);}" +
                            "public java.io.InputStream getStream() throws java.io.IOException {return new java.util.zip.GZIPInputStream(super.getStream()); }" +
                            "}")
                    .build().createType(FileSupport.class));

            serviceProvider.register(entry.getKey() + ".xz", DynamicScriptBean.newBuilder()
                    .name("java")
                    .source("public class " + value.getSimpleName() + "$XZ" + " extends " + value.getTypeName() + "{" +
                            "public " + value.getSimpleName() + "$XZ(com.chua.common.support.collection.ConfigureAttributes configureAttributes, String file, String suffix){super(configureAttributes, file, suffix);}" +
                            "public java.io.InputStream getStream() throws java.io.IOException {return new com.chua.common.support.file.xz.XZInputStream(super.getStream()); }" +
                            "}")
                    .build().createType(FileSupport.class));
        }
        try {
            fileSupport = serviceProvider.getNewExtension(suffix, profile, directory, suffix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return fileSupport.getRowType(typeFactory);
    }

    @Override
    protected Enumerable<Object[]> getEnumerator(DataContext root, List<RexNode> filters) {
        return fileSupport.aggregate(null, null, null);
    }
}
