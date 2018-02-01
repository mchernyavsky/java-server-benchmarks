package com.github.mchernyavsky.javaserverbenchmarks;

import com.github.mchernyavsky.javaserverbenchmarks.client.TestClient;
import com.github.mchernyavsky.javaserverbenchmarks.client.tcp.impl.TcpTestClient;
import com.github.mchernyavsky.javaserverbenchmarks.client.udp.impl.UdpTestClient;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@UtilityClass
public final class TestClientFactory {
    @NotNull
    TestClient createClientForServerType(@NotNull final TestServerType type) {
        final TestClient client;
        switch (type) {
            case TCP_THREAD_PER_CLIENT:
            case TCP_CACHED_POOL:
            case TCP_NON_BLOCKING_FIXED_POOL:
                log.trace("Creating TcpTestClient");
                client = new TcpTestClient(true);
                break;
            case TCP_SINGLE_THREAD:
            case TCP_ASYNC:
                log.trace("Creating TcpTestClient");
                client = new TcpTestClient(false);
                break;
            case UDP_THREAD_PER_CLIENT:
            case UDP_FIXED_POOL:
                log.trace("Creating UdpTestClient");
                client = new UdpTestClient(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown TestServerType: " + type.name());
        }
        return client;
    }
}
