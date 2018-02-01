package com.github.mchernyavsky.javaserverbenchmarks.client.udp.impl;

import com.github.mchernyavsky.javaserverbenchmarks.client.AbstractTestClient;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import com.github.mchernyavsky.javaserverbenchmarks.network.impl.UdpConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

public class UdpTestClient extends AbstractTestClient {

    public UdpTestClient(final boolean singleConnection) {
        super(singleConnection);
    }

    @NotNull
    @Override
    public Connection connect(@NotNull final String host, final int port) throws IOException {
        return new UdpConnection(new InetSocketAddress(host, port));
    }
}
