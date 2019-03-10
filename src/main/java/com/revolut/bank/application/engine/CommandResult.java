package com.revolut.bank.application.engine;

import com.revolut.bank.application.engine.error.ApplicationError;
import com.revolut.bank.application.engine.error.ValidationError;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Represent response of command execution
 *
 * @param <ResponseT> type of returned command response
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class CommandResult<ResponseT> {

    /**
     * Flag if command was executed succesfully
     */
    private final boolean success;

    /**
     * Command's response
     */
    private final ResponseT response;

    /**
     * Errors occurred during command execution
     */
    private final Errors errors;

    private CommandResult(
            boolean success,
            @Nullable ResponseT response,
            @Nullable Errors errors
    ) {
        this.success = success;
        this.response = response;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nonnull
    public Optional<ResponseT> getResponse() {
        return Optional.ofNullable(response);
    }

    @Nonnull
    public Errors getErrorsOrThrow() {
        if (errors == null) {
            throw new IllegalStateException("Errors is absent");
        }
        return errors;
    }

    @Nonnull
    @Override
    public String toString() {
        return "CommandResult<ResponseT>{" +
                "success=" + success +
                ", response=" + response +
                ", errors=" + errors +
                '}';
    }

    @Nonnull
    public static <ResponseT> CommandResult<ResponseT> success() {
        return CommandResult.<ResponseT>builder()
                .withSuccess(true)
                .build();
    }

    @Nonnull
    public static <ResponseT> CommandResult<ResponseT> success(@Nonnull ResponseT response) {
        requireNonNull(response, "response");
        return CommandResult.<ResponseT>builder()
                .withSuccess(true)
                .withResponse(response)
                .build();
    }

    @Nonnull
    public static <ResponseT> CommandResult<ResponseT> retryAfter(@Nonnull Duration retryAfter) {
        requireNonNull(retryAfter, "retryAfter");
        return CommandResult.<ResponseT>builder()
                .withSuccess(false)
                .withErrors(Errors.builder()
                        .withRetryAfter(retryAfter)
                        .build())
                .build();
    }

    @Nonnull
    public static <ResponseT> CommandResult<ResponseT> applicationError(@Nonnull ApplicationError error) {
        requireNonNull(error, "error");
        return CommandResult.<ResponseT>builder()
                .withSuccess(false)
                .withErrors(Errors.builder()
                        .withApplication(error)
                        .build())
                .build();
    }

    @Nonnull
    public static <ResponseT> CommandResult<ResponseT> validationErrors(Collection<ValidationError> errors) {
        if (CollectionUtils.isEmpty(errors)) {
            throw new IllegalArgumentException("Error list must not be empty");
        }
        return CommandResult.<ResponseT>builder()
                .withSuccess(false)
                .withErrors(Errors.builder()
                        .withValidation(errors)
                        .build())
                .build();
    }

    @Nonnull
    public static <ResponseT> Builder<ResponseT> builder() {
        return new Builder<>();
    }

    public static class Builder<ResponseT> {

        private Boolean success;
        private ResponseT response;
        private Errors errors;

        private Builder() {
        }

        @Nonnull
        public Builder<ResponseT> withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        @Nonnull
        public Builder<ResponseT> withResponse(@Nonnull ResponseT response) {
            this.response = response;
            return this;
        }

        @Nonnull
        public Builder<ResponseT> withErrors(@Nonnull Errors errors) {
            this.errors = errors;
            return this;
        }

        @Nonnull
        public CommandResult<ResponseT> build() {
            return new CommandResult<ResponseT>(
                    success,
                    response,
                    errors
            );
        }

    }

}
