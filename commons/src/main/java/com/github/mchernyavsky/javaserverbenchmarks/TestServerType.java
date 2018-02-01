package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum TestServerType {
    TCP_THREAD_PER_CLIENT,
    TCP_CACHED_POOL,
    TCP_NON_BLOCKING_FIXED_POOL,
    TCP_SINGLE_THREAD,
    TCP_ASYNC,
    UDP_THREAD_PER_CLIENT,
    UDP_FIXED_POOL;

    @NotNull
    public static TestServerType getByOrdinal(final int ordinal) {
        return values()[ordinal];
    }
}
