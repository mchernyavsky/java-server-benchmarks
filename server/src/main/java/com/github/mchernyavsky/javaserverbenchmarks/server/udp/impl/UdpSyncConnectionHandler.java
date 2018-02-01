package com.github.mchernyavsky.javaserverbenchmarks.server.udp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import com.github.mchernyavsky.javaserverbenchmarks.network.impl.UdpConnection;
import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractSyncConnectionHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
final class UdpSyncConnectionHandler extends AbstractSyncConnectionHandler {
    @Getter
    @NotNull
    private final Connection connection;

    UdpSyncConnectionHandler(@NotNull final DatagramSocket serverSocket,
                             @NotNull final DatagramPacket packet) {
        connection = new UdpConnection(serverSocket, packet);
    }
}
