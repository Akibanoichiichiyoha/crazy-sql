package com.crazy.sql.core.pool.impl;

import com.crazy.sql.core.config.CrazySQLConfig;
import com.crazy.sql.core.exception.ConnectionPoolBusyException;
import com.crazy.sql.core.jdbc.AutoCallBackConnection;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 标准的连接池
 */
public class SimpleConnectionPool extends ConnectionPool {
    private static Logger logger= LoggerFactory.getLogger(SimpleConnectionPool.class);
    protected String driverClassName;
    protected String username;
    protected String url;
    protected String password;
    private final Properties properties=new Properties();
    protected ConnectionUtils connectionUtils;
    private boolean isInit=false;

    public SimpleConnectionPool() {
    }

    public SimpleConnectionPool(Properties properties) {
        logger.info("The connection pool starts to be initialized. The configuration information is as follows:"+properties);
        this.driverClassName =properties.getProperty("driverClassName");
        this.username=properties.getProperty("username");
        this.url=properties.getProperty("url");
        this.password=properties.getProperty("password");
        this.connectionUtils=new ConnectionUtils(properties,this);
        try {
            Class.forName(properties.getProperty("driverClassName"));
            for (int i=0;i<maximum;i++)
                pool.add(connectionUtils.establishConnection());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        isInit=true;
    }
    public SimpleConnectionPool(String driverClassName, String userName, String url, String password) {

        this.maximum= CrazySQLConfig.getInstance().getMaximum();
        logger.info("The connection pool starts to be initialized. The configuration information is as follows:"+getProperties()+"\nmaximum:"+maximum);
        this.connectionUtils=new ConnectionUtils(getProperties(),this);
        try {
            Class.forName(driverClassName);
            for (int i=0;i<maximum;i++)
                pool.add(connectionUtils.establishConnection());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        isInit=true;
        logger.info("pool size:"+pool.size());
    }
    private synchronized void init(){

        logger.info("The connection pool starts to be initialized. The configuration information is as follows:"+getProperties()+"maximum:"+maximum);
        this.connectionUtils=new ConnectionUtils(getProperties(),this);
        maximum=CrazySQLConfig.getInstance().getMaximum();
        try {
            Class.forName(driverClassName);
            for (int i=0;i<maximum;i++)
                pool.add(connectionUtils.establishConnection());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        isInit=true;
        logger.info("pool size:"+pool.size());
    }
    @Override
    public synchronized Connection getConnection() throws ConnectionPoolBusyException {
        if(!isInit){
            init();
        }
        int count=0;
        while (pool.size() == 0) {
            try {
                if(count>100)
                    throw new ConnectionPoolBusyException();
                Thread.sleep(10);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Connection connection=pool.remove(pool.size()-1);
        logger.info("A connection was obtained from the connection pool:"+connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("not support method!");
    }

    @Override
    public synchronized void callBack(Connection connection){
        if (!isPoolFill(1)){
            logger.info("Returned a connection to the connection pool:"+connection);
            if(connection instanceof AutoCallBackConnection)
                pool.add(connection);
            else
                pool.add(new AutoCallBackConnection(connection,this));
        }
        logger.info("pool size:"+pool.size());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("not support method!");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("not support method!");
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionUtils getConnectionUtils() {
        return connectionUtils;
    }

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }
    public Properties getProperties(){
        properties.setProperty("driverClassName", driverClassName);
        properties.setProperty("username",username);
        properties.setProperty("url",url);
        properties.setProperty("password",password);
        return properties;
    }

}
