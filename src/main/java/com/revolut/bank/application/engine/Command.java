package com.revolut.bank.application.engine;

import com.revolut.bank.application.engine.validation.ValidationRules;
import javax.annotation.Nonnull;

/**
 * Common interface for API commands
 *
 * @param <RequestT> request's type
 * @param <ResponseT> response's type
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public interface Command<RequestT, ResponseT> {

    /**
     * Process request and returns result
     *
     * @param request request
     * @return result of processing request
     */
    @Nonnull
    CommandResult<ResponseT> execute(@Nonnull RequestT request);

    /**
     * Returns default validation rules
     *
     * @return empty validation rules
     */
    @Nonnull
    default ValidationRules<RequestT> getValidationRules() {
        return ValidationRules.emptyValidationRules();
    }

}
