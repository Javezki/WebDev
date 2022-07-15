package com.lftech.sqlapi.database;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public abstract class DatabaseUtil {

    private DatabaseUtil() {
    }

    public static Connection getConnection(String driverClassName, String url, String username, String password) throws SQLException {
        if (StringUtils.isNotBlank(driverClassName)) {
            try {
                Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new ConnectorException(e.getCause());
            }
        }
        Connection conn = DriverManager.getConnection(url, username, password);
        log.info("数据连接建立{}->{}", conn.getClass(), conn.getMetaData().getURL());
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(AutoCloseable rs) {
        if (null != rs) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}