package com.pualrdwade.nioserver.bootstrap;

import com.pualrdwade.nioserver.Server;
import com.pualrdwade.nioserver.configure.ServerConfigure;
import com.pualrdwade.nioserver.http.HttpMessageProcessor;
import com.pualrdwade.nioserver.http.HttpMessageReaderFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author PualrDwade
 * @apiNote 服务器启动类, 分配端口
 * @date 2018-04-10
 */
public class ServerBootStrap {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 构造一个server,工厂方法模式,由于server需要分配不同的reader,为了DI原则所以注入一个工厂
        Server server = new Server(ServerConfigure.TCP_LISTEN_PORT, new HttpMessageReaderFactory(),
                new HttpMessageProcessor());
        server.start();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 启动客户端线程
        for (int i = 0; i < 1000; ++i) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    java.net.Socket socket;
                    socket = new java.net.Socket("localhost", ServerConfigure.TCP_LISTEN_PORT);
                    socket.getOutputStream().write("hello!myname is 陈志轩".getBytes());
                    byte[] bytes = new byte[1024 * 20];
                    socket.getInputStream().read(bytes);
                    System.out.println("客户端收到消息:" + new String(bytes));
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        System.out.println("高并发来袭....");
        Thread.sleep(2000);
        countDownLatch.countDown();
    }
}
