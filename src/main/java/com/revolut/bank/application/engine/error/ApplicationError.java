package com.revolut.bank.application.engine.error;

import javax.annotation.Nonnull;

/**
 * Common interface to represent application error
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public interface ApplicationError {

    /**
     * Returns error's code
     *
     * @return error's code
     */
    @Nonnull
    String getCode();

    /**
     * Returns error's message
     *
     * @return error's message
     */
    @Nonnull
    String getMessage();
}
