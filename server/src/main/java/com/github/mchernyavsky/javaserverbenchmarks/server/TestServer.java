package com.github.mchernyavsky.javaserverbenchmarks.server;

import java.io.IOException;

public interface TestServer extends AutoCloseable {
    @Override
    void close() throws IOException;
}
