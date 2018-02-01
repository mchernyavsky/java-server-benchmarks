package com.github.mchernyavsky.javaserverbenchmarks.server;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public abstract class AbstractTestServer implements TestServer {

    @NotNull
    public AbstractTestServer() {
        log.trace("Creating client");
    }

    @Override
    public void close() throws IOException {
        log.trace("Shutdown client");
    }
}
