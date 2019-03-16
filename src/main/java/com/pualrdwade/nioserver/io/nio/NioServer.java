package com.pualrdwade.nioserver.io.nio;

import com.pualrdwade.nioserver.IMessageProcessor;
import com.pualrdwade.nioserver.IMessageReaderFactory;
import com.pualrdwade.nioserver.MessageBuffer;
import com.pualrdwade.nioserver.configure.ServerConfigure;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author PualrDWade
 * @date 2018-04-10
 * @apiNote 核心服务类, 管理工作线程与队列, 加载配置
 */
public class NioServer {

    private NioSocketAccepter nioSocketAccepter = null;
    private NioSocketProcessor nioSocketProcessor = null;
    private int tcpPort = 0;
    private IMessageReaderFactory messageReaderFactory = null;
    private IMessageProcessor messageProcessor = null;

    /**
     * @param tcpPort              监听端口
     * @param messageReaderFactory 工厂方法模式,生产得到messageReader,实现DI
     * @param messageProcessor     对Message进行处理
     */
    public NioServer(int tcpPort, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) {
        this.tcpPort = tcpPort;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException {
        System.out.println("服务器开始启动...");
        // 创建socket队列
        System.out.println("初始化服务器配置...");
        //创建Socket阻塞队列,使用生产者/消费者模型共享阻塞队列
        BlockingQueue<NioSocket> nioSocketQueue = new ArrayBlockingQueue<>(ServerConfigure.SOCKET_QUERE_CAPACITY);
        //初始化
        this.nioSocketAccepter = new NioSocketAccepter(tcpPort, nioSocketQueue);
        MessageBuffer readBuffer = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();
        this.nioSocketProcessor = new NioSocketProcessor(nioSocketQueue, readBuffer, writeBuffer, this.messageReaderFactory,
                this.messageProcessor);

        // 单一线程,非阻塞IO
        Thread accepterThread = new Thread(this.nioSocketAccepter);
        Thread processorThread = new Thread(this.nioSocketProcessor);
        System.out.println("启动socketAccepter工作线程");
        // 启动接受者
        accepterThread.start();
        System.out.println("启动socketProcessor工作线程");
        // 启动处理者线程
        processorThread.start();
        System.out.println("服务器已经启动,监听端口:" + tcpPort);
    }

}
