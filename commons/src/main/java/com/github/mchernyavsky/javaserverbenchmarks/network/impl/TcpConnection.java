package com.github.mchernyavsky.javaserverbenchmarks.network.impl;

import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class TcpConnection implements Connection {
    @NotNull
    private final Socket socket;
    @NotNull
    private final DataInputStream input;
    @NotNull
    private final DataOutputStream output;

    public TcpConnection(@NotNull final Socket socket) throws IOException {
        this.socket = socket;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    @NotNull
    @Override
    public Message receiveData() throws IOException {
        log.trace("Receiving data");
        val size = input.readInt();
        val buffer = new byte[size];
        input.readFully(buffer);
        return Message.parseFrom(buffer);
    }

    @Override
    public void sendData(@NotNull final Message request) throws IOException {
        log.trace("Sending data");
        output.writeInt(request.getSerializedSize());
        output.write(request.toByteArray());
        output.flush();
    }

    @Override
    public void close() throws IOException {
        log.trace("Closing the connection");
        socket.close();
    }
}
