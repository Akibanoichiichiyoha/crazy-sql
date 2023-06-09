package com.crazy.sql.core.executor;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.utils.SQLUtils;

import java.sql.Connection;

public interface SimpleSQLExecutor<T> extends SQLExecutor<T>{
    /**
     * 设置数据库连接
     * @param connection
     */
    public void setConnection(Connection connection);

    /**
     * 获取数据库连接
     * @return
     */
    public Connection getConnection();
    /**
     * 设置SQLUtils用于操作数据库
     * @param sqlUtils
     */
    public void setSQLUtils(SQLUtils<T> sqlUtils);

    /**
     * 设置缓存管理器
     * @param cacheManager
     */
    public void setCacheManager(CacheManager cacheManager);
    public CacheManager getCacheManager();
}
