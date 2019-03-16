package com.pualrdwade.nioserver.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

/**
 * @author PualrDwade
 * @apiNote socket接受者, 传入chanel管道
 */
public class NioSocketAccepter implements Runnable {

    private int tcpPort = 0;
    private ServerSocketChannel serverSocket = null;

    private BlockingQueue<NioSocket> nioSocketQueue = null;

    public NioSocketAccepter(int tcpPort, BlockingQueue<NioSocket> nioSocketQueue) {
        this.tcpPort = tcpPort;
        this.nioSocketQueue = nioSocketQueue;
    }

    public void run() {
        try {
            // 打开serverSocket管道,绑定端口
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                // 暂时阻塞知道得到一个管道连接//这里的阻塞是必要的,因为不会产生任何影响
                // 非阻塞是强调IO,而不是接受socket
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("Socket accepted: " + socketChannel);
                // 放入socket阻塞队列中,生产者-消费者
                //todo 检查队列是否可以放入更多的socket,目前直接使用阻塞队列进行阻塞
                try {
                    this.nioSocketQueue.put(new NioSocket(socketChannel));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
