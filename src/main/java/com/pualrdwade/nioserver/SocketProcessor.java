package com.pualrdwade.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * @author PualrDwade
 * @apiNote 核心处理类, 作为消费者, 消费socket队列中
 */
public class SocketProcessor implements Runnable {

    private Queue<NioSocket> inboundNioSocketQueue = null;

    private MessageBuffer readMessageBuffer = null; // todo Not used now - but perhaps will be later - to check for
    // space in the buffer before reading from sockets
    private MessageBuffer writeMessageBuffer = null; // todo Not used now - but perhaps will be later - to check for
    // space in the buffer before reading from sockets (space for more
    // to write?)

    private IMessageReaderFactory messageReaderFactory = null;

    private Queue<Message> outboundMessageQueue = new LinkedList<>(); // todo use a better / faster queue.

    private Map<Long, NioSocket> socketMap = new HashMap<>();

    private ByteBuffer readByteBuffer = ByteBuffer.allocate(1024 * 1024);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocate(1024 * 1024);
    private Selector readSelector = null;
    private Selector writeSelector = null;

    private IMessageProcessor messageProcessor = null;
    private WriteProxy writeProxy = null;

    private long nextSocketId = 16 * 1024; // start incoming socket ids from 16K - reserve bottom ids for pre-defined
    // sockets (servers).

    private Set<NioSocket> emptyToNonEmptyNioSockets = new HashSet<>();
    private Set<NioSocket> nonEmptyToEmptyNioSockets = new HashSet<>();

    public SocketProcessor(Queue<NioSocket> inboundNioSocketQueue, MessageBuffer readMessageBuffer,
                           MessageBuffer writeMessageBuffer, IMessageReaderFactory messageReaderFactory,
                           IMessageProcessor messageProcessor) throws IOException {
        this.inboundNioSocketQueue = inboundNioSocketQueue;

        this.readMessageBuffer = readMessageBuffer;
        this.writeMessageBuffer = writeMessageBuffer;
        this.writeProxy = new WriteProxy(writeMessageBuffer, this.outboundMessageQueue);

        this.messageReaderFactory = messageReaderFactory;

        this.messageProcessor = messageProcessor;

        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
    }

    public void run() {
        while (true) {
            try {
                executeCycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void executeCycle() throws IOException {
        takeNewSockets();
        readFromSockets();
        writeToSockets();
    }

    public void takeNewSockets() throws IOException {
        NioSocket newNioSocket = this.inboundNioSocketQueue.poll();

        while (newNioSocket != null) {
            newNioSocket.socketId = this.nextSocketId++;
            newNioSocket.socketChannel.configureBlocking(false);

            newNioSocket.messageReader = this.messageReaderFactory.createMessageReader();
            newNioSocket.messageReader.init(this.readMessageBuffer);

            newNioSocket.messageWriter = new MessageWriter();

            this.socketMap.put(newNioSocket.socketId, newNioSocket);

            SelectionKey key = newNioSocket.socketChannel.register(this.readSelector, SelectionKey.OP_READ);
            key.attach(newNioSocket);

            newNioSocket = this.inboundNioSocketQueue.poll();
        }
    }

    public void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0) {
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                readFromSocket(key);

                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        NioSocket nioSocket = (NioSocket) key.attachment();
        nioSocket.messageReader.read(nioSocket, this.readByteBuffer);

        List<Message> fullMessages = nioSocket.messageReader.getMessages();
        if (fullMessages.size() > 0) {
            for (Message message : fullMessages) {
                message.socketId = nioSocket.socketId;
                this.messageProcessor.process(message, this.writeProxy); // the message processor will eventually push
                // outgoing messages into an IMessageWriter for
                // this socket.
            }
            fullMessages.clear();
        }

        if (nioSocket.endOfStreamReached) {
            System.out.println("Socket closed: " + nioSocket.socketId);
            this.socketMap.remove(nioSocket.socketId);
            key.attach(null);
            key.cancel();
            key.channel().close();
        }
    }

    public void writeToSockets() throws IOException {

        // Take all new messages from outboundMessageQueue
        takeNewOutboundMessages();

        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        registerNonEmptySockets();

        // Select from the Selector.
        int writeReady = this.writeSelector.selectNow();

        if (writeReady > 0) {
            Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                NioSocket nioSocket = (NioSocket) key.attachment();

                nioSocket.messageWriter.write(nioSocket, this.writeByteBuffer);

                if (nioSocket.messageWriter.isEmpty()) {
                    this.nonEmptyToEmptyNioSockets.add(nioSocket);
                }

                keyIterator.remove();
            }

            selectionKeys.clear();

        }
    }

    private void registerNonEmptySockets() throws ClosedChannelException {
        for (NioSocket nioSocket : emptyToNonEmptyNioSockets) {
            nioSocket.socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE, nioSocket);
        }
        emptyToNonEmptyNioSockets.clear();
    }

    private void cancelEmptySockets() {
        for (NioSocket nioSocket : nonEmptyToEmptyNioSockets) {
            SelectionKey key = nioSocket.socketChannel.keyFor(this.writeSelector);

            key.cancel();
        }
        nonEmptyToEmptyNioSockets.clear();
    }

    private void takeNewOutboundMessages() {
        Message outMessage = this.outboundMessageQueue.poll();
        while (outMessage != null) {
            NioSocket nioSocket = this.socketMap.get(outMessage.socketId);

            if (nioSocket != null) {
                MessageWriter messageWriter = nioSocket.messageWriter;
                if (messageWriter.isEmpty()) {
                    messageWriter.enqueue(outMessage);
                    nonEmptyToEmptyNioSockets.remove(nioSocket);
                    emptyToNonEmptyNioSockets.add(nioSocket); // not necessary if removed from nonEmptyToEmptySockets in prev.
                    // statement.
                } else {
                    messageWriter.enqueue(outMessage);
                }
            }

            outMessage = this.outboundMessageQueue.poll();
        }
    }

}
