package com.pualrdwade.nioserver.bootstrap;

import com.pualrdwade.nioserver.NioServer;
import com.pualrdwade.nioserver.configure.ServerConfigure;
import com.pualrdwade.nioserver.http.HttpMessageProcessor;
import com.pualrdwade.nioserver.http.HttpMessageReaderFactory;

import java.io.IOException;

/**
 * @author PualrDwade
 * @apiNote 服务器启动类, 分配端口
 * @date 2018-04-10
 */
public class ServerBootStrap {

    public static void main(String[] args) throws IOException {
        // 构造一个server,工厂方法模式,由于server需要分配不同的reader,为了DI原则所以注入一个工厂
        NioServer nioServer = new NioServer(ServerConfigure.TCP_LISTEN_PORT, new HttpMessageReaderFactory(), new HttpMessageProcessor());
        nioServer.start();
    }
}
