package com.github.mchernyavsky.javaserverbenchmarks;

import com.github.mchernyavsky.javaserverbenchmarks.server.TestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl.TcpAsyncTestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl.TcpCachedPoolTestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl.TcpNonBlockingFixedPoolTestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl.TcpSingleThreadTestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.tcp.impl.TcpThreadPerClientTestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.udp.impl.UdpFixedPoolTestServer;
import com.github.mchernyavsky.javaserverbenchmarks.server.udp.impl.UdpThreadPerClientTestServer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
@UtilityClass
public final class TestServerFactory {
    private final int serverPort = 4242;

    @NotNull
    TestServer createServerForServerType(@NotNull final TestServerType type) throws IOException {
        final TestServer server;
        switch (type) {
            case TCP_THREAD_PER_CLIENT:
                log.trace("Creating TcpThreadPerClientTestServer");
                server = new TcpThreadPerClientTestServer(serverPort);
                break;
            case TCP_CACHED_POOL:
                log.trace("Creating TcpCachedPoolTestServer");
                server = new TcpCachedPoolTestServer(serverPort);
                break;
            case TCP_NON_BLOCKING_FIXED_POOL:
                log.trace("Creating TcpNonBlockingFixedPoolTestServer");
                server = new TcpNonBlockingFixedPoolTestServer(serverPort);
                break;
            case TCP_SINGLE_THREAD:
                log.trace("Creating TcpSingleThreadTestServer");
                server = new TcpSingleThreadTestServer(serverPort);
                break;
            case TCP_ASYNC:
                log.trace("Creating TcpAsyncTestServer");
                server = new TcpAsyncTestServer(serverPort);
                break;
            case UDP_THREAD_PER_CLIENT:
                log.trace("Creating UdpThreadPerClientTestServer");
                server = new UdpThreadPerClientTestServer(serverPort);
                break;
            case UDP_FIXED_POOL:
                log.trace("Creating UdpFixedPoolTestServer");
                server = new UdpFixedPoolTestServer(serverPort);
                break;
            default:
                throw new IllegalArgumentException("Unknown TestServerType: " + type.name());
        }
        return server;
    }
}
