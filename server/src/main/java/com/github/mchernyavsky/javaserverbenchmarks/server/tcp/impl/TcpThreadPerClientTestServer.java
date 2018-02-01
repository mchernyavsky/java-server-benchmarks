package com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractTestServer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TcpThreadPerClientTestServer extends AbstractTestServer {
    @NotNull
    private final ServerSocket serverSocket;
    @NotNull
    private final Thread listener = new Thread(new ConnectionsListener());
    @NotNull
    private final List<Thread> handlers = new ArrayList<>();

    public TcpThreadPerClientTestServer(final int port) throws IOException {
        super();
        serverSocket = new ServerSocket(port);
        listener.start();
    }

    public void close() throws IOException {
        super.close();
        serverSocket.close();
        listener.interrupt();
        handlers.forEach(Thread::interrupt);
    }

    private class ConnectionsListener implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    val socket = serverSocket.accept();
                    val handler = new Thread(new TcpSyncConnectionHandler(socket));
                    handlers.add(handler);
                    handler.start();
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
