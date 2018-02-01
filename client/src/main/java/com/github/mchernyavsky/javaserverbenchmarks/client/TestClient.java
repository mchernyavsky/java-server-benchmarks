package com.github.mchernyavsky.javaserverbenchmarks.client;

import com.github.mchernyavsky.javaserverbenchmarks.TestConfig;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface TestClient {

    @NotNull
    Connection connect(@NotNull String host, int port) throws IOException;

    void doTest(@NotNull TestConfig config);
}
