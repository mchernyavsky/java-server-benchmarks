package com.github.mchernyavsky.javaserverbenchmarks.network;

import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Connection extends AutoCloseable {

    @NotNull
    Message receiveData() throws IOException;

    void sendData(@NotNull Message request) throws IOException;

    @Override
    void close() throws IOException;
}
