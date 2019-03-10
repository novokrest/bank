package com.revolut.bank.application.engine.validation;

import com.revolut.bank.application.engine.error.ValidationError;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents strategy for applying validation rules to a request
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@FunctionalInterface
interface ValidationStrategy {

    /**
     * Validate request by applying validation rules
     *
     * @param validators list of validation rules
     * @param request request
     * @param <RequestT> request's type
     * @return list of validation errors (empty if there are not errors)
     */
    @Nonnull
    <RequestT> List<ValidationError> validate(@Nonnull List<ValidationRule<RequestT>> validators, @Nonnull RequestT request);

}
