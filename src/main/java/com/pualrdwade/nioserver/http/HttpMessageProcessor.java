package com.pualrdwade.nioserver.http;

import com.pualrdwade.nioserver.IMessageProcessor;
import com.pualrdwade.nioserver.Message;
import com.pualrdwade.nioserver.WriteProxy;

import java.nio.charset.StandardCharsets;

/**
 * @author PualrDwade
 * @date 2018-07-10
 * @apiNote MessageProcessor的Http协议实现
 */
public class HttpMessageProcessor implements IMessageProcessor {

    /**
     * 获得到write代理之后对消息进行处理
     *
     * @param message
     * @param writeProxy
     */
    @Override
    public void process(Message request, WriteProxy writeProxy) {

        String httpResponse = "HTTP/1.1 200 OK\r\n" + "Content-Length: 38\r\n" + "Content-Type: text/html\r\n" + "\r\n"
                + "<html><body>Hello World!</body></html>";
        byte[] httpResponseBytes = httpResponse.getBytes(StandardCharsets.UTF_8);
        System.out.println("Message Received from socket: " + request.socketId + "--reported by" + this);
        System.out.println("message content" + request.metaData);
        Message response = writeProxy.getMessage();
        response.socketId = request.socketId;
        response.writeToMessage(httpResponseBytes);
        writeProxy.enqueue(response);

    }
}
