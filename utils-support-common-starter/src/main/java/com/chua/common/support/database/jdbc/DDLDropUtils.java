
package com.chua.common.support.database.jdbc;

import com.chua.common.support.database.jdbc.id.*;
import com.chua.common.support.database.jdbc.model.*;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * To transfer platform-independent model to drop DDL String array
 *
 * @author Yong Zhu
 * @since 1.0.2
 */
@Slf4j
@SuppressWarnings("all")
public class DDLDropUtils {
    /**
     * Transfer columnModels to drop column DDL String array like:
     * "alter table xx_table drop [column] xxx_column ... " by given dialect and columnModels <br/>
     * Please note: this method does not drop index/constraint/primary key/foreign keys related to column
     */
    public static String[] toDropColumnDDL(Dialect dialect, ColumnModel... columnModels) {
        DDLFeatures f = dialect.getDdlFeatures();
        List<String> result = new ArrayList<>();
        for (ColumnModel c : columnModels) {
            result.add(new StringBuilder("alter table ").append(c.getTableModel().getTableName()).append(" ")//
                    .append(f.dropColumnString).append(" ").append(c.getColumnName()).toString());
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Transfer tables to drop DDL and without format it
     */
    public static String[] toDropDDL(Dialect dialect, TableModel... tables) {
        // resultList store mixed drop DDL + drop Ojbects
        List<Object> objectResultList = new ArrayList<Object>();

        for (TableModel table : tables)
            transferTableToObjectList(dialect, table, objectResultList);

        boolean hasAutoIdGenerator = false;
        for (TableModel table : tables) {
            for (ColumnModel column : table.getColumns())
                if (GenerationType.AUTO.equals(column.getIdGenerationType())) {
                    hasAutoIdGenerator = true;
                    break;
                }
            for (IdGenerator idGens : table.getIdGenerators())
                if (hasAutoIdGenerator || idGens.dependOnAutoIdGenerator()) {
                    hasAutoIdGenerator = true;
                    break;
                }
        }

        List<String> stringResultList = new ArrayList<String>();
        List<TableIdGenerator> tbGeneratorList = new ArrayList<TableIdGenerator>();
        List<SequenceIdGenerator> sequenceList = new ArrayList<SequenceIdGenerator>();
        List<FKeyModel> fKeyConstraintList = new ArrayList<FKeyModel>();

        for (Object strOrObj : objectResultList) {
            if (!ObjectUtils.isEmpty(strOrObj)) {
                if (strOrObj instanceof String)
                    stringResultList.add((String) strOrObj);
                else if (strOrObj instanceof TableIdGenerator)
                    tbGeneratorList.add((TableIdGenerator) strOrObj);
                else if (strOrObj instanceof SequenceIdGenerator)
                    sequenceList.add((SequenceIdGenerator) strOrObj);
                else if (strOrObj instanceof FKeyModel)
                    fKeyConstraintList.add((FKeyModel) strOrObj);
            }
        }

        if (hasAutoIdGenerator) {
            IdGenerator realIdGen = AutoIdGenerator.INSTANCE.getSequenceOrTableIdGenerator(dialect);
            if (realIdGen instanceof TableIdGenerator)
                tbGeneratorList.add((TableIdGenerator) realIdGen);
            else if (realIdGen instanceof SequenceIdGenerator)
                sequenceList.add((SequenceIdGenerator) realIdGen);
            else
                throw new DialectException("Unknow exception happen for realIdGen, please report this bug");
        }

        buildDropSequenceDDL(dialect, stringResultList, sequenceList);
        buildDropTableGeneratorDDL(dialect, stringResultList, tbGeneratorList);
        outputDropFKeyConstraintDDL(dialect, stringResultList, fKeyConstraintList);

        String[] result = stringResultList.toArray(new String[stringResultList.size()]);
        if (Dialect.getGlobalAllowShowSql())
            log.info("Drop DDL:\r" + StringUtils.arrayToString(result, "\r"));
        return result;

    }

    /**
     * Transfer table to a mixed DDL String or TableGen Object list
     */
    private static void transferTableToObjectList(Dialect dialect, TableModel t, List<Object> objectResultList) {
        StringBuilder buf = new StringBuilder();
        String tableName = t.getTableName();
        List<ColumnModel> columns = t.getColumns();

        // Reserved words check
        dialect.checkNotEmptyReservedWords(tableName, "Table name", tableName);

        List<IndexModel> l = t.getIndexConsts();// check index names
        if (l != null && !l.isEmpty())
            for (IndexModel index : l)
                dialect.checkReservedWords(index.getName());

        List<UniqueModel> l2 = t.getUniqueConsts();// check unique names
        if (l2 != null && !l2.isEmpty())
            for (UniqueModel unique : l2)
                dialect.checkReservedWords(unique.getName());

        List<FKeyModel> fkeyChks = t.getFkeyConstraints();// check Fkey names
        if (fkeyChks != null && !fkeyChks.isEmpty())
            for (FKeyModel fkey : fkeyChks)
                dialect.checkReservedWords(fkey.getFkeyName());

        for (ColumnModel col : columns)
            if (!col.getTransientable())
                dialect.checkNotEmptyReservedWords(col.getColumnName(), "Column name", tableName);

        // idGenerator
        for (IdGenerator idGen : t.getIdGenerators())
            objectResultList.add(idGen);

        // Foreign key
        for (FKeyModel fkey : t.getFkeyConstraints())
            objectResultList.add(fkey);

        // drop table
        buf.append(dialect.dropTableDDL(tableName));
        objectResultList.add(buf.toString());
    }

    private static void buildDropSequenceDDL(Dialect dialect, List<String> stringResultList, List<SequenceIdGenerator> sequenceList) {
        Set<SequenceIdGenerator> notRepeatedSequences = new HashSet<SequenceIdGenerator>();
        for (SequenceIdGenerator seq : sequenceList)
            DDLCreateUtils.checkAndInsertToNotRepeatSeq(notRepeatedSequences, seq);
        DDLFeatures features = dialect.ddlFeatures;

        for (SequenceIdGenerator seq : notRepeatedSequences) {
            if (!features.supportBasicOrPooledSequence()) {
                DialectException.throwEX("Dialect \"" + dialect + "\" does not support sequence setting on sequence \"" + seq.getName() + "\"");
            }
            if (!DDLFeatures.NOT_SUPPORT.equals(features.dropSequenceStrings) && !StringUtils.isEmpty(features.dropSequenceStrings)) {
                stringResultList.add(0, StringUtils.replace(features.dropSequenceStrings, "_SEQNAME", seq.getSequenceName()));
            } else
                DialectException.throwEX("Dialect \"" + dialect + "\" does not support drop sequence ddl, on sequence \"" + seq.getName() + "\"");
        }
    }

    private static void buildDropTableGeneratorDDL(Dialect dialect, List<String> stringResultList, List<TableIdGenerator> tbGeneratorList) {
        for (TableIdGenerator tg : tbGeneratorList) {
            // @formatter:off
            DialectException.assureNotEmpty(tg.getName(), "TableGen name can not be empty");
            DialectException.assureNotEmpty(tg.getTable(),
                    "TableGen tableName can not be empty of \"" + tg.getName() + "\"");
            DialectException.assureNotEmpty(tg.getPkColumnName(),
                    "TableGen pkColumnName can not be empty of \"" + tg.getName() + "\"");
            DialectException.assureNotEmpty(tg.getPkColumnValue(),
                    "TableGen pkColumnValue can not be empty of \"" + tg.getName() + "\"");
            DialectException.assureNotEmpty(tg.getValueColumnName(),
                    "TableGen valueColumnName can not be empty of \"" + tg.getName() + "\"");
            // @formatter:on
        }

        Set<String> tableExisted = new HashSet<String>();
        for (TableIdGenerator tg : tbGeneratorList) {
            String tableName = tg.getTable().toLowerCase();
            if (!tableExisted.contains(tableName)) {
                stringResultList.add(0, dialect.dropTableDDL(tableName));
                tableExisted.add(tableName);
            }
        }
    }

    private static void outputDropFKeyConstraintDDL(Dialect dialect, List<String> stringResultList, List<FKeyModel> trueList) {
        if (DDLFeatures.NOT_SUPPORT.equals(dialect.ddlFeatures.addForeignKeyConstraintString))
            return;
        for (FKeyModel t : trueList) {
            if (!t.getDdl())
                continue; // if ddl is false, skip
            String dropStr = dialect.ddlFeatures.dropForeignKeyString;
            String constName = t.getFkeyName();
            if (StringUtils.isEmpty(constName))
                constName = "fk_" + t.getTableName().toLowerCase() + "_" + StringUtils.replace(StringUtils.listToString(t.getColumnNames()), ",", "_");
            constName = StringUtils.clearQuote(constName);
            if (DDLFeatures.NOT_SUPPORT.equals(dropStr))
                DialectException.throwEX("Dialect \"" + dialect + "\" does not support drop foreign key, for setting: \"" + "fk_" + constName + "\"");
            stringResultList.add(0, "alter table " + t.getTableName() + " " + dropStr + " " + constName);
        }
    }
}
