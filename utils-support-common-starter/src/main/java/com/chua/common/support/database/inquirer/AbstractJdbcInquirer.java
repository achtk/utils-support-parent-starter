package com.chua.common.support.database.inquirer;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;

/**
 * jdbc查询器
 *
 * @author CH
 */
@Slf4j
public abstract class AbstractJdbcInquirer implements SqlInquirer, Inquirer {
    protected DataSource dataSource;
    private final boolean isAutoCommit;
    private StatementConfiguration stmtConfig;
    private volatile boolean pmdKnownBroken = false;

    public AbstractJdbcInquirer(DataSource dataSource, boolean isAutoCommit, StatementConfiguration stmtConfig) {
        this(dataSource, isAutoCommit, stmtConfig, false);
    }

    public AbstractJdbcInquirer(DataSource dataSource, boolean isAutoCommit, StatementConfiguration stmtConfig, boolean pmdKnownBroken) {
        this.dataSource = dataSource;
        this.pmdKnownBroken = pmdKnownBroken;
        this.isAutoCommit = isAutoCommit;
        this.stmtConfig = stmtConfig;
    }


    /**
     * 准备声明
     *
     * @param conn 连接
     * @param sql  sql
     * @return 声明
     * @throws SQLException ex
     */
    protected CallableStatement prepareCall(Connection conn, String sql)
            throws SQLException {

        return conn.prepareCall(sql);
    }

    /**
     * 准备声明
     *
     * @param conn 连接
     * @param sql  sql
     * @return 声明
     * @throws SQLException ex
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql)
            throws SQLException {

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            ps = conn.prepareStatement(sql);
        }
        try {
            configureStatement(ps);
        } catch (SQLException e) {
            ps.close();
            throw e;
        }
        return ps;
    }

    /**
     * 异常信息
     *
     * @param cause  异常
     * @param sql    sql
     * @param params 参数
     * @throws SQLException e'x
     */
    protected void rethrow(Exception cause, String sql, Object... params)
            throws Exception {

        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer buffer = new StringBuffer(causeMessage);

        buffer.append(" Query: ");
        buffer.append(sql);
        buffer.append(" Parameters: ");

        if (params == null) {
            buffer.append("[]");
        } else {
            buffer.append(Arrays.deepToString(params));
        }

        throw cause;
    }

    /**
     * 结果集
     *
     * @param rs 结果
     * @return 结果集
     */
    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }

    /**
     * 填充声明
     *
     * @param stmt   声明
     * @param params 参数
     * @throws SQLException ex
     */
    public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("pmdKnownBroken: {}", pmdKnownBroken);
        }
        ParameterMetaData pmd = null;
        if (!pmdKnownBroken) {
            try {
                pmd = stmt.getParameterMetaData();
                if (pmd == null) {
                    pmdKnownBroken = true;
                } else {
                    int stmtCount = pmd.getParameterCount();
                    int paramsCount = params == null ? 0 : params.length;
                    if (stmtCount != paramsCount) {
                        throw new SQLException("Wrong number of parameters: expected "
                                + stmtCount + ", was given " + paramsCount);
                    }
                }
            } catch (SQLFeatureNotSupportedException ex) {
                pmdKnownBroken = true;
            }
        }

        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
                continue;
            }

            int sqlType = Types.VARCHAR;
            if (!pmdKnownBroken) {
                try {
                    sqlType = pmd.getParameterType(i + 1);
                } catch (SQLException e) {
                    pmdKnownBroken = true;
                }
            }
            stmt.setNull(i + 1, sqlType);
        }
    }

    /**
     * 声明设置
     *
     * @param stmt 声明
     * @throws SQLException ex
     */
    private void configureStatement(Statement stmt) throws SQLException {

        if (stmtConfig != null) {
            if (stmtConfig.isFetchDirectionSet()) {
                stmt.setFetchDirection(stmtConfig.getFetchDirection());
            }

            if (stmtConfig.isFetchSizeSet()) {
                stmt.setFetchSize(stmtConfig.getFetchSize());
            }

            if (stmtConfig.isMaxFieldSizeSet()) {
                stmt.setMaxFieldSize(stmtConfig.getMaxFieldSize());
            }

            if (stmtConfig.isMaxRowsSet()) {
                stmt.setMaxRows(stmtConfig.getMaxRows());
            }

            if (stmtConfig.isQueryTimeoutSet()) {
                stmt.setQueryTimeout(stmtConfig.getQueryTimeout());
            }
        }
    }

    /**
     * 获取连接
     *
     * @return 连接
     * @throws SQLException ex
     */
    protected Connection prepareConnection() throws SQLException {
        if (this.getDataSource() == null) {
            throw new SQLException(
                    "JdbcInquirer requires a DataSource to be "
                            + "invoked in this way, or a Connection should be passed in");
        }
        return this.getDataSource().getConnection();
    }

    /**
     * 数据源
     *
     * @return 数据源
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 关闭连接
     *
     * @param conn 连接
     * @throws SQLException ex
     */
    protected void close(Connection conn) throws SQLException {
        if (null == conn) {
            return;
        }
        conn.close();
    }

    /**
     * 关闭连接
     *
     * @param statement 连接
     * @throws SQLException ex
     */
    protected void close(Statement statement) throws SQLException {
        if (null == statement) {
            return;
        }

        statement.close();
    }

    /**
     * 关闭连接
     *
     * @param statement 连接
     * @throws SQLException ex
     */
    protected void close(PreparedStatement statement) throws SQLException {
        if (null == statement) {
            return;
        }

        statement.close();
    }

    /**
     * 关闭连接
     *
     * @param rs 连接
     * @throws SQLException ex
     */
    protected void close(ResultSet rs) throws SQLException {
        if (null == rs) {
            return;
        }

        rs.close();
    }

    public void close() throws Exception {
        if (dataSource instanceof AutoCloseable) {
            ((AutoCloseable) dataSource).close();
        }
    }

}
