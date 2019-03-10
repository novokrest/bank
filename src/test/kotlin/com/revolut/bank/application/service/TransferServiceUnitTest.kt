package com.revolut.bank.application.service

import com.nhaarman.mockitokotlin2.eq
import com.revolut.bank.application.domain.TransferError
import com.revolut.bank.application.domain.account.Account
import com.revolut.bank.application.domain.account.Uid
import com.revolut.bank.application.domain.money.Currency
import com.revolut.bank.application.domain.money.MonetaryAmount
import com.revolut.bank.application.service.account.AccountLocker
import com.revolut.bank.application.service.account.AccountManager
import com.revolut.bank.application.service.transfer.TransferService
import org.amshove.kluent.When
import org.amshove.kluent.any
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional

class TransferServiceUnitTest {

    private lateinit var accountManager: AccountManager
    private lateinit var accountLocker: AccountLocker
    private lateinit var transferService: TransferService

    @BeforeMethod
    fun beforeMethod() {
        accountManager = mock()
        accountLocker = mock()
        transferService = TransferService(accountManager, accountLocker)
    }

    @Test
    fun `should return error when failed to acquire lock to transfer money`() {
        // given
        val sourceAccount = Uid.of(100000001)
        val destinationAccount = Uid.of(100000002)
        val amountToTransfer = MonetaryAmount.builder()
                .withAmount(BigDecimal.TEN)
                .withCurrency(Currency.USD)
                .build()

        When calling accountManager.getAccount(eq(sourceAccount)) itReturns createAccount(sourceAccount)
        When calling accountManager.getAccount(eq(destinationAccount)) itReturns createAccount(destinationAccount)
        When calling accountManager.isBalanceAllowable(any()) itReturns true
        When calling accountLocker.executeUnderLocks<Any>(any(), any(), any()) itReturns Optional.empty()

        // when
        val result = transferService.transferMoney(sourceAccount, destinationAccount, amountToTransfer)

        // then
        result.isError shouldEqualTo true
        result.errorOrThrow shouldEqual TransferError.ACCOUNT_BUSY
    }

    private fun createAccount(sourceAccount: Uid): Account {
        return Account.builder()
                .withUid(sourceAccount)
                .withBalance(MonetaryAmount.builder()
                        .withAmount(BigDecimal.TEN)
                        .withCurrency(Currency.USD)
                        .build())
                .withCreatedAt(ZonedDateTime.now())
                .build()
    }

}