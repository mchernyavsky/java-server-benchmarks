package com.github.mchernyavsky.javaserverbenchmarks;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@UtilityClass
public final class BenchmarkMain {

    public void main(@NotNull final String[] args) {
        val options = buildOptions();
        try {
            log.trace("Parsing options");
            val parser = new DefaultParser();
            val commandLine = parser.parse(options, args);
            val testConfig = createTestConfigFromCommandLine(commandLine);
            log.trace("Testing started");
            val testResult = Benchmark.doTest(testConfig);
            log.trace("Testing finished");
            testResult.exportResult(testConfig);
        } catch (ParseException exception) {
            log.trace("Caught ParseException");
            log.error(exception.getMessage());
            printHelp(options);
        } catch (IOException exception) {
            log.trace("Caught IOException");
            log.error(exception.getMessage());
        }
    }

    @NotNull
    private Options buildOptions() {
        val options = new Options();

        options.addRequiredOption("a", "server-arch", true,
                "Server architecture:"
                        + IntStream.range(0, TestServerType.values().length)
                        .mapToObj(i -> String.format("%d - %s", i, TestServerType.getByOrdinal(i)))
                        .collect(Collectors.joining(System.lineSeparator(),
                                System.lineSeparator(),
                                System.lineSeparator()))
        );

        options.addRequiredOption("x", "requests-number", true, "Number of requests from client");

        options.addOption("n", "arrays-size", true, "Number of elements in arrays");
        options.addOption("m", "clients-number", true, "Number of clients");
        options.addOption("d", "delay", true, "Delay between requests (ms)");

        options.addRequiredOption("p", "parameter", true, "Tracked parameter (n, m or d)");

        options.addRequiredOption("f", "seq-from", true, "Sequence starts");
        options.addRequiredOption("t", "seq-to", true, "Sequence ends (> f)");
        options.addRequiredOption("s", "seq-step", true, "Sequence step (> 0)");

        return options;
    }

    private void printHelp(@NotNull final Options options) {
        val commandLineSyntax = "java benchmark.jar";
        val helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(commandLineSyntax, options);
    }

    @NotNull
    private TestConfig createTestConfigFromCommandLine(@NotNull final CommandLine commandLine)
            throws ParseException {
        try {
            TestConfig.TestConfigBuilder testConfigBuilder = TestConfig.builder()
                    .serverType(TestServerType.getByOrdinal(
                            Integer.parseInt(commandLine.getOptionValue('a'))))
                    .requestsNumber(Integer.parseInt(commandLine.getOptionValue('x')));

            val parameterToTest = TestConfig.Parameter.valueOf(
                    commandLine.getOptionValue('p').toUpperCase());
            testConfigBuilder = testConfigBuilder.parameter(parameterToTest);
            switch (parameterToTest) {
                case N:
                    testConfigBuilder = testConfigBuilder
                            .clientsNumber(Integer.parseInt(commandLine.getOptionValue('m')))
                            .delay(Integer.parseInt(commandLine.getOptionValue('d')));
                    break;
                case M:
                    testConfigBuilder = testConfigBuilder
                            .arraySize(Integer.parseInt(commandLine.getOptionValue('n')))
                            .delay(Integer.parseInt(commandLine.getOptionValue('d')));
                    break;
                case D:
                    testConfigBuilder = testConfigBuilder
                            .arraySize(Integer.parseInt(commandLine.getOptionValue('n')))
                            .clientsNumber(Integer.parseInt(commandLine.getOptionValue('m')));
                    break;
                default:
                    throw new ParseException(String.format("Invalid parameter: %s",
                            parameterToTest.name().toLowerCase()));
            }

            val seqFrom = Integer.parseInt(commandLine.getOptionValue('f'));
            val seqTo = Integer.parseInt(commandLine.getOptionValue('t'));
            val seqStep = Integer.parseInt(commandLine.getOptionValue('s'));
            if (!(seqFrom < seqTo || seqStep > 0)) {
                throw new ParseException(String.format("Invalid range: "
                        + "[seqFrom=%d, seqTo=%d, seqStep=%d]", seqFrom, seqTo, seqStep));
            }
            testConfigBuilder = testConfigBuilder
                    .seqFrom(seqFrom)
                    .seqTo(seqTo)
                    .seqStep(seqStep);

            return testConfigBuilder.build();
        } catch (NumberFormatException exception) {
            log.trace("Caught NumberFormatException");
            throw new ParseException(exception.getMessage());
        }
    }
}
