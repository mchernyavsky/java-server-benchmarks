package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Constants {
    @NotNull
    public final String serverHost = "localhost";
    public final int serverPort = 4242;
    public final int maxUdpPacketSize = 64 * 1024;
}
