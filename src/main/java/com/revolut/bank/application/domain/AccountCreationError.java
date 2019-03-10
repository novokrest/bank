package com.revolut.bank.application.domain;

/**
 * Enumeration of errors that can occur during account creation
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public enum AccountCreationError {

    /**
     * Failed to create account because initial balance is too high
     */
    BALANCE_TO_HIGH

}
