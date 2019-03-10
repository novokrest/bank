package com.revolut.bank.application.api.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Response with errors occurred in API method
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Response with errors occurred in API method")
public class ApiErrorsResponse {

    /**
     * Errors
     */
    @ApiModelProperty(
            value = "Errors",
            required = true
    )
    @JsonProperty("errors")
    private final ApiErrors errors;

    /**
     * Factory method to create {@link ApiErrorsResponse} instance
     *
     * @param errors errors
     * @return new instance
     */
    @Nonnull
    public static ApiErrorsResponse of(@Nonnull ApiErrors errors) {
        return new ApiErrorsResponse(errors);
    }

    @JsonCreator
    private ApiErrorsResponse(@JsonProperty("errors") @Nonnull ApiErrors errors) {
        this.errors = requireNonNull(errors, "errors");
    }

    @Nonnull
    public ApiErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ApiErrorsResponse{" +
                "errors=" + errors +
                '}';
    }

}
