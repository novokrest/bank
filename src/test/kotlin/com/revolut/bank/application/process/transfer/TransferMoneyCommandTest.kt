package com.revolut.bank.application.process.transfer

import com.revolut.bank.application.AbstractComponentTest
import com.revolut.bank.application.api.ApiMonetaryAmount
import com.revolut.bank.application.api.transfer.TransferMoneyApplicationError
import com.revolut.bank.application.api.transfer.TransferMoneyRequest
import com.revolut.bank.application.api.transfer.TransferMoneyResponse
import com.revolut.bank.application.api.transfer.TransferStatus
import com.revolut.bank.application.domain.money.Currency
import com.revolut.bank.application.engine.error.ValidationError
import com.revolut.bank.application.engine.error.factory.ValidationErrorFactory
import com.revolut.bank.application.test.ApiEndpoint
import com.revolut.bank.application.test.ResponseUtils.shouldBeBadRequest
import com.revolut.bank.application.test.ResponseUtils.shouldBeOk
import com.revolut.bank.application.test.ResponseUtils.shouldHaveApplicationError
import com.revolut.bank.application.test.ResponseUtils.shouldHaveBody
import com.revolut.bank.application.test.ResponseUtils.shouldHaveValidationError
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.math.BigDecimal
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class TransferMoneyCommandTest : AbstractComponentTest() {

    @Test(dataProvider = "invalidRequests")
    fun `should return validation error when request is invalid`(request: String, error: ValidationError) {
        // when
        val response: Response = target(ApiEndpoint.TRANSFER_MONEY.path)
                .request()
                .post(Entity.json(request))

        // then
        response.shouldBeBadRequest()
        response shouldHaveValidationError error
    }

    @DataProvider
    fun invalidRequests() = arrayOf(
            arrayOf(
                    "{\"destination\":\"1000000002\",\"amount\":{\"amount\":10.00,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramNotProvided("source")
            ),
            arrayOf(
                    "{\"source\":\"invalid\",\"destination\":\"1000000002\",\"amount\":{\"amount\":10.00,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramInvalid("source")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"amount\":{\"amount\":10.00,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramNotProvided("destination")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"invalid\",\"amount\":{\"amount\":10.00,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramInvalid("destination")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000002\"}",
                    ValidationErrorFactory.paramNotProvided("amount")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000002\",\"amount\":{\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramInvalid("amount", "Amount sum must be provided")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000002\",\"amount\":{\"amount\":-10.00,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramInvalid("amount", "Amount to transfer must be positive")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000002\",\"amount\":{\"amount\":10.00}}",
                    ValidationErrorFactory.paramInvalid("amount", "Amount currency must be provided")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000002\",\"amount\":{\"amount\":10.00,\"currency\":\"CNY\"}}",
                    ValidationErrorFactory.paramInvalid("amount", "Amount currency is not supported")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000002\",\"amount\":{\"amount\":10.000,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramInvalid("amount", "Amount to transfer must have 2 decimal places")
            ),
            arrayOf(
                    "{\"source\":\"1000000001\",\"destination\":\"1000000001\",\"amount\":{\"amount\":10.00,\"currency\":\"USD\"}}",
                    ValidationErrorFactory.paramInvalid("destination", "Destination account must differ from source")
            )
    )

    @Test(dataProvider = "moneyTransferDataSet")
    fun `should transfer money between accounts successfully`(
            originalSourceBalance: BigDecimal,
            originalDestinationBalance: BigDecimal,
            amountToTransfer: BigDecimal,
            expectedFinalSourceBalance: BigDecimal,
            expectedFinalDestinationBalance: BigDecimal
    ) {
        // given
        val sourceAccount = createAccountWithBalance(originalSourceBalance)
        val destinationAccount = createAccountWithBalance(originalDestinationBalance)

        val transferRequest = TransferMoneyRequest.builder()
                .withSource(sourceAccount.asString())
                .withDestination(destinationAccount.asString())
                .withAmount(ApiMonetaryAmount.builder()
                        .withAmount(amountToTransfer)
                        .withCurrency(DEFAULT_CURRENCY)
                        .build())
                .build()

        // when
        val response = target(ApiEndpoint.TRANSFER_MONEY.path)
                .request()
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))

        // then
        response.shouldBeOk()
        response.shouldHaveBody<TransferMoneyResponse> {
            status shouldEqual TransferStatus.SUCCESS
        }

        val actualFinalSourceBalance = getAccountBalance(sourceAccount).amount
        val actualFinalDestinationBalance = getAccountBalance(destinationAccount).amount

        actualFinalSourceBalance shouldEqualTo expectedFinalSourceBalance
        actualFinalDestinationBalance shouldEqualTo expectedFinalDestinationBalance
    }

    @DataProvider
    fun moneyTransferDataSet() = arrayOf(
            arrayOf(
                    BigDecimal("100.00"), BigDecimal("0.00"),
                    BigDecimal("10.00"),
                    BigDecimal("90.00"), BigDecimal("10.00")
            ),
            arrayOf(
                    BigDecimal("100.00"), BigDecimal("0.00"),
                    BigDecimal("100.00"),
                    BigDecimal("0.00"), BigDecimal("100.00")
            ),
            arrayOf(
                    BigDecimal("100.00"), BigDecimal("200.00"),
                    BigDecimal("70.00"),
                    BigDecimal("30.00"), BigDecimal("270.00")
            )
    )

    @Test
    fun `should return error when there is not enough money on source account's balance`() {
        // given
        val sourceBalance = BigDecimal(100.00).setScale(2)
        val sourceAccount = createAccountWithBalance(sourceBalance)
        val destinationAccount = createAccountWithBalance(BigDecimal.ZERO.setScale(2))

        val moneyToTransfer = sourceBalance.multiply(BigDecimal.TEN)
        val transferRequest = TransferMoneyRequest.builder()
                .withSource(sourceAccount.asString())
                .withDestination(destinationAccount.asString())
                .withAmount(ApiMonetaryAmount.builder()
                        .withAmount(moneyToTransfer)
                        .withCurrency(DEFAULT_CURRENCY)
                        .build())
                .build()

        // when
        val response = target(ApiEndpoint.TRANSFER_MONEY.path)
                .request()
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))

        // then
        response.shouldBeOk()
        response shouldHaveApplicationError TransferMoneyApplicationError.INSUFFICIENT_SOURCE_BALANCE
    }

    @Test
    fun `should return error when there is too much money on destination account's balance`() {
        // given
        val sourceBalance = BigDecimal(100.00).setScale(2)
        val sourceAccount = createAccountWithBalance(sourceBalance)
        val destinationAccount = createAccountWithBalance(BigDecimal("1000000000000000000").subtract(BigDecimal.ONE).setScale(2))

        val transferRequest = TransferMoneyRequest.builder()
                .withSource(sourceAccount.asString())
                .withDestination(destinationAccount.asString())
                .withAmount(ApiMonetaryAmount.builder()
                        .withAmount(sourceBalance)
                        .withCurrency(DEFAULT_CURRENCY)
                        .build())
                .build()

        // when
        val response = target(ApiEndpoint.TRANSFER_MONEY.path)
                .request()
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))

        // then
        response.shouldBeOk()
        response shouldHaveApplicationError TransferMoneyApplicationError.DESTINATION_BALANCE_LIMIT_EXCEEDED
    }

    @Test
    fun `should return error when requested accounts' currencies differ`() {
        // given
        val balance = BigDecimal(100.00).setScale(2)
        val sourceAccount = createAccountWithBalance(balance, currency = Currency.USD)
        val destinationAccount = createAccountWithBalance(balance, currency = Currency.EUR)

        val transferRequest = TransferMoneyRequest.builder()
                .withSource(sourceAccount.asString())
                .withDestination(destinationAccount.asString())
                .withAmount(ApiMonetaryAmount.builder()
                        .withAmount(BigDecimal(10).setScale(2))
                        .withCurrency(Currency.USD)
                        .build())
                .build()

        // when
        val response = target(ApiEndpoint.TRANSFER_MONEY.path)
                .request()
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))

        // then
        response.shouldBeOk()
        response shouldHaveApplicationError TransferMoneyApplicationError.ACCOUNTS_CURRENCIES_NOT_SAME
    }

    @Test
    fun `should return error when transfer currency differs from accounts' currencies`() {
        // given
        val balance = BigDecimal(100.00).setScale(2)
        val sourceAccount = createAccountWithBalance(balance, currency = Currency.USD)
        val destinationAccount = createAccountWithBalance(balance, currency = Currency.USD)

        val transferRequest = TransferMoneyRequest.builder()
                .withSource(sourceAccount.asString())
                .withDestination(destinationAccount.asString())
                .withAmount(ApiMonetaryAmount.builder()
                        .withAmount(BigDecimal(10).setScale(2))
                        .withCurrency(Currency.RUB)
                        .build())
                .build()

        // when
        val response = target(ApiEndpoint.TRANSFER_MONEY.path)
                .request()
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))

        // then
        response.shouldBeOk()
        response shouldHaveApplicationError TransferMoneyApplicationError.TRANSFER_AMOUNT_CURRENCY_DIFFERS_FROM_ACCOUNTS
    }

}