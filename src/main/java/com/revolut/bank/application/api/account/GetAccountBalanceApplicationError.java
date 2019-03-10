package com.revolut.bank.application.api.account;

import com.revolut.bank.application.engine.error.ApplicationError;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Enumeration of application error of command to get account's balance
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public enum GetAccountBalanceApplicationError implements ApplicationError {

    /**
     * Failed to get balance because account was not found by UID
     */
    ACCOUNT_NOT_FOUND("AccountNotFound", "Account was not found"),

    ;

    private final String code;
    private final String message;

    GetAccountBalanceApplicationError(@Nonnull String code, @Nonnull String message) {
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
