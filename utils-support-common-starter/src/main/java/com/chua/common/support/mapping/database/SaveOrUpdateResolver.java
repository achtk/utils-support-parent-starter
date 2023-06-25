package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_INSERT;

/**
 * 解释器
 *
 * @author CH
 */
@Extension("saveOrUpdate")
public class SaveOrUpdateResolver implements Resolver{

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        Object arg = args[0];
        Object key = metadata.getKeyPropertyValue(arg);
        if(null == key) {
            Object resolve = new SaveResolver().resolve(dataSource, args, metadata);
            return null != resolve ? 1 : 0;
        }

        return new UpdateByIdResolver().resolve(dataSource, args, metadata);
    }
}
