package com.pualrdwade.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author PualrDwade
 * @apiNote 封装socketChanel
 */
public class Socket {

    public long socketId;

    public SocketChannel socketChannel = null;
    public IMessageReader messageReader = null;
    public MessageWriter messageWriter = null;

    public boolean endOfStreamReached = false;

    public Socket() {
    }

    public Socket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * 核心IO方法,从socket读取数据发送到缓冲区
     *
     * @param byteBuffer 字节缓冲区
     * @return
     * @throws IOException
     */
    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);//非阻塞读取socket,没有读到则返回-1;
        int totalBytesRead = bytesRead;
        //socket自旋
        while (bytesRead > 0) {
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if (bytesRead == -1) {
            this.endOfStreamReached = true;//打上结束标记
        }
        return totalBytesRead;
    }

    /**
     * 核心IO方法,从缓冲区读取数据写入socket
     *
     * @param byteBuffer
     * @return
     * @throws IOException
     */
    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWritten = this.socketChannel.write(byteBuffer); //非阻塞写入socket
        int totalBytesWritten = bytesWritten;
        //写入到socket了,并且缓冲区还存在数据
        while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
            //继续写入socket
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }
}
