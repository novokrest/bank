package com.revolut.bank.application.service

import com.nhaarman.mockitokotlin2.eq
import com.revolut.bank.application.domain.money.Currency
import com.revolut.bank.application.domain.money.MonetaryAmount
import com.revolut.bank.application.service.account.AccountManager
import com.revolut.bank.application.service.account.AccountStorage
import org.amshove.kluent.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class AccountManagerUnitTest {

    private lateinit var accountManager: AccountManager
    private lateinit var accountStorage: AccountStorage

    @BeforeMethod
    fun beforeMethod() {
        accountStorage = mock()
        accountManager = AccountManager(accountStorage, BigDecimal.ZERO, BigDecimal("10000000000"))
    }

    @Test
    fun `should create account and store in storage`() {
        // given
        val beforeAccountCreated = ZonedDateTime.now()
        val accountBalance = MonetaryAmount.builder()
                .withAmount(BigDecimal.TEN.setScale(2))
                .withCurrency(Currency.USD)
                .build()
        // when
        val result = accountManager.createAccount(accountBalance)

        // then
        result.isSuccess shouldEqualTo true
        val account = result.resultOrThrow
        (Verify on accountStorage).storeAccount(eq(account))
        account should {
            uid.asLong() shouldBeGreaterThan 0
            balance shouldEqual accountBalance
            createdAt.isBefore(beforeAccountCreated) shouldEqualTo false
            true
        }
    }

}