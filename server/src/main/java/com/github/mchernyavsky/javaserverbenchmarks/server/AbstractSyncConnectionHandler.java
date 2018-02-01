package com.github.mchernyavsky.javaserverbenchmarks.server;

import com.github.mchernyavsky.javaserverbenchmarks.SortUtils;
import com.github.mchernyavsky.javaserverbenchmarks.StatService;
import com.github.mchernyavsky.javaserverbenchmarks.commons.network.Message;
import com.github.mchernyavsky.javaserverbenchmarks.network.Connection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

@Slf4j
public abstract class AbstractSyncConnectionHandler implements Runnable {
    @Override
    public final void run() {
        try {
            while (true) {
                val connection = getConnection();

                val startClientProcessing = System.currentTimeMillis();
                val request = connection.receiveData();

                val startRequestProcessing = System.currentTimeMillis();
                val data = new ArrayList<Integer>(request.getDataList());
                SortUtils.selectionSort(data);
                val finishRequestProcessing = System.currentTimeMillis();
                StatService.getInstance().addRequestProcessingTime(
                        finishRequestProcessing - startRequestProcessing);

                val responseBuilder = Message.newBuilder();
                data.forEach(responseBuilder::addData);
                connection.sendData(responseBuilder.build());

                val finishClientProcessing = System.currentTimeMillis();
                StatService.getInstance().addClientProcessingTime(
                        finishClientProcessing - startClientProcessing);
            }
        } catch (EOFException ignored) {
            log.trace("Caught EOFException");
        } catch (SocketException exception) {
            log.trace("Caught SocketException");
        } catch (IOException exception) {
            log.trace("Caught IOException");
            log.error(exception.getMessage());
        }
    }

    @NotNull
    protected abstract Connection getConnection();
}
