package com.pualrdwade.nioserver;

import java.util.Queue;

/**
 * 写操作中介者
 * 
 * @author PualrDwade
 */
public class WriteProxy {

    private MessageBuffer messageBuffer = null;
    private Queue<Message> writeQueue = null;

    public WriteProxy(MessageBuffer messageBuffer, Queue<Message> writeQueue) {
        this.messageBuffer = messageBuffer;
        this.writeQueue = writeQueue;
    }

    public Message getMessage() {
        return this.messageBuffer.getMessage();
    }

    // 将message放入队列
    public boolean enqueue(Message message) {
        return this.writeQueue.offer(message);
    }

}
