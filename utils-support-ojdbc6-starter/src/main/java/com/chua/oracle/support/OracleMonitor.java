package com.chua.oracle.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.monitor.AbstractMonitor;
import com.chua.common.support.monitor.NotifyType;
import com.chua.common.support.monitor.session.ObjectSession;
import com.chua.common.support.monitor.session.SessionNotifyMessage;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.NetAddress;
import lombok.SneakyThrows;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.dcn.*;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

/**
 * oracle
 *
 * @author CH
 */
@Spi("oracle")
public class OracleMonitor extends AbstractMonitor implements DatabaseChangeListener {

    DatabaseChangeRegistration databaseChangeRegistration;
    private OracleConnection conn;

    @Override
    public void preStart() {
        OracleDataSource dataSource = buildOracleDataSource();
        databaseChangeRegistration = buildDatabaseChangeRegistration(dataSource);
    }

    @SneakyThrows
    private DatabaseChangeRegistration buildDatabaseChangeRegistration(OracleDataSource dataSource) {
        this.conn = (OracleConnection) dataSource.getConnection();
        Properties prop = new Properties();
        // 要取得更改记录的rowid
        prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
        prop.setProperty(OracleConnection.NTF_QOS_PURGE_ON_NTFN, "true");
        // 设置超时，这里是1个小时，届时数据库和驱动器的资源自动释放。如果为0或不设置，则用不过期，直到程序停止监听，当数据库发送更新通知时，因为没有监听端口，数据库随后释放资源
        prop.setProperty(OracleConnection.NTF_TIMEOUT, "0");

        try {
            this.databaseChangeRegistration = conn.registerDatabaseChangeNotification(prop);
            databaseChangeRegistration.addListener(this);
            return databaseChangeRegistration;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private OracleDataSource buildOracleDataSource() {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(configuration.username());
        dataSource.setPassword(configuration.password());
        NetAddress address = NetAddress.of(configuration.url());
        dataSource.setURL("jdbc:oracle:thin:@" + address.getAddress() + ":" + configuration.database());
        return dataSource;
    }

    @Override
    public void afterStart() {

    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        IoUtils.closeQuietly(conn);
    }

    @Override
    public void onDatabaseChangeNotification(DatabaseChangeEvent databaseChangeEvent) {
        TableChangeDescription[] tds = databaseChangeEvent.getTableChangeDescription();
        long regId = databaseChangeEvent.getRegId();
        System.out.println("=============================" + new Date() + "=============================");
        if (regId == databaseChangeRegistration.getRegId()) {
            System.out.println("'TableChangeDescription'(数据表的变化次数):" + tds.length);
            for (TableChangeDescription td : tds) {
                RowChangeDescription[] rowChangeDescription = td.getRowChangeDescription();
                for (RowChangeDescription changeDescription : rowChangeDescription) {
                    SessionNotifyMessage sessionMessage = null;
                    if (changeDescription.getRowOperation() == RowChangeDescription.RowOperation.INSERT) {
                        sessionMessage = new SessionNotifyMessage();
                        sessionMessage.setType(NotifyType.CREATE);
                    } else if (changeDescription.getRowOperation() == RowChangeDescription.RowOperation.UPDATE) {
                        sessionMessage = new SessionNotifyMessage();
                        sessionMessage.setType(NotifyType.MODIFY);
                    } else {
                        sessionMessage = new SessionNotifyMessage();
                        sessionMessage.setType(NotifyType.DELETE);
                    }
                    sessionMessage.setSession(new ObjectSession(td));
                    sessionMessage.setMessage(changeDescription.getRowid().stringValue());
                    notifyMessage(sessionMessage);
                }
            }
        }
    }
}
