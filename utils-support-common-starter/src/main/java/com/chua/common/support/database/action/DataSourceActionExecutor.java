package com.chua.common.support.database.action;

import com.chua.common.support.database.actuator.Actuator;
import com.chua.common.support.database.actuator.DataSourceActuator;
import com.chua.common.support.database.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.sql.DataSource;

/**
 * 动作执行器
 *
 * @author CH
 */
public enum DataSourceActionExecutor implements ActionExecutor<DataSourceActionExecutor.DataSourceActionMetadata, Object> {
    /**
     * update
     */
    UPDATE() {
        @Override
        public Object doExecute(DataSourceActionMetadata input) {
            Actuator actuator = new DataSourceActuator();
            actuator.doExecute("update", input);
            return null;
        }
    },

    /**
     * delete
     */
    DELETE(){
        @Override
        public Object doExecute(DataSourceActionMetadata input) {
            Actuator actuator = new DataSourceActuator();
            actuator.doExecute("drop", input);
            return null;
        }
    },

    /**
     * create
     */
    CREATE(){
        @Override
        public Object doExecute(DataSourceActionMetadata input) {
            Actuator actuator = new DataSourceActuator();
            actuator.doExecute("create", input);
            return null;
        }
    };


    @Data
    @AllArgsConstructor
    public static class DataSourceActionMetadata {

        private DataSource dataSource;

        private Expression expression;
    }
}
