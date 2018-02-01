package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@UtilityClass
public final class Benchmark {
    @NotNull
    TestResult doTest(@NotNull final TestConfig config) throws IOException {
        val results = new ArrayList<Object[]>();

        for (int parameterValue = config.getSeqFrom();
             parameterValue < config.getSeqTo();
             parameterValue += config.getSeqStep()) {
            updateParam(config, parameterValue);

            val serverType = config.getServerType();
            @Cleanup val server = TestServerFactory.createServerForServerType(serverType);
            val clients = IntStream.range(0, config.getClientsNumber())
                    .mapToObj(j -> TestClientFactory.createClientForServerType(serverType))
                    .collect(Collectors.toList());

            val clientsThreads = clients.stream()
                    .map(client -> new Thread(() -> client.doTest(config)))
                    .collect(Collectors.toList());
            clientsThreads.forEach(Thread::start);
            for (val thread : clientsThreads) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                    log.trace("Caught InterruptedException");
                }
            }

            val statService = StatService.getInstance();
            val averageRequestProcessingTime = statService.getAverageRequestProcessingTime();
            val averageClientProcessingTime = statService.getAverageClientProcessingTime();
            val averageClientLifeTime = statService.getAverageClientLifeTime();

            results.add(new Object[] {
                parameterValue,
                averageRequestProcessingTime,
                averageClientProcessingTime,
                averageClientLifeTime,
            });
        }

        return new TestResult(results);
    }

    private void updateParam(@NotNull final TestConfig config, final int newValue) {
        switch (config.getParameter()) {
            case N:
                config.setArraySize(newValue);
                break;
            case M:
                config.setClientsNumber(newValue);
                break;
            case D:
                config.setDelay(newValue);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown parameter: " + config.getParameter().name()
                );
        }
    }
}
