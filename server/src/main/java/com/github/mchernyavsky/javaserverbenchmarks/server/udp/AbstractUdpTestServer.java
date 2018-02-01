package com.github.mchernyavsky.javaserverbenchmarks.server.udp;

import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractTestServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramSocket;

public abstract class AbstractUdpTestServer extends AbstractTestServer {
    @NotNull
    protected final DatagramSocket serverSocket;

    public AbstractUdpTestServer(final int port) throws IOException {
        serverSocket = new DatagramSocket(port);
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
