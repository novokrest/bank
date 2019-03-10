package com.revolut.bank.application.api.transfer;

import com.revolut.bank.application.engine.error.ApplicationError;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Enumeration of application errors of command to transfer money between accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public enum TransferMoneyApplicationError implements ApplicationError {

    /**
     * Currencies of requested accounts differ from each other
     */
    ACCOUNTS_CURRENCIES_NOT_SAME("AccountsCurrenciesNotSame", "Currencies of given accounts differs from each other"),

    /**
     * Currency of amount to transfer differs from requested accounts
     */
    TRANSFER_AMOUNT_CURRENCY_DIFFERS_FROM_ACCOUNTS("TransferAmountCurrencyDiffersFromAccounts",
            "Currency of amount to transfer differs from requested accounts"),

    /**
     * Insufficient balance on source account
     */
    INSUFFICIENT_SOURCE_BALANCE("InsufficientSourceBalance", "Insufficient balance on source account"),

    /**
     * Destination balance limit will be exceeded
     */
    DESTINATION_BALANCE_LIMIT_EXCEEDED("DestinationBalanceLimitExceeded", "Destination balance limit will be exceeded"),

    ;

    private final String code;
    private final String message;

    TransferMoneyApplicationError(@Nonnull String code, @Nonnull String message) {
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
