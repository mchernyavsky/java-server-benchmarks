package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public final class FixedByteArrayOutputStream extends OutputStream {
    @NotNull
    private final byte[] buffer;
    @Getter
    private int count;

    public FixedByteArrayOutputStream(@NotNull final byte[] buffer) {
        this.buffer = buffer;
    }

    public FixedByteArrayOutputStream(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }

        buffer = new byte[size];
    }

    @Override
    public void write(final int intByte) throws IOException {
        if (count + 1 > buffer.length) {
            throw new IOException("Message too long");
        }

        buffer[count] = (byte) intByte;
        count += 1;
    }

    @Override
    public void write(@NotNull final byte[] bytes, final int offset, final int length)
            throws IOException {
        if (offset < 0 || offset > bytes.length || length < 0
                || offset + length > bytes.length
                || count + length > buffer.length) {
            throw new IOException("Message too long");
        }

        System.arraycopy(bytes, offset, buffer, count, length);
        count += length;
    }
}
