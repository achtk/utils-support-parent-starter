package com.chua.datasource.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.file.univocity.parsers.common.IterableResult;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;
import com.chua.common.support.file.univocity.parsers.common.ResultIterator;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParser;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParserSettings;
import com.chua.common.support.lang.profile.Profile;
import com.chua.datasource.support.rule.TableEnumerator;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * tcv
 *
 * @author CH
 */
@Spi("tsv")
public class TsvSupport extends AbstractFileSupport {
    TsvParserSettings settings = new TsvParserSettings();
    protected AbstractEnumerable<Object> dataList;

    public TsvSupport(Profile configureAttributes, String file, String suffix) {
        super(configureAttributes, file, suffix);
        if (Projects.isWindows()) {
            settings.getFormat().setLineSeparator("\n");
        } else {
            settings.getFormat().setLineSeparator("\r\n");
        }
        if (mem) {
            this.dataList = new AbstractEnumerable<Object>() {
                @Override
                public Enumerator<Object> enumerator() {
                    return new TableEnumerator(getDataList());
                }
            };
            ;
        }
    }

    /**
     * Returns the data list of the table.
     */
    public List<Object[]> getDataList() {
        TsvParser parser = new TsvParser(settings);
        try (InputStream stream = getStream()) {
            List strings = parser.parseAll(stream);
            return strings.subList(1, strings.size());
        } catch (IOException ignored) {
        }
        return Collections.emptyList();
    }

    @Override
    protected InputStream getStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        final List<RelDataType> types = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        String[] line = null;
        TsvParser parser = new TsvParser(settings);
        try (InputStream inputStream = getStream()) {
            IterableResult<String[], ParsingContext> iterate = parser.iterate(inputStream);
            ResultIterator<String[], ParsingContext> iterator = iterate.iterator();
            while (iterator.hasNext()) {
                line = iterator.next();
                break;
            }
        } catch (IOException ignored) {
        }
        for (String s : line) {
            if (s.startsWith("\"")) {
                s = s.substring(1);
            }

            if (s.endsWith("\"")) {
                s = s.substring(0, s.length() - 1);
            }
            names.add(mapping(s));
            types.add(relDataTypeFactory.createSqlType(SqlTypeName.VARCHAR));
        }
        return relDataTypeFactory.createStructType(Pair.zip(names, types));
    }

    @Override
    public Enumerable<Object> find(Profile attributes, String filterJson, String projectJson, List<Map.Entry<String, Class>> fields) {
        return aggregate(profile, null, null);
    }

    @Override
    public Enumerable<Object> aggregate(Profile configureAttributes, List<Map.Entry<String, Class>> fields, List<String> operations) {
        if (mem) {
            return dataList;
        }
        return new AbstractEnumerable<Object>() {
            @Override
            public Enumerator<Object> enumerator() {
                return new TableEnumerator(getDataList());
            }
        };
    }
}
