package com.chua.common.support.database.inquirer;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 声明配置
 *
 * @author CH
 */
@Data
@Accessors(chain = true)
public class StatementConfiguration {
    private final Integer fetchDirection;
    private final Integer fetchSize;
    private final Integer maxFieldSize;
    private final Integer maxRows;
    private final Integer queryTimeout;

    public boolean isFetchDirectionSet() {
        return null != fetchDirection;
    }

    public boolean isFetchSizeSet() {
        return null != fetchSize;
    }

    public boolean isMaxFieldSizeSet() {
        return null != maxFieldSize;
    }

    public boolean isMaxRowsSet() {
        return null != maxRows;
    }

    public boolean isQueryTimeoutSet() {
        return null != queryTimeout;
    }
}
