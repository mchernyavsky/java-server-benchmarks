package com.github.mchernyavsky.javaserverbenchmarks.client.tcp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.client.AbstractTestClient;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import com.github.mchernyavsky.javaserverbenchmarks.network.impl.TcpConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public class TcpTestClient extends AbstractTestClient {

    public TcpTestClient(final boolean singleConnection) {
        super(singleConnection);
    }

    @NotNull
    @Override
    public Connection connect(@NotNull final String host, final int port) throws IOException {
        return new TcpConnection(new Socket(host, port));
    }
}
