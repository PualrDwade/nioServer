package com.pualrdwade.nioserver.configure;

/**
 * @author PualrDwade
 * @date 2018-04-10
 * @apiNote 服务器配置类
 */
public final class ServerConfigure {
    //Server服务器最大socket阻塞队列容量,控制服务器并发量与吞吐量
    public static final int SOCKET_QUERE_CAPACITY = 1024;
    public static final int TCP_LISTEN_PORT = 9999;
}
