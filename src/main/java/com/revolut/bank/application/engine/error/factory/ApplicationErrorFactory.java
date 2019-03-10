package com.revolut.bank.application.engine.error.factory;

import com.revolut.bank.application.engine.error.ApplicationError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Factory to create {@link ApplicationError} instances
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class ApplicationErrorFactory {

    private static final ApplicationError TECHNICAL_ERROR = fromCode("TechnicalError", "Technical error");

    /**
     * Return object to represent technical error
     *
     * @return technical error
     */
    @Nonnull
    public static ApplicationError technicalError() {
        return TECHNICAL_ERROR;
    }

    /**
     * Creates {@link ApplicationError} with given code and message
     *
     * @param code error code
     * @param message error message
     * @return application error
     */
    @Nonnull
    public static ApplicationError fromCode(@Nonnull String code, @Nullable String message) {
        return new ApplicationErrorImpl(code, message);
    }

    /**
     * Default implementation of {@link ApplicationError}
     */
    private static class ApplicationErrorImpl implements ApplicationError {

        private final String code;

        private final String message;

        private ApplicationErrorImpl(@Nonnull String code, @Nonnull String message) {
            this.code = requireNonNull(code);
            this.message = requireNonNull(message);
        }

        @Nonnull
        public String getCode() {
            return code;
        }

        @Nonnull
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "ApplicationError{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }

    }
}
