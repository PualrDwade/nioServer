package com.pualrdwade.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by jjenkov on 19-10-2015. socket接受者,传入chanel管道
 */
public class SocketAccepter implements Runnable {

    private int tcpPort = 0;
    private ServerSocketChannel serverSocket = null;

    private Queue<Socket> socketQueue = null;

    public SocketAccepter(int tcpPort, Queue<Socket> socketQueue) {
        this.tcpPort = tcpPort;
        this.socketQueue = socketQueue;
    }

    public void run() {
        try {
            // 打开socket管道
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("Socket accepted: " + socketChannel);
                // 放入socket阻塞队列中,生产者-消费者
                // todo check if the queue can even accept more sockets.
                this.socketQueue.add(new Socket(socketChannel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
