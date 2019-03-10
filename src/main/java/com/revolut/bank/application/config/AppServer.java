package com.revolut.bank.application.config;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Server to listen requests to API methods
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class AppServer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(AppServer.class);

    private final AppSettings settings;
    private HttpServer httpServer;

    public AppServer(@Nonnull AppSettings settings) {
        this.settings = requireNonNull(settings, "settings");
    }

    /**
     * Starts server
     */
    public void run() {
        httpServer = GrizzlyHttpServerFactory.createHttpServer(settings.getApiBaseUrl(), getResourceConfig());
        log.info("Application server was started: {}", settings.getApiBaseUrl());
    }

    @Nonnull
    private AppResourceConfig getResourceConfig() {
        return new AppResourceConfig(settings);
    }

    @Override
    public void close() {
        if (httpServer != null) {
            httpServer.shutdownNow();
        }
    }

}
