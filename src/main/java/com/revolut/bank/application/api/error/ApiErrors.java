package com.revolut.bank.application.api.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Class to holds errors returned from commands
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class ApiErrors {

    /**
     * List of errors occurred during request validation
     */
    @JsonProperty("validation")
    private final List<ApiValidationError> validation;

    /**
     * Application error occurred during command execution
     */
    @JsonProperty("application")
    private final ApiApplicationError application;

    @JsonCreator
    private ApiErrors(
            @JsonProperty("validation") @Nullable List<ApiValidationError> validation,
            @JsonProperty("application") @Nullable ApiApplicationError application
    ) {
        this.validation = validation;
        this.application = application;
    }

    @Nullable
    public List<ApiValidationError> getValidation() {
        return validation;
    }

    @Nullable
    public ApiApplicationError getApplication() {
        return application;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ApiErrors{" +
                "validation=" + validation +
                ", application=" + application +
                '}';
    }

    /**
     * Returns builder to construct {@link ApiErrors}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link ApiErrors} type
     */
    public static class Builder {

        private List<ApiValidationError> validation;
        private ApiApplicationError application;

        private Builder() {
        }

        @Nonnull
        public Builder withValidation(@Nonnull List<ApiValidationError> validation) {
            this.validation = ImmutableList.copyOf(validation);
            return this;
        }

        @Nonnull
        public Builder withApplication(@Nonnull ApiApplicationError application) {
            this.application = application;
            return this;
        }

        @Nonnull
        public ApiErrors build() {
            return new ApiErrors(
                    validation,
                    application
            );
        }

    }

}

