package com.revolut.bank.application.api.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.bank.application.engine.error.ApplicationError;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Application error returned from API methods
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Application error")
public class ApiApplicationError {

    /**
     * Error code
     */
    @ApiModelProperty(
            value = "Error code",
            example = "AccountNotFound",
            required = true
    )
    @JsonProperty("code")
    private final String code;

    /**
     * Error message
     */
    @ApiModelProperty(
            value = "Error message",
            example = "Account was not found",
            required = true
    )
    @JsonProperty("message")
    private final String message;

    @JsonCreator
    private ApiApplicationError(
            @JsonProperty("code") @Nonnull String code,
            @JsonProperty("message") @Nonnull String message
    ) {
        this.code = requireNonNull(code, "code");
        this.message = requireNonNull(message, "message");
    }

    @Nonnull
    public static ApiApplicationError from(@Nonnull ApplicationError error) {
        return ApiApplicationError.builder()
                .withCode(error.getCode())
                .withMessage(error.getMessage())
                .build();
    }

    @Nonnull
    public String getCode() {
        return code;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ApiApplicationError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * Returns builder to construct {@link ApiApplicationError}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link ApiApplicationError} type
     */
    public static class Builder {

        private String code;
        private String message;

        private Builder() {
        }

        @Nonnull
        public Builder withCode(@Nonnull String code) {
            this.code = code;
            return this;
        }

        @Nonnull
        public Builder withMessage(@Nonnull String message) {
            this.message = message;
            return this;
        }

        @Nonnull
        public ApiApplicationError build() {
            return new ApiApplicationError(
                    code,
                    message
            );
        }

    }

}
