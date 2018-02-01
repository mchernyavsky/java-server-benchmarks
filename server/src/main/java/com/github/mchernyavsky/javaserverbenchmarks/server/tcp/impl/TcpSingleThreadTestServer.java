package com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractTestServer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

@Slf4j
public class TcpSingleThreadTestServer extends AbstractTestServer {
    @NotNull
    private final ServerSocket serverSocket;
    @NotNull
    private final Thread listener = new Thread(new ConnectionsListener());

    public TcpSingleThreadTestServer(final int port) throws IOException {
        super();
        serverSocket = new ServerSocket(port);
        listener.start();
    }

    public void close() throws IOException {
        super.close();
        serverSocket.close();
        listener.interrupt();
    }

    private class ConnectionsListener implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    val socket = serverSocket.accept();
                    val handler = new TcpSyncConnectionHandler(socket);
                    handler.run();
                } catch (SocketException ignored) {
                    log.trace("Caught SocketException");
                } catch (IOException exception) {
                    log.trace("Caught IOException");
                    log.error(exception.getMessage());
                }
            }
        }
    }
}
