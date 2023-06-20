package com.chua.common.support.database.inquirer;

import com.chua.common.support.database.ResultSetUtils;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * jdbc查询器
 *
 * @author CH
 */
public class JdbcInquirer extends AbstractJdbcInquirer implements SqlInquirer, Inquirer {

    private final boolean isAutoCommit;

    public JdbcInquirer(DataSource dataSource, boolean isAutoCommit) {
        super(dataSource, isAutoCommit, null);
        this.isAutoCommit = isAutoCommit;
    }

    public JdbcInquirer(DataSource dataSource, boolean isAutoCommit, StatementConfiguration stmtConfig) {
        super(dataSource, isAutoCommit, stmtConfig);
        this.isAutoCommit = isAutoCommit;
    }

    public JdbcInquirer(DataSource dataSource, boolean isAutoCommit, StatementConfiguration stmtConfig, boolean pmdKnownBroken) {
        super(dataSource, isAutoCommit, stmtConfig, pmdKnownBroken);
        this.isAutoCommit = isAutoCommit;
    }

    @Override
    public int execute(String sql, Object... args) throws Exception {
        Connection conn = this.prepareConnection();
        conn.setAutoCommit(true);
        CallableStatement stmt = null;
        int rows = 0;

        try {
            stmt = this.prepareCall(conn, sql);
            this.fillStatement(stmt, args);
            stmt.execute();
            rows = stmt.getUpdateCount();
        } catch (SQLException e) {
            this.rethrow(e, sql, args);
            if (!isAutoCommit) {
                conn.rollback();
            }
        } finally {
            close(stmt);
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }
            close(conn);
        }

        return rows;
    }


    @Override
    public int executeStatement(String sql, Object... args) throws Exception {
        Connection conn = this.prepareConnection();
        conn.setAutoCommit(true);
        Statement stmt = null;
        int rows = 0;

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return 1;
        } catch (SQLException e) {
            this.rethrow(e, sql, args);
            if (!isAutoCommit) {
                conn.rollback();
            }
        } finally {
            close(stmt);
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }
            close(conn);
        }

        return rows;
    }

    @Override
    public int executeUpdate(String sql, Object... params) throws Exception {
        Connection conn = this.prepareConnection();
        conn.setAutoCommit(isAutoCommit);
        PreparedStatement stmt = null;
        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            this.rethrow(e, sql, params);
            if (!isAutoCommit) {
                conn.rollback();
            }
        } finally {
            close(stmt);
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }
            close(conn);
        }
        return -1;
    }

    @Override
    @SneakyThrows
    public int[] batch(String sql, Object[][] params) {
        Connection conn = this.prepareConnection();
        conn.setAutoCommit(isAutoCommit);
        PreparedStatement stmt = null;
        int[] rows = null;
        try {
            stmt = this.prepareStatement(conn, sql);
            for (Object[] param : params) {
                this.fillStatement(stmt, param);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int i = 0;
            rows = new int[rows.length];
            while (generatedKeys.next()) {
                rows[i++] = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            this.rethrow(e, sql, (Object[]) params);
            if (!isAutoCommit) {
                conn.rollback();
            }
        } finally {
            close(stmt);
            if (!isAutoCommit) {
                conn.commit();
                conn.setAutoCommit(true);
            }
            close(conn);
        }

        return rows;
    }

    @Override
    @SneakyThrows
    public int update(String sql, Object[] params) {
        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }

        Connection conn = this.prepareConnection();
        conn.setAutoCommit(true);
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.fillStatement(stmt, params);
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
            this.rethrow(e, sql, params);
            if (!isAutoCommit) {
                conn.rollback();
            }
        } finally {
            close(stmt);
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }
            close(conn);
        }

        return rows;

    }

    @Override
    @SneakyThrows
    public <T> T insert(String sql, Object[] params, Class<T> beanType) {
        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }

        Connection conn = this.prepareConnection();
        conn.setAutoCommit(true);
        Statement stmt = null;
        T generatedKeys = null;
        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.fillStatement((PreparedStatement) stmt, params);
            ((PreparedStatement)stmt).executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            generatedKeys = ResultSetUtils.handleOne(resultSet, beanType);
        } catch (SQLException e) {
            if(e instanceof SQLFeatureNotSupportedException) {
                stmt = conn.createStatement();
                try {
                    stmt.executeUpdate(String.format(sql.replace("?", "'%s'"), params));
                    ResultSet resultSet = stmt.getGeneratedKeys();
                    generatedKeys = ResultSetUtils.handleOne(resultSet, beanType);
                } catch (SQLException ex) {
                    e = ex;
                }
            }
            this.rethrow(e, sql, params);
            if (!isAutoCommit) {
                conn.rollback();
            }
        } finally {
            close(stmt);
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }
            close(conn);
        }

        return generatedKeys;

    }

    @Override
    @SneakyThrows
    public <T> List<T> query(String sql, Object[] args, Class<T> beanType) {
        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }
        Connection conn = this.prepareConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> result = null;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, args);
            rs = this.wrap(stmt.executeQuery());
            result = ResultSetUtils.handle(rs, beanType);

        } catch (Exception e) {
            this.rethrow(e, sql, args);
        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
                close(conn);
            }
        }

        return result;
    }


}
