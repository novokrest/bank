package com.revolut.bank.application.api.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Validation error returned from API methods
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Validation error")
public class ApiValidationError {

    /**
     * Name of request's incorrect parameter
     */
    @ApiModelProperty(
            value = "Invalid parameter's name",
            example = "balance",
            required = true
    )
    @JsonProperty("paramName")
    private final String paramName;

    /**
     * Error code
     */
    @ApiModelProperty(
            value = "Error code",
            example = "balanceInvalid",
            required = true
    )
    @JsonProperty("code")
    private final String code;

    /**
     * Error message
     */
    @ApiModelProperty(
            value = "Error message",
            example = "Balance is invalid",
            required = true
    )
    @JsonProperty("message")
    private final String message;

    @JsonCreator
    private ApiValidationError(
            @JsonProperty("paramName") @Nonnull String paramName,
            @JsonProperty("code") @Nonnull String code,
            @JsonProperty("message") @Nonnull String message
    ) {
        this.paramName = requireNonNull(paramName, "paramName");
        this.code = requireNonNull(code, "code");
        this.message = requireNonNull(message, "message");
    }

    @Nonnull
    public String getParamName() {
        return paramName;
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
        return "ApiValidationError{" +
                "paramName='" + paramName + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * Returns builder to construct {@link ApiValidationError}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link ApiValidationError} type
     */
    public static class Builder {

        private String paramName;
        private String code;
        private String message;

        private Builder() {
        }

        @Nonnull
        public Builder withParamName(@Nonnull String paramName) {
            this.paramName = paramName;
            return this;
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
        public ApiValidationError build() {
            return new ApiValidationError(
                    paramName,
                    code,
                    message
            );
        }

    }

}
