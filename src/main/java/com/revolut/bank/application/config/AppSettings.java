package com.revolut.bank.application.config;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * Application settings
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 28.03.2019
 */
public class AppSettings {

    /**
     * Host name
     */
    private final String host;

    /**
     * Port number
     */
    private final int port;

    /**
     * Base path for API methods
     */
    private final String basePath;

    /**
     * Minimum allowable monetary amount on account balance
     */
    private final BigDecimal minAccountBalance;

    /**
     * Maximum allowable monetary amount on account balance
     */
    private final BigDecimal maxAccountBalance;

    /**
     * Number of threads to process requests to API commands
     */
    private final int commandThreadsCount;

    private AppSettings(
            @Nonnull String host,
            @Nonnull Integer port,
            @Nonnull String basePath,
            @Nonnull BigDecimal minAccountBalance,
            @Nonnull BigDecimal maxAccountBalance,
            @Nonnull Integer commandThreadsCount
    ) {
        this.host = requireNonNull(host, "host");
        this.port = requireNonNull(port, "port");
        this.basePath = requireNonNull(basePath, "basePath");
        this.minAccountBalance = requireNonNull(minAccountBalance, "minAccountBalance");
        this.maxAccountBalance = requireNonNull(maxAccountBalance, "maxAccountBalance");
        this.commandThreadsCount = requireNonNull(commandThreadsCount, "commandThreadsCount");
    }

    @Nonnull
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Nonnull
    public String getBasePath() {
        return basePath;
    }

    @Nonnull
    public URI getApiBaseUrl() {
        return URI.create(String.format("http://%s:%d/%s", host, port, basePath));
    }

    @Nonnull
    public BigDecimal getMinAccountBalance() {
        return minAccountBalance;
    }

    @Nonnull
    public BigDecimal getMaxAccountBalance() {
        return maxAccountBalance;
    }

    public int getCommandThreadsCount() {
        return commandThreadsCount;
    }

    /**
     * Returns builder to construct {@link AppSettings}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link AppSettings} type
     */
    public static class Builder {

        private String host;
        private Integer port;
        private String basePath;
        private BigDecimal minAccountBalance;
        private BigDecimal maxAccountBalance;
        private Integer commandThreadsCount;

        private Builder() {
        }

        @Nonnull
        public Builder withHost(@Nonnull String host) {
            this.host = host;
            return this;
        }

        @Nonnull
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        @Nonnull
        public Builder withBasePath(@Nonnull String basePath) {
            this.basePath = basePath;
            return this;
        }

        @Nonnull
        public Builder withMinAccountBalance(@Nonnull BigDecimal minAccountBalance) {
            this.minAccountBalance = minAccountBalance;
            return this;
        }

        @Nonnull
        public Builder withMaxAccountBalance(@Nonnull BigDecimal maxAccountBalance) {
            this.maxAccountBalance = maxAccountBalance;
            return this;
        }

        @Nonnull
        public Builder withCommandThreadsCount(int commandThreadsCount) {
            this.commandThreadsCount = commandThreadsCount;
            return this;
        }

        @Nonnull
        public AppSettings build() {
            return new AppSettings(
                    host,
                    port,
                    basePath,
                    minAccountBalance,
                    maxAccountBalance,
                    commandThreadsCount
            );
        }

    }

}


