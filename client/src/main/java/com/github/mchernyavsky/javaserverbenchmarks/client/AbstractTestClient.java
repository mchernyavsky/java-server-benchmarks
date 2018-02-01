package com.github.mchernyavsky.javaserverbenchmarks.client;

import com.github.mchernyavsky.javaserverbenchmarks.Constants;
import com.github.mchernyavsky.javaserverbenchmarks.StatService;
import com.github.mchernyavsky.javaserverbenchmarks.TestConfig;
import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

@Slf4j
public abstract class AbstractTestClient implements TestClient {
    @NotNull
    private static final Random RANDOM = new Random();
    private final boolean singleConnection;

    protected AbstractTestClient(final boolean singleConnection) {
        this.singleConnection = singleConnection;
    }

    @Override
    public void doTest(@NotNull final TestConfig config) {
        val request = generateRandomSortRequest(config.getArraySize());
        try {
            val start = System.currentTimeMillis();

            if (singleConnection) {
                @Cleanup val connection = connect(Constants.serverHost, Constants.serverPort);
                for (int i = 0; i < config.getRequestsNumber(); i++) {
                    AbstractTestClient.sendAndReceiveMessageWithDelay(
                            connection, request, config.getDelay());
                }
            } else {
                for (int i = 0; i < config.getRequestsNumber(); i++) {
                    @Cleanup val connection = connect(Constants.serverHost, Constants.serverPort);
                    AbstractTestClient.sendAndReceiveMessageWithDelay(
                            connection, request, config.getDelay());
                }
            }

            val finish = System.currentTimeMillis();
            StatService.getInstance().addClientLifeTime(finish - start);
        } catch (IOException | InterruptedException exception) {
            log.error(exception.getMessage());
        }
    }

    private static void sendAndReceiveMessageWithDelay(@NotNull final Connection connection,
                                                       @NotNull final Message request,
                                                       final int delay)
            throws InterruptedException, IOException {
        connection.sendData(request);
        val response = connection.receiveData();
        Thread.sleep(delay);
    }

    @NotNull
    private static Message generateRandomSortRequest(final int elementsNumber) {
        log.trace("Generating array");
        val requestBuilder = Message.newBuilder();
        RANDOM.ints(elementsNumber).forEach(requestBuilder::addData);
        return requestBuilder.build();
    }
}
