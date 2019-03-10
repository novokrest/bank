package com.revolut.bank.application.api.account;

import com.revolut.bank.application.engine.error.ApplicationError;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Enumeration of application error of command to create account
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public enum CreateAccountApplicationError implements ApplicationError {

    /**
     * Required account's balance is too high
     */
    BALANCE_TO_HIGH("BalanceTooHigh", "Balance is too high"),

    ;

    private final String code;
    private final String message;

    CreateAccountApplicationError(@Nonnull String code, @Nonnull String message) {
        this.code = requireNonNull(code);
        this.message = requireNonNull(message);
    }

    @Nonnull
    @Override
    public String getCode() {
        return code;
    }

    @Nonnull
    @Override
    public String getMessage() {
        return message;
    }

}
