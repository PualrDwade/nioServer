package com.pualrdwade.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author PualrDwade
 */
public interface IMessageReader {

    public void init(MessageBuffer readMessageBuffer);

    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

    public List<Message> getMessages();

}
