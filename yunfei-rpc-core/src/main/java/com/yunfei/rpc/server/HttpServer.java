package com.yunfei.rpc.server;

public interface HttpServer {
    /**
     * 启动服务器
     * @param port 端口
     */
    void doStart(int port);
}
