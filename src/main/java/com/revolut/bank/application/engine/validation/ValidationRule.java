package com.revolut.bank.application.engine.validation;

import com.revolut.bank.application.engine.error.ValidationError;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represent rule for validating request
 *
 * @param <RequestT> request's type
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@FunctionalInterface
public interface ValidationRule<RequestT> {

    /**
     * Validate request
     *
     * @param request request
     * @return {@link Optional#empty()}, if request is valid,
     *         {@link Optional} with validation error - otherwise
     */
    @Nonnull
    Optional<ValidationError> validate(@Nonnull RequestT request);

}
