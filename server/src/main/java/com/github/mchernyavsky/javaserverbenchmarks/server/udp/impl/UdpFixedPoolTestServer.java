package com.github.mchernyavsky.javaserverbenchmarks.server.udp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.Constants;
import com.github.mchernyavsky.javaserverbenchmarks.server.udp.AbstractUdpTestServer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class UdpFixedPoolTestServer extends AbstractUdpTestServer {
    @NotNull
    private final Thread listener = new Thread(new ConnectionsListener());
    @NotNull
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public UdpFixedPoolTestServer(final int port) throws IOException {
        super(port);
        listener.start();
    }

    public void close() throws IOException {
        super.close();
        listener.interrupt();
        executor.shutdownNow();
    }

    private class ConnectionsListener implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    val buffer = new byte[Constants.maxUdpPacketSize];
                    val packet = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(packet);
                    executor.submit(new UdpSyncConnectionHandler(serverSocket, packet));
                } catch (SocketException exception) {
                    log.trace("Caught SocketException");
                } catch (IOException exception) {
                    log.trace("Caught IOException");
                    log.error(exception.getMessage());
                }
            }
        }
    }
}
