package com.github.mchernyavsky.javaserverbenchmarks.server.udp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.Constants;
import com.github.mchernyavsky.javaserverbenchmarks.server.udp.AbstractUdpTestServer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UdpThreadPerClientTestServer extends AbstractUdpTestServer {
    @NotNull
    private final Thread listener = new Thread(new ConnectionsListener());
    @NotNull
    private final List<Thread> handlers = new ArrayList<>();

    public UdpThreadPerClientTestServer(final int port) throws IOException {
        super(port);
        listener.start();
    }

    public void close() throws IOException {
        super.close();
        listener.interrupt();
        handlers.forEach(Thread::interrupt);
    }

    private class ConnectionsListener implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    val buffer = new byte[Constants.maxUdpPacketSize];
                    val packet = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(packet);
                    val handler = new Thread(new UdpSyncConnectionHandler(serverSocket, packet));
                    handlers.add(handler);
                    handler.start();
                } catch (IOException exception) {
                    log.trace("Caught IOException");
                    log.error(exception.getMessage());
                }
            }
        }
    }
}
