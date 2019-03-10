package com.revolut.bank.application.test

/**
 * Enumeration of API endpoint to use in tests
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 24.03.2019
 */
enum class ApiEndpoint(val path: String) {

    CREATE_ACCOUNT("/api/account/create"),

    GET_ACCOUNT_BALANCE("/api/account/{uid}/balance"),

    TRANSFER_MONEY("/api/transfer"),

}