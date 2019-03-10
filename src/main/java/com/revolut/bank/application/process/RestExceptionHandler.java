package com.revolut.bank.application.process;

import com.revolut.bank.application.api.error.ApiApplicationError;
import com.revolut.bank.application.api.error.ApiErrors;
import com.revolut.bank.application.api.error.ApiErrorsResponse;
import com.revolut.bank.application.engine.error.factory.ApplicationErrorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * JAX-RS exception handler to convert unhandled command error to API responses
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class RestExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final ApiErrorsResponse TECHNICAL_ERROR = ApiErrorsResponse.of(
            ApiErrors.builder()
                    .withApplication(ApiApplicationError.from(ApplicationErrorFactory.technicalError()))
                    .build()
    );

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            Response.StatusType status = ((WebApplicationException) exception).getResponse().getStatusInfo();
            return Response.status(status.getStatusCode())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(status.getReasonPhrase())
                    .build();
        } else {
            log.error("Unhandled error occurred", exception);
            return Response.serverError()
                    .entity(TECHNICAL_ERROR)
                    .build();
        }
    }

}
