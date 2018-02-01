package com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.SortUtils;
import com.github.mchernyavsky.javaserverbenchmarks.StatService;
import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractTestServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TcpAsyncTestServer extends AbstractTestServer {
    @NotNull
    private final AsynchronousServerSocketChannel serverSocketChannel =
            AsynchronousServerSocketChannel.open();

    public TcpAsyncTestServer(final int port) throws IOException {
        super();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.accept(null, new AcceptHandler());
    }

    public void close() throws IOException {
        super.close();
        serverSocketChannel.close();
    }

    private void handleCompletedException(@NotNull final AsynchronousSocketChannel channel) {
        try {
            channel.close();
            log.trace("Stopped listening to the clientSocketChannel %s",
                    channel.getRemoteAddress());
        } catch (IOException exception) {
            log.trace("Caught IOException");
            log.error(exception.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    private class Attachment {
        private AsynchronousSocketChannel clientSocketChannel;
        private ByteBuffer buffer;
        private boolean isReadingSize;
        private long startClientProcessing;
    }

    private class AcceptHandler implements
            CompletionHandler<AsynchronousSocketChannel, Attachment> {
        @Override
        public void completed(@NotNull final AsynchronousSocketChannel clientSocketChannel,
                              @Nullable final Attachment ignore) {
            receiveData(clientSocketChannel);
        }

        @Override
        public void failed(@NotNull final Throwable exception,
                           @Nullable final Attachment attachment) {
            if (exception instanceof ClosedChannelException) {
                return;
            }

            log.error(exception.getMessage());
        }

        private void receiveData(@NotNull final AsynchronousSocketChannel clientSocketChannel) {
            val attachment = new Attachment(
                    clientSocketChannel,
                    ByteBuffer.allocate(Integer.BYTES),
                    true,
                    System.currentTimeMillis()
            );
            clientSocketChannel.read(attachment.buffer, attachment, new ReadHandler());
            serverSocketChannel.accept(null, this);
        }
    }

    private class ReadHandler implements CompletionHandler<Integer, Attachment> {

        @Override
        public void completed(@NotNull final Integer result, @NotNull final Attachment attachment) {
            if (result == -1) {
                handleCompletedException(attachment.clientSocketChannel);
                return;
            }

            if (attachment.buffer.hasRemaining()) {
                attachment.clientSocketChannel.read(attachment.buffer, attachment, this);
                return;
            }

            attachment.buffer.flip();
            if (attachment.isReadingSize) {
                val size = parseSize(attachment);
                attachment.buffer = ByteBuffer.allocate(size);
                attachment.clientSocketChannel.read(attachment.buffer, attachment, this);
            } else {
                try {
                    val data = parseData(attachment);
                    val sortedData = processData(data);
                    sendData(sortedData, attachment);
                } catch (IOException exception) {
                    log.trace("Caught IOException");
                    log.error(exception.getMessage());
                }
            }
        }

        @Override
        public void failed(@NotNull final Throwable exception,
                           @NotNull final Attachment attachment) {
            if (exception instanceof ClosedChannelException) {
                return;
            }

            log.error(exception.getMessage());
        }

        private int parseSize(@NotNull final Attachment attachment) {
            attachment.isReadingSize = false;
            return attachment.buffer.getInt();
        }

        @NotNull
        private List<Integer> parseData(@NotNull final Attachment attachment) throws IOException {
            val request = Message.parseFrom(attachment.buffer.array());
            return request.getDataList();
        }

        @NotNull
        private List<Integer> processData(@NotNull final List<Integer> data) {
            val startRequestProcessing = System.currentTimeMillis();
            val sortedData = new ArrayList<Integer>(data);
            SortUtils.selectionSort(sortedData);
            val finishRequestProcessing = System.currentTimeMillis();
            StatService.getInstance().addRequestProcessingTime(
                    finishRequestProcessing - startRequestProcessing);
            return sortedData;
        }

        private void sendData(@NotNull final List<Integer> data,
                              @NotNull final Attachment attachment) {
            val responseBuilder = Message.newBuilder();
            data.forEach(responseBuilder::addData);
            val response = responseBuilder.build();

            attachment.setBuffer(ByteBuffer.allocate(Integer.BYTES + response.getSerializedSize()));
            attachment.buffer.putInt(response.getSerializedSize());
            attachment.buffer.put(response.toByteArray());
            attachment.buffer.flip();

            attachment.clientSocketChannel.write(attachment.buffer, attachment, new WriteHandler());
        }
    }

    private class WriteHandler implements CompletionHandler<Integer, Attachment> {

        @Override
        public void completed(@NotNull final Integer result, @NotNull final Attachment attachment) {
            if (result == -1) {
                handleCompletedException(attachment.clientSocketChannel);
                return;
            }

            if (attachment.buffer.hasRemaining()) {
                attachment.clientSocketChannel.write(attachment.buffer, attachment, this);
                return;
            }

            val finishClientProcessing = System.currentTimeMillis();
            StatService.getInstance().addClientProcessingTime(
                    finishClientProcessing - attachment.startClientProcessing);
        }

        @Override
        public void failed(@NotNull final Throwable exception,
                           @NotNull final Attachment attachment) {
            if (exception instanceof ClosedChannelException) {
                return;
            }

            log.error(exception.getMessage());
        }
    }
}
