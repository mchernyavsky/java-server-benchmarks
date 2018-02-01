package com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import com.github.mchernyavsky.javaserverbenchmarks.network.impl.TcpConnection;
import com.github.mchernyavsky.javaserverbenchmarks.server.AbstractSyncConnectionHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

@Slf4j
final class TcpSyncConnectionHandler extends AbstractSyncConnectionHandler {
    @Getter
    @NotNull
    private final Connection connection;

    TcpSyncConnectionHandler(@NotNull final Socket socket) throws IOException {
        connection = new TcpConnection(socket);
    }
}
