package com.revolut.bank.application.engine.error;

import javax.annotation.Nonnull;

/**
 * Common interface to represent validation error
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public interface ValidationError {

    /**
     * Returns name of parameter that was not passed validation
     *
     * @return parameter's name
     */
    @Nonnull
    String getParamName();

    /**
     * Returns validation error's code
     *
     * @return error's code
     */
    @Nonnull
    String getCode();

    /**
     * Returns validation error's message
     *
     * @return error's message
     */
    @Nonnull
    String getMessage();
}
