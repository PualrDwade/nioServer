package com.pualrdwade.nioserver.example;

import com.pualrdwade.nioserver.Server;
import com.pualrdwade.nioserver.http.HttpMessageProcessor;
import com.pualrdwade.nioserver.http.HttpMessageReaderFactory;

import java.io.IOException;

/**
 * Created by jjenkov on 19-10-2015.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        // 构造一个server,工厂方法模式,由于server需要分配不同的reader,为了DI原则所以注入一个工厂
        Server server = new Server(9999, new HttpMessageReaderFactory(), new HttpMessageProcessor());
        server.start();
    }
}
