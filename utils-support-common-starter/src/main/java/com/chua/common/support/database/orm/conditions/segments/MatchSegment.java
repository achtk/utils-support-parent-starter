
package com.chua.common.support.database.orm.conditions.segments;

import com.chua.common.support.database.orm.conditions.ISqlSegment;
import com.chua.common.support.database.orm.enums.SqlKeyword;
import com.chua.common.support.database.orm.enums.WrapperKeyword;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * 匹配片段
 *
 * @author miemie
 * @since 2018-06-27
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum MatchSegment {
    /**
     * GROUP_BY
     */
    GROUP_BY(i -> i == SqlKeyword.GROUP_BY),
    /**
     * ORDER_BY
     */
    ORDER_BY(i -> i == SqlKeyword.ORDER_BY),
    /**
     * NOT
     */
    NOT(i -> i == SqlKeyword.NOT),
    /**
     * AND
     */
    AND(i -> i == SqlKeyword.AND),
    /**
     * OR
     */
    OR(i -> i == SqlKeyword.OR),
    /**
     * AND_OR
     */
    AND_OR(i -> i == SqlKeyword.AND || i == SqlKeyword.OR),
    /**
     * EXISTS
     */
    EXISTS(i -> i == SqlKeyword.EXISTS),
    /**
     * HAVING
     */
    HAVING(i -> i == SqlKeyword.HAVING),
    /**
     * APPLY
     */
    APPLY(i -> i == WrapperKeyword.APPLY);

    private final Predicate<ISqlSegment> predicate;

    public boolean match(ISqlSegment segment) {
        return getPredicate().test(segment);
    }
}
