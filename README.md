# 基于非阻塞IO模型的http服务器
## NIO
NIO在java中是new IO的意思,也有非阻塞IO的含义,NIO是针对传统的阻塞IO(BIO)而言的,传统的IO流操作的读写方法都是阻塞的,而NIO模块提出了更复杂的模块:通道,选择器…提供高性能IO,基于事件的Reactor模型使得再IO密集型网络应用中即使单线程环境也可以得到极强的处理能力,减少了线程开销。

NIO 实现了 IO 多路复用中的 Reactor 模型，一个线程 Thread 使用一个选择器 Selector 通过轮询的方式去监听多个通道 Channel 上的事件，从而让一个线程就可以处理多个事件。

通过配置监听的通道 Channel 为非阻塞，那么当 Channel 上的 IO 事件还未到达时，就不会进入阻塞状态一直等待，而是继续轮询其它 Channel，找到 IO 事件已经到达的 Channel 执行。

因为创建和切换线程的开销很大，因此使用一个线程来处理多个事件而不是一个线程处理一个事件，对于 IO 密集型的应用具有很好地性能。

![](https://i.loli.net/2019/03/17/5c8dbffd9d000.png)
> 图片参考自CSNOTE作者

## 架构分析
```
'|-- IMessageProcessor.java', ---处理者接口,自定义拓展
'|-- IMessageReader.java',
'|-- IMessageReaderFactory.java', --- 工厂方法模式
'|-- Message.java', --- IO封装
'|-- MessageBuffer.java',
'|-- MessageWriter.java',
'|-- QueueIntFlip.java',
'|-- Server.java', ---服务器类,线程级别管理
'|-- Socket.java',
'|-- SocketAccepter.java', ---socket接受类,生产者
'|-- SocketProcessor.java', ---socket处理者,消费者
'|-- WriteProxy.java', ---中介者模式,控制对IO操作的处理
'|-- bootstrap',
'|   |-- ServerBootStrap.java', ---服务器启动类
'|-- configure',
'|   |-- ServerConfigure.java', ---服务器配置类
'|-- http',---http协议的简单实现
'    |-- HttpHeaders.java',
'    |-- HttpMessageProcessor.java',
'    |-- HttpMessageReader.java',
'    |-- HttpMessageReaderFactory.java',
'    |-- HttpUtil.java',
```
