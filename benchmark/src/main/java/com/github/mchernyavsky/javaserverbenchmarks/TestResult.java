package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.Cleanup;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Value
final class TestResult {
    @NotNull
    private static final String RESULTS_FOLDER = "results";

    @NotNull
    private final List<Object[]> data;

    public void exportResult(@NotNull final TestConfig config) throws IOException {
        log.trace("Exporting result");
        exportProperties(config);
        exportData(config);
    }

    private void exportProperties(@NotNull final TestConfig config) throws IOException {
        val properties = new Properties();
        properties.setProperty("server_type", config.getServerType().name());
        properties.setProperty("requests_number", String.valueOf(config.getRequestsNumber()));
        properties.setProperty("parameter", config.getParameter().name());
        properties.setProperty("from", String.valueOf(config.getSeqFrom()));
        properties.setProperty("to", String.valueOf(config.getSeqTo()));
        properties.setProperty("step", String.valueOf(config.getSeqStep()));
        properties.setProperty("array_size", String.valueOf(config.getArraySize()));
        properties.setProperty("clients_number", String.valueOf(config.getClientsNumber()));
        properties.setProperty("delay", String.valueOf(config.getDelay()));

        val fileName = String.format("%s/%s.properties", RESULTS_FOLDER, getTestName(config));
        @Cleanup val output = new FileOutputStream(fileName);
        properties.store(output, null);
    }

    private void exportData(@NotNull final TestConfig config) throws IOException {
        val fileName = String.format("%s/%s.csv", RESULTS_FOLDER, getTestName(config));
        @Cleanup val output = new PrintWriter(new FileOutputStream(fileName));
        output.println("parameter,"
                + "average_request_processing_time,"
                + "average_client_processing_time,"
                + "average_client_lifetime");
        data.stream()
                .map(Arrays::stream)
                .map(record -> record.map(Object::toString).collect(Collectors.joining(",")))
                .forEach(output::println);
    }

    @NotNull
    private String getTestName(@NotNull final TestConfig config) {
        return String.format("%s_%s",
                config.getServerType().name().toLowerCase(),
                config.getParameter().name().toLowerCase()
        );
    }
}
