package com.revolut.bank.application.process.account.balance

import com.revolut.bank.application.AbstractComponentTest
import com.revolut.bank.application.api.account.GetAccountBalanceApplicationError
import com.revolut.bank.application.api.account.GetAccountBalanceResponse
import com.revolut.bank.application.domain.account.Uid
import com.revolut.bank.application.engine.error.factory.ValidationErrorFactory
import com.revolut.bank.application.test.ApiEndpoint
import com.revolut.bank.application.test.ResponseUtils.shouldBeBadRequest
import com.revolut.bank.application.test.ResponseUtils.shouldBeOk
import com.revolut.bank.application.test.ResponseUtils.shouldHaveApplicationError
import com.revolut.bank.application.test.ResponseUtils.shouldHaveBody
import com.revolut.bank.application.test.ResponseUtils.shouldHaveValidationError
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqualTo
import org.testng.annotations.Test
import java.math.BigDecimal

class GetAccountBalanceCommandTest : AbstractComponentTest() {

    @Test
    fun `should return error when account UID is invalid`() {
        // given
        val invalidAccountUid = "invalid"

        // when
        val response = target(ApiEndpoint.GET_ACCOUNT_BALANCE.path)
                .resolveTemplate("uid", invalidAccountUid)
                .request()
                .get()

        // then
        response.shouldBeBadRequest()
        response shouldHaveValidationError ValidationErrorFactory.paramInvalid("uid")
    }

    @Test
    fun `should return error when account was not found`() {
        // given
        val unknownAccountUid = Uid.of(10)

        // when
        val response = target(ApiEndpoint.GET_ACCOUNT_BALANCE.path)
                .resolveTemplate("uid", unknownAccountUid.asString())
                .request()
                .get()

        // then
        response.shouldBeOk()
        response shouldHaveApplicationError GetAccountBalanceApplicationError.ACCOUNT_NOT_FOUND
    }

    @Test
    fun `should return balance successfully when account exists`() {
        // given
        val accountBalanceAmount = BigDecimal.TEN.setScale(2)
        val accountUid = createAccountWithBalance(accountBalanceAmount)

        // when
        val response = target(ApiEndpoint.GET_ACCOUNT_BALANCE.path)
                .resolveTemplate("uid", accountUid.asString())
                .request()
                .get()

        // then
        response.shouldBeOk()
        response.shouldHaveBody<GetAccountBalanceResponse> {
            balance.amount shouldEqualTo accountBalanceAmount
            balance.currency shouldBeEqualTo DEFAULT_CURRENCY.code
        }
    }

}