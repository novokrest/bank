package com.revolut.bank.application.process;

import com.revolut.bank.application.api.error.ApiApplicationError;
import com.revolut.bank.application.api.error.ApiErrors;
import com.revolut.bank.application.api.error.ApiErrorsResponse;
import com.revolut.bank.application.api.error.ApiValidationError;
import com.revolut.bank.application.engine.CommandResult;
import com.revolut.bank.application.engine.Errors;
import com.revolut.bank.application.engine.error.ApplicationError;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JAX-RS filter to convert command result to API responses
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Provider
public class RestCommandResponseFilter implements ContainerResponseFilter {

    private static final ApiErrorsResponse RETRY_AFTER_ERROR = ApiErrorsResponse.of(
            ApiErrors.builder().withApplication(ApiApplicationError.builder()
                    .withCode("RetryAfter")
                    .withMessage("Service is unavailable")
                    .build())
                    .build()
    );

    private static final Object EMPTY_RESPONSE = new Object();

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (responseContext.getEntity().getClass().isAssignableFrom(CommandResult.class)) {
            CommandResult<?> result = (CommandResult<?>) responseContext.getEntity();
            convertResultToResponse(responseContext, result);
        }
    }

    private static void convertResultToResponse(@Nonnull ContainerResponseContext responseContext,
                                                @Nonnull CommandResult<?> result) {
        if (result.isSuccess()) {
            responseContext.setStatusInfo(Response.Status.OK);
            responseContext.setEntity(result.getResponse().map(Function.<Object>identity()).orElse(EMPTY_RESPONSE));
        } else if (isValidationError(result.getErrorsOrThrow())) {
            responseContext.setStatusInfo(Response.Status.BAD_REQUEST);
            responseContext.setEntity(getValidationErrorResponse(result.getErrorsOrThrow()));
        } else if (isApplicationError(result.getErrorsOrThrow())) {
            responseContext.setStatusInfo(Response.Status.OK);
            responseContext.setEntity(getApplicationErrorResponse(result.getErrorsOrThrow()));
        } else if (isRetryAfter(result.getErrorsOrThrow())) {
            Duration retryAfter = result.getErrorsOrThrow().getRetryAfter().get();
            responseContext.setStatusInfo(Response.Status.SERVICE_UNAVAILABLE);
            responseContext.getHeaders().add(HttpHeaders.RETRY_AFTER, retryAfter.getSeconds());
            responseContext.setEntity(RETRY_AFTER_ERROR);
        } else {
            throw new RuntimeException("Invalid command result: result=" + result);
        }
    }

    private static boolean isValidationError(@Nonnull Errors errors) {
        return CollectionUtils.isNotEmpty(errors.getValidationErrors());
    }

    @Nonnull
    private static ApiErrorsResponse getValidationErrorResponse(@Nonnull Errors errors) {
        List<ApiValidationError> validationErrors = errors.getValidationErrors().stream()
                .map(error -> ApiValidationError.builder()
                        .withParamName(error.getParamName())
                        .withCode(error.getCode())
                        .withMessage(error.getMessage())
                        .build())
                .collect(Collectors.toList());
        return ApiErrorsResponse.of(ApiErrors.builder()
                .withValidation(validationErrors)
                .build());
    }

    private static boolean isApplicationError(@Nonnull Errors errors) {
        return errors.getApplicationError().isPresent();
    }

    @Nonnull
    private static ApiErrorsResponse getApplicationErrorResponse(@Nonnull Errors errors) {
        ApplicationError applicationError = errors.getApplicationError().get();
        return ApiErrorsResponse.of(ApiErrors.builder()
                .withApplication(ApiApplicationError.builder()
                        .withCode(applicationError.getCode())
                        .withMessage(applicationError.getMessage())
                        .build())
                .build());
    }

    private static boolean isRetryAfter(Errors errors) {
        return errors.getRetryAfter().isPresent();
    }

}
