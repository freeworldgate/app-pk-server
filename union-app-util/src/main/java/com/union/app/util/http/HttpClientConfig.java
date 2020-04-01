package com.union.app.util.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class HttpClientConfig {


    /**
     * 连接池;
     */

    @Value("${http.client.socketTimeOut}")
    private int socketTimeout;

    @Value("${http.client.connectTimeout}")
    private int connectTimeout;

    @Value("${http.client.poolSize}")
    private int poolSize;

    @Value("${http.client.maxPerRoute}")
    private int maxPerRoute;



    /*代理*/

    @Value("${http.client.host}")
    private String host;

    @Value("${http.client.port}")
    private int port;

    @Value("${http.client.username}")
    private String username;

    @Value("${http.client.password}")
    private String password;




    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }



    public int getSocketTimeout()
    {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }


    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
