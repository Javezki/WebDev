package com.lftech.sqlapi.database;

import com.lftech.sqlapi.pojo.ConnectionConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Slf4j
public class SimpleDataSource implements DataSource, AutoCloseable {

    private ConnectionConfig config;

    private HikariConfig pollConfig = new HikariConfig();
    private HikariDataSource ds;

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public SimpleDataSource(ConnectionConfig config){
        this.config = config;
        pollConfig.setJdbcUrl(config.getUrl());
        pollConfig.setUsername(config.getUsername());
        pollConfig.setPassword(config.getPassword());
        pollConfig.setDriverClassName(config.getDriverClassName());
        pollConfig.addDataSourceProperty("cachePrepStmts", "true");
        pollConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        pollConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource( pollConfig );
    }

    public <T> T execute(HandleCallback callback) {
        Connection connection = null;
        try {
            connection = getConnection();
            return (T) callback.apply(new DatabaseTemplate(connection));
        } catch (EmptyResultDataAccessException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConnectorException(e.getMessage(), e.getCause());
        } finally {
            DatabaseUtil.close(connection);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new ConnectorException("Unsupported method.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public void close() {
        ds.close();
    }
}
