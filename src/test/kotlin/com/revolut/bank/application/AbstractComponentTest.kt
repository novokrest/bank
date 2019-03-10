package com.revolut.bank.application

import com.revolut.bank.application.api.ApiMonetaryAmount
import com.revolut.bank.application.api.account.CreateAccountRequest
import com.revolut.bank.application.api.account.CreateAccountResponse
import com.revolut.bank.application.api.account.GetAccountBalanceResponse
import com.revolut.bank.application.config.AppResourceConfig
import com.revolut.bank.application.config.AppSettings
import com.revolut.bank.application.domain.account.Uid
import com.revolut.bank.application.domain.money.Currency
import com.revolut.bank.application.domain.money.MonetaryAmount
import com.revolut.bank.application.test.ApiEndpoint
import com.revolut.bank.application.test.ResponseUtils.shouldBeOk
import org.glassfish.jersey.internal.inject.InjectionManager
import org.glassfish.jersey.server.spi.Container
import org.glassfish.jersey.server.spi.ContainerLifecycleListener
import org.glassfish.jersey.test.JerseyTestNg
import java.math.BigDecimal
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType

abstract class AbstractComponentTest : JerseyTestNg.ContainerPerClassTest() {

    protected lateinit var injectionManager: InjectionManager

    override fun configure(): Application = AppResourceConfig(TEST_APP_SETTINGS)
            .register(testLifecycleListener())

    private fun testLifecycleListener(): ContainerLifecycleListener {
        return object : ContainerLifecycleListener {
            override fun onStartup(container: Container?) {
                injectionManager = container!!.applicationHandler.injectionManager
            }

            override fun onReload(container: Container?) {

            }

            override fun onShutdown(container: Container?) {
            }
        }
    }

    protected inline fun <reified T> getInstance(): T = injectionManager.getInstance<T>(T::class.java)

    fun createAccountWithBalance(balance: BigDecimal, currency: Currency = DEFAULT_CURRENCY): Uid {
        val createAccountRequest = CreateAccountRequest.builder()
                .withBalance(ApiMonetaryAmount.builder()
                        .withAmount(balance)
                        .withCurrency(currency)
                        .build())
                .build()
        val response = target(ApiEndpoint.CREATE_ACCOUNT.path)
                .request()
                .post(Entity.entity(createAccountRequest, MediaType.APPLICATION_JSON_TYPE))

        response.shouldBeOk()
        return response.readEntity(CreateAccountResponse::class.java).account
    }

    fun getAccountBalance(accountUid: Uid): MonetaryAmount {
        val response = target(ApiEndpoint.GET_ACCOUNT_BALANCE.path)
                .resolveTemplate("uid", accountUid.asString())
                .request()
                .get()

        response.shouldBeOk()
        val balance = response.readEntity(GetAccountBalanceResponse::class.java).balance
        return MonetaryAmount.builder()
                .withAmount(balance.amount)
                .withCurrency(Currency.fromCode(balance.currency))
                .build()
    }

    protected companion object {
        val DEFAULT_CURRENCY = Currency.USD

        val TEST_APP_SETTINGS = AppSettings.builder()
                .withHost("localhost")
                .withPort(0)
                .withBasePath("bank")
                .withMinAccountBalance(BigDecimal.ZERO)
                .withMaxAccountBalance(BigDecimal("1000000000000000000"))
                .withCommandThreadsCount(100)
                .build()
    }

}