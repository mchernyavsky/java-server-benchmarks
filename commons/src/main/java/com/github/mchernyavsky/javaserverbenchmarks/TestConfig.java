package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class TestConfig {
    @NotNull private TestServerType serverType;
    private int requestsNumber;
    private int arraySize;
    private int clientsNumber;
    private int delay;
    @NotNull private Parameter parameter;
    private int seqFrom;
    private int seqTo;
    private int seqStep;

    public enum Parameter { N, M, D }
}
