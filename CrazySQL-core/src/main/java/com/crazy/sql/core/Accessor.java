package com.crazy.sql.core;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.exception.ConnectionPoolBusyException;
import com.crazy.sql.core.exception.SQLExecutorBuildException;
import com.crazy.sql.core.executor.SQLExecutor;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.SQLUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 聚合的实体类，代理了SimpleSQLExecutor，用于实现数据库操作功能
 * @param <T>
 */
public class Accessor<T> implements SQLExecutor<T> {
    private SimpleSQLExecutor<T> executor;
    private DataSource pool;
    private SQLUtils<T> sqlUtils;
    private CacheManager cacheManager;


    public SimpleSQLExecutor<T> getExecutor() {
        if(sqlUtils==null){
            throw new SQLExecutorBuildException("not set SQLUtils!");
        }else if(pool==null){
            throw new SQLExecutorBuildException("doesn't have connectionPool");
        }
        try {
            executor.setConnection(pool.getConnection());
            executor.setSQLUtils(sqlUtils);
            executor.setCacheManager(cacheManager);
        } catch (ConnectionPoolBusyException | SQLException e) {
            e.printStackTrace();
        }
        return executor;
    }

    public void setExecutor(SimpleSQLExecutor<T> executor) {
        this.executor = executor;
    }
    public DataSource getPool() {
        return pool;
    }

    public void setPool(DataSource pool) {
        this.pool = pool;
    }

    public SQLUtils<T> getSqlUtils() {
        return sqlUtils;
    }

    public void setSqlUtils(SQLUtils<T> sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    @Override
    public int insert(T t) throws SQLException {
        return getExecutor().insert(t);
    }

    @Override
    public int update(T t) throws SQLException {
        return getExecutor().update(t);
    }

    @Override
    public int delete(T t) throws SQLException {
        return getExecutor().delete(t);
    }

    @Override
    public T queryOne(T t) throws SQLException {
        return getExecutor().queryOne(t);
    }

    @Override
    public List<T> queryAll() throws SQLException {
        return getExecutor().queryAll();
    }
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        return getExecutor().queryByWords(queryWords);
    }
}
