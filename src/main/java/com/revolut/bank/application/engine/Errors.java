package com.revolut.bank.application.engine;

import com.google.common.collect.ImmutableList;
import com.revolut.bank.application.engine.error.ApplicationError;
import com.revolut.bank.application.engine.error.ValidationError;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents errors occurred during command execution
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class Errors {

    /**
     * List of validation errors.
     * Empty if there are no validation errors
     */
    private final List<ValidationError> validationErrors;

    /**
     * Application error
     */
    @Nullable
    private final ApplicationError applicationError;

    /**
     * Represents timeout after which command can be invoked again
     */
    @Nullable
    private final Duration retryAfter;

    private Errors(
            @Nullable List<ValidationError> validationErrors,
            @Nullable ApplicationError applicationError,
            @Nullable Duration retryAfter) {
        this.validationErrors = validationErrors == null
                ? Collections.emptyList()
                : validationErrors;
        this.applicationError = applicationError;
        this.retryAfter = retryAfter;
    }

    @Nonnull
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    @Nonnull
    public Optional<ApplicationError> getApplicationError() {
        return Optional.ofNullable(applicationError);
    }

    @Nonnull
    public Optional<Duration> getRetryAfter() {
        return Optional.ofNullable(retryAfter);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Errors other = (Errors) obj;
        return Objects.equals(validationErrors, other.validationErrors) &&
                Objects.equals(applicationError, other.applicationError);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validationErrors, applicationError);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Errors{" +
                "validationErrors=" + validationErrors +
                ", applicationError=" + applicationError +
                ", retryAfter=" + retryAfter +
                '}';
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private List<ValidationError> validation;
        private ApplicationError application;
        private Duration retryAfter;

        private Builder() {
        }

        @Nonnull
        public Builder withValidation(@Nonnull Collection<ValidationError> validation) {
            this.validation = ImmutableList.copyOf(validation);
            return this;
        }

        @Nonnull
        public Builder withApplication(@Nonnull ApplicationError application) {
            this.application = application;
            return this;
        }

        @Nonnull
        public Builder withRetryAfter(@Nonnull Duration retryAfter) {
            this.retryAfter = retryAfter;
            return this;
        }

        @Nonnull
        public Errors build() {
            return new Errors(
                    validation,
                    application,
                    retryAfter
            );
        }

    }

}
