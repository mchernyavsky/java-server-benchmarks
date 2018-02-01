package com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.SortUtils;
import com.github.mchernyavsky.javaserverbenchmarks.StatService;
import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractTestServer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TcpNonBlockingFixedPoolTestServer extends AbstractTestServer {
    @NotNull
    private final Selector selector = Selector.open();
    @NotNull
    private final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    @NotNull
    private final Thread listener = new Thread(new ConnectionsListener());
    @NotNull
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public TcpNonBlockingFixedPoolTestServer(final int port) throws IOException {
        super();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        listener.start();
    }

    public void close() throws IOException {
        super.close();
        listener.interrupt();
        executor.shutdownNow();
        serverSocketChannel.close();
        selector.close();
    }

    private void processSelectedSet(@NotNull final Set selectedKeys) throws IOException {
        val keyIterator = selectedKeys.iterator();
        while (keyIterator.hasNext()) {
            val key = (SelectionKey) keyIterator.next();
            keyIterator.remove();

            if (key.isAcceptable()) {
                val socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(key.selector(), SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                val socketChannel = (SocketChannel) key.channel();
                val startClientProcessing = System.currentTimeMillis();
                val request = processRead(socketChannel);
                executor.submit(() -> {
                    try {
                        val data = processRequest(request);
                        processWrite(socketChannel, data);
                    } catch (IOException exception) {
                        log.trace("Caught IOException");
                        log.error(exception.getMessage());
                        return;
                    }

                    val finishClientProcessing = System.currentTimeMillis();
                    StatService.getInstance().addClientProcessingTime(
                            finishClientProcessing - startClientProcessing);
                });
            }
        }
    }

    @NotNull
    private static Message processRead(@NotNull final SocketChannel socketChannel)
            throws IOException {
        val sizeBuffer = ByteBuffer.allocate(Integer.BYTES);
        while (sizeBuffer.hasRemaining()) {
            if (socketChannel.read(sizeBuffer) == -1) {
                socketChannel.close();
                throw new SocketException("Socket was closed");
            }
        }

        sizeBuffer.flip();
        val size = sizeBuffer.getInt();

        val messageBuffer = ByteBuffer.allocate(size);
        while (messageBuffer.hasRemaining()) {
            if (socketChannel.read(messageBuffer) == -1) {
                socketChannel.close();
                throw new SocketException("Socket was closed");
            }
        }

        messageBuffer.flip();
        return Message.parseFrom(messageBuffer.array());
    }

    @NotNull
    private static List<Integer> processRequest(@NotNull final Message request) {
        val startRequestProcessing = System.currentTimeMillis();
        val data = new ArrayList<Integer>(request.getDataList());
        SortUtils.selectionSort(data);
        val finishRequestProcessing = System.currentTimeMillis();
        StatService.getInstance().addRequestProcessingTime(
                finishRequestProcessing - startRequestProcessing);
        return data;
    }

    private static void processWrite(@NotNull final SocketChannel socketChannel,
                                     @NotNull final List<Integer> data) throws IOException {
        val responseBuilder = Message.newBuilder();
        data.forEach(responseBuilder::addData);
        val response = responseBuilder.build();

        val arrayStream = new ByteArrayOutputStream();
        val output = new DataOutputStream(arrayStream);
        output.writeInt(response.getSerializedSize());
        response.writeTo(output);

        val buffer = ByteBuffer.wrap(arrayStream.toByteArray());
        while (buffer.hasRemaining()) {
            if (socketChannel.write(buffer) == -1) {
                socketChannel.close();
                throw new SocketException();
            }
        }
    }

    private class ConnectionsListener implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    if (selector.select() <= 0) {
                        continue;
                    }
                    processSelectedSet(selector.selectedKeys());
                } catch (SocketException ignored) {
                    log.trace("Caught SocketException");
                } catch (IOException exception) {
                    log.error(exception.getMessage());
                }
            }
        }
    }
}
