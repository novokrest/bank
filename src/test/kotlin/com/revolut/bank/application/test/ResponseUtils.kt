package com.revolut.bank.application.test

import com.revolut.bank.application.engine.error.ApplicationError
import com.revolut.bank.application.engine.error.ValidationError
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import javax.ws.rs.core.Response

/**
 * Extensions for [Response] class
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 24.03.2019
 */
object ResponseUtils {

    fun Response.shouldBeOk() =
            this.statusInfo shouldEqual Response.Status.OK

    fun Response.shouldBeBadRequest() =
            this.statusInfo shouldEqual Response.Status.BAD_REQUEST

    inline infix fun <reified T> Response.shouldHaveBody(assertion: T.() -> Unit) {
        val responseBody = this.readEntity(T::class.java)
        assertion(responseBody)
    }

    infix fun Response.shouldHaveValidationError(expectedError: ValidationError) {
        val errors = this.readEntity(com.revolut.bank.application.api.error.ApiErrorsResponse::class.java).errors
        errors.application.shouldBeNull()
        errors.validation.shouldNotBeNull()
        errors.validation!!.size shouldEqualTo 1
        val validationError = errors.validation!![0]
        validationError.paramName shouldBeEqualTo expectedError.paramName
        validationError.code shouldBeEqualTo expectedError.code
        validationError.message shouldBeEqualTo expectedError.message
    }

    infix fun Response.shouldHaveApplicationError(expectedError: ApplicationError) {
        val errors = this.readEntity(com.revolut.bank.application.api.error.ApiErrorsResponse::class.java).errors
        errors.validation.shouldBeNull()
        errors.application.shouldNotBeNull()
        val applicationError = errors.application!!
        applicationError.code shouldBeEqualTo expectedError.code
        applicationError.message shouldBeEqualTo expectedError.message
    }

}