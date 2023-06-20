package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.SqlModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sybase 数据库分页方言
 *
 * @author CH
 */
@Spi("sybase")
public class SybaseDialect extends OracleDialect {

    private final boolean hasTop; // sybase12.5.4以前，不支持select top

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
        Matcher select_ = Pattern.compile("SELECT ").matcher(tempSql);
        Matcher from_ = Pattern.compile(" FROM ").matcher(tempSql);
        List<Integer> selectIndex = new ArrayList<>(10);
        List<Integer> fromIndex = new ArrayList<>(10);
        while (select_.find()) {
            int start = select_.start();
            if (start == 0 || tempSql.charAt(start - 1) == ' ' || tempSql.charAt(start - 1) == '(') {
                selectIndex.add(start);
            }
        }
        while (from_.find()) {
            fromIndex.add(from_.start());
        }

        // 形成select与from的混合顺序下标列表
        List<Integer> indexList = new ArrayList<>(20);
        indexList.addAll(selectIndex);
        indexList.addAll(fromIndex);
        indexList.sort(Comparator.naturalOrder());
        // 无法匹配有效下标
        if (indexList.size() < 2) {
            return -1;
        }
        // 利用栈逻辑匹配select与from
        int selectCount = 1;
        for (int i = 1; i < indexList.size(); i++) {
            int each = indexList.get(i);
            if (fromIndex.contains(each)) {
                // pointer弹栈
                selectCount--;
            } else {
                // pointer压栈
                selectCount++;
            }
            // from将全部select弹出，代表当前这个from为主要from
            if (selectCount == 0) {
                return each;
            }
        }
        return -1;
    }
}
