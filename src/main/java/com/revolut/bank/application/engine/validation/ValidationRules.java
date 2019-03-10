package com.revolut.bank.application.engine.validation;

import com.revolut.bank.application.engine.error.ValidationError;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Represents list of validation rules for command's request
 *
 * @param <RequestT> request's type
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@FunctionalInterface
public interface ValidationRules<RequestT> {

    /**
     * Validate request and returns list of errors
     *
     * @param request request
     * @return list of errors (empty if there are not errors)
     */
    @Nonnull
    List<ValidationError> validate(@Nonnull RequestT request);

    /**
     * Returns empty validation rules
     *
     * @param <RequestT> request's type
     * @return empty validation rules
     */
    @Nonnull
    static <RequestT> ValidationRules<RequestT> emptyValidationRules() {
        return request -> Collections.emptyList();
    }

}
