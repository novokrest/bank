package com.revolut.bank.application.service

import com.revolut.bank.application.AbstractComponentTest
import com.revolut.bank.application.domain.account.Uid
import com.revolut.bank.application.domain.money.Currency
import com.revolut.bank.application.domain.money.MonetaryAmount
import com.revolut.bank.application.service.account.AccountManager
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.math.BigDecimal

class AccountManagerTest : AbstractComponentTest() {

    private lateinit var accountManager: AccountManager

    @BeforeMethod
    fun beforeMethod() {
        accountManager = getInstance()
    }

    @Test
    fun `should return account successfully when account exists`() {
        // given
        val originalAccount = accountManager.createAccount(
                MonetaryAmount.builder()
                        .withAmount(BigDecimal.ZERO)
                        .withCurrency(Currency.USD)
                        .build()
        ).resultOrThrow

        // when
        val foundAccount = accountManager.getAccount(originalAccount.uid)

        // then
        foundAccount shouldEqual originalAccount
    }

    @Test(expectedExceptions = [RuntimeException::class], expectedExceptionsMessageRegExp = "Account was not found: uid=\\d+")
    fun `should throw exception when account does not exist`() {
        // given
        val unknownAccountUid = Uid.of(10)

        // expect exception
        accountManager.getAccount(unknownAccountUid)
    }

    @Test
    fun `should return empty Optional when account does not exist`() {
        // given
        val unknownAccountUid = Uid.of(10)

        // when
        val foundAccount = accountManager.findAccount(unknownAccountUid)

        //then
        foundAccount.isPresent shouldEqualTo false
    }

    @Test
    fun `should update account's balance successfully`() {
        // given
        val originalAccount = accountManager.createAccount(
                MonetaryAmount.builder()
                        .withAmount(BigDecimal.ZERO)
                        .withCurrency(Currency.USD)
                        .build()
        ).resultOrThrow

        val newBalance = originalAccount.balance.add(BigDecimal.TEN)

        // when
        accountManager.updateAccount(originalAccount, newBalance)

        // then
        val actualAccount = accountManager.getAccount(originalAccount.uid)
        actualAccount.balance shouldEqual newBalance
        actualAccount.createdAt shouldEqual originalAccount.createdAt
    }

}