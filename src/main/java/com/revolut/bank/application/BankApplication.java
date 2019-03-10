package com.revolut.bank.application;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.revolut.bank.application.config.AppServer;
import com.revolut.bank.application.config.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Application;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * Main application
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class BankApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(BankApplication.class);

    private static final String KEY_TO_STOP_SERVER = "q";

    public static void main(String[] args) {
        AppArguments arguments = parseArgs(args);
        if (arguments.help) {
            JCommander c = new JCommander(arguments);
            c.setProgramName(BankApplication.class.getSimpleName());
            c.usage();
        } else {
            startApplication(arguments);
        }
    }

    private static void startApplication(@Nonnull AppArguments settings) {
        try(AppServer server = new AppServer(getAppSettings(settings))) {
            server.run();
            waitForExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private static AppSettings getAppSettings(@Nonnull AppArguments settings) {
        return AppSettings.builder()
                .withHost(settings.host)
                .withPort(settings.port)
                .withBasePath(settings.application)
                .withMinAccountBalance(settings.minAccountBalance)
                .withMaxAccountBalance(settings.maxAccountBalance)
                .withCommandThreadsCount(settings.threadsCount)
                .build();
    }

    private static void waitForExit() throws IOException {
        log.info("Press \"{}\" to exit", KEY_TO_STOP_SERVER);
        final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        do {
            final String userInput = inReader.readLine();
            if (userInput == null || KEY_TO_STOP_SERVER.equals(userInput)) {
                break;
            }
        } while (true);
    }

    @Nonnull
    private static AppArguments parseArgs(@Nonnull String[] args) {
        AppArguments arguments = new AppArguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        return arguments;
    }

    /**
     * Application input settings
     */
    private static class AppArguments {

        @Parameter(names = { "--host", "-h" }, description = "Host to listen requests")
        private String host = "localhost";

        @Parameter(names = { "--port", "-p" }, description = "Port to listen requests")
        private int port = 18080;

        @Parameter(names = {"--app", "-a"}, description = "Application name")
        private String application = "bank";

        @Parameter(names = {"--threads-count"}, description = "Number of threads to process API requests")
        private int threadsCount = 100;

        @Parameter(names = {"--min-account-balance"}, description = "Minimum allowable amount of money on account balance")
        private BigDecimal minAccountBalance = BigDecimal.ZERO;

        @Parameter(names = {"--max-account-balance"}, description = "Maximum allowable amount of money on account balance")
        private BigDecimal maxAccountBalance = new BigDecimal("1000000000000000000");

        @Parameter(names = { "--debug", "-d" }, description = "Debug mode")
        private boolean debug = false;

        @Parameter(names = { "--help" }, help = true, description = "Help")
        private boolean help = false;

    }

}
