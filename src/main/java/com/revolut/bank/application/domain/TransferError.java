package com.revolut.bank.application.domain;

/**
 * Enumeration of errors occurred during money transfer
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public enum TransferError {

    /**
     * Transfer is impossible because currencies of participating accounts differ from each other
     */
    ACCOUNTS_CURRENCIES_NOT_SAME,

    /**
     * Transfer is impossible because currency of amount to transfer differs from participating accounts
     */
    TRANSFER_AMOUNT_CURRENCY_DIFFERS_FROM_ACCOUNTS,

    /**
     * Transfer is impossible because of insufficient balance on source account
     */
    INSUFFICIENT_SOURCE_BALANCE,

    /**
     * Transfer is impossible because of destination balance limit will be exceeded
     */
    DESTINATION_BALANCE_OVERFLOW,

    /**
     * Failed to lock accounts for transfer
     */
    ACCOUNT_BUSY,

    ;

}
