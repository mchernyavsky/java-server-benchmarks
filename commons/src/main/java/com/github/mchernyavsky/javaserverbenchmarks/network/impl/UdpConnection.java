package com.github.mchernyavsky.javaserverbenchmarks.network.impl;

import com.github.mchernyavsky.javaserverbenchmarks.Constants;
import com.github.mchernyavsky.javaserverbenchmarks.FixedByteArrayOutputStream;
import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

@Slf4j
public class UdpConnection implements Connection {
    @NotNull
    private final DatagramSocket socket;
    @NotNull
    private final SocketAddress remoteAddress;
    @NotNull
    private final byte[] buffer;
    private boolean isMessageReceived;

    public UdpConnection(@NotNull final DatagramSocket socket,
                         @NotNull final DatagramPacket packet) {
        this(socket, packet.getSocketAddress(), packet.getData());
        isMessageReceived = true;
    }

    public UdpConnection(@NotNull final SocketAddress remoteAddress) throws SocketException {
        this(new DatagramSocket(), remoteAddress, new byte[Constants.maxUdpPacketSize]);
    }

    private UdpConnection(@NotNull final DatagramSocket socket,
                          @NotNull final SocketAddress remoteAddress,
                          @NotNull final byte[] buffer) {
        this.socket = socket;
        this.remoteAddress = remoteAddress;
        this.buffer = buffer;
    }

    @NotNull
    @Override
    public Message receiveData() throws IOException {
        log.trace("Receiving data");
        val responsePacket = new DatagramPacket(buffer, buffer.length, remoteAddress);
        if (!isMessageReceived) {
            socket.receive(responsePacket);
        }
        isMessageReceived = false;
        val input = new DataInputStream(new ByteArrayInputStream(buffer));
        input.skipBytes(Integer.BYTES);
        return Message.parseDelimitedFrom(input);
    }

    @Override
    public void sendData(@NotNull final Message request) throws IOException {
        log.trace("Sending data");
        val arrayStream = new FixedByteArrayOutputStream(buffer);
        val output = new DataOutputStream(arrayStream);
        output.writeInt(request.getSerializedSize());
        request.writeDelimitedTo(output);
        val requestPacket = new DatagramPacket(buffer, arrayStream.getCount(), remoteAddress);
        socket.send(requestPacket);
    }

    @Override
    public void close() {
        log.trace("Closing the connection");
        socket.close();
    }
}
