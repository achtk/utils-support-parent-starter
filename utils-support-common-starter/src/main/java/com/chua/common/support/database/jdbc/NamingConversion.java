
package com.chua.common.support.database.jdbc;

import com.chua.common.support.unit.name.NamingCase;

/**
 * Set global NamingConversion for entity which have no &#064;table and &#064;column annotation
 *
 * <pre>
 * For example:
 *   //OrderDetail.class map to OrderDetail database table, entity field OrderPrice map to OrderPrice column
 *   Dialect.setGlobalNamingRule(NamingRule.NONE);
 *
 *   //OrderDetail.class map to order_detail database table, entity field OrderPrice map to order_price column
 *   Dialect.setGlobalNamingRule(NamingRule.LOWER_CASE_UNDERSCORE);  //
 *
 *   //OrderDetail.class map to ORDER_DETAIL database table, entity field OrderPrice map to ORDER_PRICE column
 *   Dialect.setGlobalNamingRule(NamingRule.UPPER_CASE_UNDERSCORE);
 * </pre>
 *
 * @author Yong
 * @since 5.0.10
 */
public interface NamingConversion {

    /**
     * Get table name from entity class
     */
    String getTableName(Class<?> clazz);

    /**
     * Get column name from entity field
     */
    String getColumnName(String entityField);

    static final NamingConversion NONE = null;
    static final NamingConversion LOWER_CASE_UNDERSCORE = new LowerCaseUnderscoreConversion();
    static final NamingConversion UPPER_CASE_UNDERSCORE = new UpperCaseUnderscoreConversion();


    public static class LowerCaseUnderscoreConversion implements NamingConversion {
        @Override
        public String getTableName(Class<?> clazz) {
            return NamingCase.toCamelCase(clazz.getSimpleName());
        }

        @Override
        public String getColumnName(String entityField) {
            return NamingCase.toCamelCase(entityField);
        }
    }

    public static class UpperCaseUnderscoreConversion implements NamingConversion {
        @Override
        public String getTableName(Class<?> clazz) {
            return NamingCase.toCamelCase(clazz.getSimpleName()).toUpperCase();
        }

        @Override
        public String getColumnName(String entityField) {
            return NamingCase.toCamelCase(entityField).toUpperCase();
        }
    }

}
