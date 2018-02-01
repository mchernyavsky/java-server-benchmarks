package com.github.mchernyavsky.javaserverbenchmarks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StatService {
    @NotNull
    private final List<Long> requestProcessingTimes =
            Collections.synchronizedList(new ArrayList<>());
    @NotNull
    private final List<Long> clientProcessingTimes =
            Collections.synchronizedList(new ArrayList<>());
    @NotNull
    private final List<Long> clientLifeTimes = Collections.synchronizedList(new ArrayList<>());

    private StatService() {
    }

    public double getAverageRequestProcessingTime() {
        return calculateAverage(requestProcessingTimes);
    }

    public double getAverageClientProcessingTime() {
        return calculateAverage(clientProcessingTimes);
    }

    public double getAverageClientLifeTime() {
        return calculateAverage(clientLifeTimes);
    }

    public void addRequestProcessingTime(final long time) {
        requestProcessingTimes.add(time);
    }

    public void addClientProcessingTime(final long time) {
        clientProcessingTimes.add(time);
    }

    public void addClientLifeTime(final long time) {
        clientLifeTimes.add(time);
    }

    private double calculateAverage(@NotNull final List<Long> items) {
        return items.stream()
                .mapToLong(i -> i)
                .average()
                .orElse(0);
    }

    public static StatService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static class SingletonHolder {
        static final StatService INSTANCE = new StatService();
    }
}
