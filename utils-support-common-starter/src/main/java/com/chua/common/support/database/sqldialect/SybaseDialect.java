package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.SqlModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.NumberConstant.NUM_2;

/**
 * sybase 数据库分页方言
 *
 * @author CH
 */
@Spi("sybase")
public class SybaseDialect extends OracleDialect {

    private final boolean hasTop; 

    public SybaseDialect() {
        this(false);
    }

    public SybaseDialect(boolean hasTop) {
        this.hasTop = hasTop;
    }


    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        int index = findMainFrom(originalSql);
        if (index == -1) {
            index = originalSql.toUpperCase().indexOf(" FROM ");
        }
        String sql = "select";
        if (hasTop) {
            sql += " top " + (offset + limit);
        }
        sql += " rownum=identity(12)," + originalSql.substring(6, index) + " into #t " + originalSql.substring(index);
        sql += " select * from #t where rownum > ? and rownum <= ? ";
        sql += "drop table #t ";
        return new SqlModel(sql, offset, offset + limit);
    }

    @Override
    public String driverClassName() {
        return "com.sybase.jdbc2.jdbc.SybDriver";
    }


    private static final Pattern SELECT = Pattern.compile("SELECT ");
    private static final Pattern FROM = Pattern.compile(" FROM ");
    /**
     * 查找主查询的FROM位置
     *
     * @param sql 需要查找的SQL
     * @return FROM位置的起始下标
     * @author lroyia
     * @since 2022年6月15日 17:57:28
     */
    private int findMainFrom(String sql) {
        String tempSql = sql.toUpperCase();
        tempSql = tempSql.replace("\n", " ").replace("\t", " ").replace("\r", " ");
        Matcher select = SELECT.matcher(tempSql);
        Matcher from = FROM.matcher(tempSql);
        List<Integer> selectIndex = new ArrayList<>(10);
        List<Integer> fromIndex = new ArrayList<>(10);
        while (select.find()) {
            int start = select.start();
            if (start == 0 || tempSql.charAt(start - 1) == ' ' || tempSql.charAt(start - 1) == '(') {
                selectIndex.add(start);
            }
        }
        while (from.find()) {
            fromIndex.add(from.start());
        }

        
        List<Integer> indexList = new ArrayList<>(20);
        indexList.addAll(selectIndex);
        indexList.addAll(fromIndex);
        indexList.sort(Comparator.naturalOrder());

        if (indexList.size() < NUM_2) {
            return -1;
        }
        
        int selectCount = 1;
        for (int i = 1; i < indexList.size(); i++) {
            int each = indexList.get(i);
            if (fromIndex.contains(each)) {
                
                selectCount--;
            } else {
                
                selectCount++;
            }
            
            if (selectCount == 0) {
                return each;
            }
        }
        return -1;
    }


    @Override
    public String getProtocol() {
        return "Sybase";
    }

}
