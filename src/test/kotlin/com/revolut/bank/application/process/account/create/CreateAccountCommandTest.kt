package com.revolut.bank.application.process.account.create

import com.revolut.bank.application.AbstractComponentTest
import com.revolut.bank.application.api.ApiMonetaryAmount
import com.revolut.bank.application.api.account.CreateAccountApplicationError
import com.revolut.bank.application.api.account.CreateAccountRequest
import com.revolut.bank.application.api.account.CreateAccountResponse
import com.revolut.bank.application.domain.account.Uid
import com.revolut.bank.application.domain.money.Currency
import com.revolut.bank.application.engine.error.ValidationError
import com.revolut.bank.application.engine.error.factory.ValidationErrorFactory
import com.revolut.bank.application.test.ApiEndpoint
import com.revolut.bank.application.test.ResponseUtils.shouldBeBadRequest
import com.revolut.bank.application.test.ResponseUtils.shouldBeOk
import com.revolut.bank.application.test.ResponseUtils.shouldHaveApplicationError
import com.revolut.bank.application.test.ResponseUtils.shouldHaveBody
import com.revolut.bank.application.test.ResponseUtils.shouldHaveValidationError
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.math.BigDecimal
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class CreateAccountCommandTest : AbstractComponentTest() {

    @Test(dataProvider = "invalidRequests")
    fun `should return validation error when request is invalid`(request: String, error: ValidationError) {
        // when
        val response: Response = target(ApiEndpoint.CREATE_ACCOUNT.path)
                .request()
                .post(Entity.json(request))

        // then
        response.shouldBeBadRequest()
        response shouldHaveValidationError error
    }

    @DataProvider
    fun invalidRequests() = arrayOf(
            arrayOf(
                    "{}",
                    ValidationErrorFactory.paramNotProvided("balance")
            ),
            arrayOf(
                    "{ \"balance\": { \"currency\": \"USD\" } }",
                    ValidationErrorFactory.paramInvalid("balance", "Balance sum must be provided")
            ),
            arrayOf(
                    "{ \"balance\": { \"amount\": -100.00, \"currency\": \"USD\" } }",
                    ValidationErrorFactory.paramInvalid("balance", "Balance must be non-negative")
            ),
            arrayOf(
                    "{ \"balance\": { \"amount\": 100.0, \"currency\": \"EUR\" } }",
                    ValidationErrorFactory.paramInvalid("balance", "Balance sum must have 2 decimal places")
            ),
            arrayOf(
                    "{ \"balance\": { \"amount\": 100.00} }",
                    ValidationErrorFactory.paramInvalid("balance", "Balance currency must be provided")
            ),
            arrayOf(
                    "{ \"balance\": { \"amount\": 500.00, \"currency\": \"CNY\" } }",
                    ValidationErrorFactory.paramInvalid("balance", "Balance currency is not supported")
            )
    )

    @Test
    fun `should return error when required balance is too high`() {
        // given
        val tooHighBalanceAmount = BigDecimal("100000000000000000000000000000000000000000.00")

        val requestEntity = Entity.entity(
                CreateAccountRequest.builder()
                        .withBalance(ApiMonetaryAmount.builder()
                                .withAmount(tooHighBalanceAmount)
                                .withCurrency(Currency.USD)
                                .build())
                        .build(),
                MediaType.APPLICATION_JSON_TYPE
        )

        // when
        val response = target(ApiEndpoint.CREATE_ACCOUNT.path)
                .request()
                .post(requestEntity)

        // then
        response.shouldBeOk()
        response shouldHaveApplicationError CreateAccountApplicationError.BALANCE_TO_HIGH
    }

    @Test
    fun `should create new account when request is valid`() {
        // given
        val balanceAmount = BigDecimal.valueOf(500).setScale(2)
        val currency = Currency.USD
        val requestEntity = Entity.entity(
                CreateAccountRequest.builder()
                        .withBalance(ApiMonetaryAmount.builder()
                                .withAmount(balanceAmount)
                                .withCurrency(currency)
                                .build())
                        .build(),
                MediaType.APPLICATION_JSON_TYPE
        )

        // when
        val createAccountResponse = target(ApiEndpoint.CREATE_ACCOUNT.path)
                .request()
                .post(requestEntity)

        // then
        createAccountResponse.shouldBeOk()

        var accountUid: Uid? = null
        createAccountResponse.shouldHaveBody<CreateAccountResponse> {
            account.shouldNotBeNull()
            account.asLong() shouldBeGreaterThan 0

            accountUid = account
        }

        // when
        val balance = getAccountBalance(accountUid!!)

        // then
        balance.amount shouldEqualTo balanceAmount
        balance.currency shouldEqual currency
    }

}