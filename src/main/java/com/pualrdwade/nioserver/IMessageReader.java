package com.pualrdwade.nioserver;

import com.pualrdwade.nioserver.io.nio.NioSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by jjenkov on 16-10-2015.
 */
public interface IMessageReader {

    public void init(MessageBuffer readMessageBuffer);

    public void read(NioSocket nioSocket, ByteBuffer byteBuffer) throws IOException;

    public List<Message> getMessages();

}
