package com.chua.common.support.database.orm;


import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.database.orm.type.TypeHandler;
import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * <p>
 * sql 脚本工具类
 * </p>
 *
 * @author miemie
 * @since 2018-08-15
 */
@SuppressWarnings("serial")
public abstract class BaseSqlScriptUtils {

    /**
     * <p>
     * 获取 带 if 标签的脚本
     * </p>
     *
     * @param sqlScript sql 脚本片段
     * @return if 脚本
     */
    public static String convertIf(final String sqlScript, final String ifTest, boolean symbolNewline) {
        String newSqlScript = sqlScript;
        if (symbolNewline) {
            newSqlScript = symbolNewline + newSqlScript + symbolNewline;
        }
        return String.format("<if test=\"%s\">%s</if>", ifTest, newSqlScript);
    }

    /**
     * <p>
     * 获取 带 trim 标签的脚本
     * </p>
     *
     * @param sqlScript       sql 脚本片段
     * @param prefix          以...开头
     * @param suffix          以...结尾
     * @param prefixOverrides 干掉最前一个...
     * @param suffixOverrides 干掉最后一个...
     * @return trim 脚本
     */
    public static String convertTrim(final String sqlScript, final String prefix, final String suffix,
                                     final String prefixOverrides, final String suffixOverrides) {
        StringBuilder sb = new StringBuilder("<trim");
        if (StringUtils.isNotBlank(prefix)) {
            sb.append(" prefix=\"").append(prefix).append(SYMBOL_QUOTE);
        }
        if (StringUtils.isNotBlank(suffix)) {
            sb.append(" suffix=\"").append(suffix).append(SYMBOL_QUOTE);
        }
        if (StringUtils.isNotBlank(prefixOverrides)) {
            sb.append(" prefixOverrides=\"").append(prefixOverrides).append(SYMBOL_QUOTE);
        }
        if (StringUtils.isNotBlank(suffixOverrides)) {
            sb.append(" suffixOverrides=\"").append(suffixOverrides).append(SYMBOL_QUOTE);
        }
        return sb.append(SYMBOL_RIGHT_CHEV).append(SYMBOL_NEWLINE).append(sqlScript).append(SYMBOL_NEWLINE).append("</trim>").toString();
    }

    /**
     * <p>
     * 生成 choose 标签的脚本
     * </p>
     *
     * @param whenTest  when 内 test 的内容
     * @param otherwise otherwise 内容
     * @return choose 脚本
     */
    public static String convertChoose(final String whenTest, final String whenSqlScript, final String otherwise) {
        return "<choose>" + SYMBOL_NEWLINE
            + "<when test=\"" + whenTest + SYMBOL_QUOTE + SYMBOL_RIGHT_CHEV + SYMBOL_NEWLINE
            + whenSqlScript + SYMBOL_NEWLINE + "</when>" + SYMBOL_NEWLINE
            + "<otherwise>" + otherwise + "</otherwise>" + SYMBOL_NEWLINE
            + "</choose>";
    }

    /**
     * <p>
     * 生成 foreach 标签的脚本
     * </p>
     *
     * @param sqlScript  foreach 内部的 sql 脚本
     * @param collection collection
     * @param index      index
     * @param item       item
     * @param separator  separator
     * @return foreach 脚本
     */
    public static String convertForeach(final String sqlScript, final String collection, final String index,
                                        final String item, final String separator) {
        StringBuilder sb = new StringBuilder("<foreach");
        if (StringUtils.isNotBlank(collection)) {
            sb.append(" collection=\"").append(collection).append(SYMBOL_QUOTE);
        }
        if (StringUtils.isNotBlank(index)) {
            sb.append(" index=\"").append(index).append(SYMBOL_QUOTE);
        }
        if (StringUtils.isNotBlank(item)) {
            sb.append(" item=\"").append(item).append(SYMBOL_QUOTE);
        }
        if (StringUtils.isNotBlank(separator)) {
            sb.append(" separator=\"").append(separator).append(SYMBOL_QUOTE);
        }
        return sb.append(SYMBOL_RIGHT_CHEV).append(SYMBOL_NEWLINE).append(sqlScript).append(SYMBOL_NEWLINE).append("</foreach>").toString();
    }

    /**
     * <p>
     * 生成 where 标签的脚本
     * </p>
     *
     * @param sqlScript where 内部的 sql 脚本
     * @return where 脚本
     */
    public static String convertWhere(final String sqlScript) {
        return "<where>" + SYMBOL_NEWLINE + sqlScript + SYMBOL_NEWLINE + "</where>";
    }

    /**
     * <p>
     * 生成 set 标签的脚本
     * </p>
     *
     * @param sqlScript set 内部的 sql 脚本
     * @return set 脚本
     */
    public static String convertSet(final String sqlScript) {
        return "<set>" + SYMBOL_NEWLINE + sqlScript + SYMBOL_NEWLINE + "</set>";
    }

    /**
     * <p>
     * 安全入参:  #{入参}
     * </p>
     *
     * @param param 入参
     * @return 脚本
     */
    public static String safeParam(final String param) {
        return safeParam(param, null);
    }

    /**
     * <p>
     * 安全入参:  #{入参,mapping}
     * </p>
     *
     * @param param   入参
     * @param mapping 映射
     * @return 脚本
     */
    public static String safeParam(final String param, final String mapping) {
        String target = SYMBOL_HASH_LEFT_BRACE + param;
        if (StringUtils.isBlank(mapping)) {
            return target + SYMBOL_RIGHT_BRACE;
        }
        return target + SYMBOL_COMMA + mapping + SYMBOL_RIGHT_BRACE;
    }

    /**
     * <p>
     * 非安全入参:  ${入参}
     * </p>
     *
     * @param param 入参
     * @return 脚本
     */
    public static String unSafeParam(final String param) {
        return SYMBOL_DOLLAR_LEFT_BRACE + param + SYMBOL_RIGHT_BRACE;
    }

    public static String mappingTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        if (typeHandler != null) {
            return "typeHandler=" + typeHandler.getName();
        }
        return null;
    }

    public static String mappingJdbcType(JdbcType jdbcType) {
        if (jdbcType != null) {
            return "jdbcType=" + jdbcType.name();
        }
        return null;
    }

    public static String mappingNumericScale(Integer numericScale) {
        if (numericScale != null) {
            return "numericScale=" + numericScale;
        }
        return null;
    }

    public static String convertParamMapping(Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType, Integer numericScale) {
        if (typeHandler == null && jdbcType == null && numericScale == null) {
            return null;
        }
        String mapping = null;
        if (typeHandler != null) {
            mapping = mappingTypeHandler(typeHandler);
        }
        if (jdbcType != null) {
            mapping = appendMapping(mapping, mappingJdbcType(jdbcType));
        }
        if (numericScale != null) {
            mapping = appendMapping(mapping, mappingNumericScale(numericScale));
        }
        return mapping;
    }

    private static String appendMapping(String mapping, String other) {
        if (mapping != null) {
            return mapping + SYMBOL_COMMA + other;
        }
        return other;
    }
}
