package com.lftech.sqlapi.database;

import com.lftech.sqlapi.pojo.ConnectionConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectorFactory implements DisposableBean {

    private final Map<String, SimpleDataSource> connectorCache = new ConcurrentHashMap<>();

    @Override
    public void destroy() {
        connectorCache.values().forEach(this::disconnect);
        connectorCache.clear();
    }

    /**
     * 建立连接，返回缓存连接对象
     *
     * @param config
     */
    public DataSource connect(ConnectionConfig config) {
        Assert.notNull(config, "ConnectorConfig can not be null.");
        String cacheKey = config.getCacheKey();
        if (!connectorCache.containsKey(cacheKey)) {
            synchronized (connectorCache) {
                if (!connectorCache.containsKey(cacheKey)) {
                    connectorCache.putIfAbsent(cacheKey, new SimpleDataSource(config));
                }
            }
        }
        return connectorCache.get(cacheKey);
    }

    private void disconnect(SimpleDataSource dataSource) {
        Assert.notNull(dataSource, "ConnectorMapper can not be null.");
        dataSource.close();
    }
}