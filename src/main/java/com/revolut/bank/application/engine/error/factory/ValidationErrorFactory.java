package com.revolut.bank.application.engine.error.factory;

import com.revolut.bank.application.engine.error.ValidationError;
import javax.annotation.Nonnull;

/**
 * Factory to create {@link ValidationError} instances
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class ValidationErrorFactory {

    @Nonnull
    public static ValidationError paramNotProvided(@Nonnull String paramName) {
        return paramNotProvided(paramName, String.format("Parameter '%s' must be provided", paramName));
    }

    @Nonnull
    public static ValidationError paramNotProvided(@Nonnull String paramName, @Nonnull String message) {
        return ValidationErrorImpl.builder()
                .withParamName(paramName)
                .withCode(String.format("%sNotProvided", paramName))
                .withMessage(message)
                .build();
    }

    @Nonnull
    public static ValidationError paramEmpty(@Nonnull String paramName) {
        return ValidationErrorImpl.builder()
                .withParamName(paramName)
                .withCode(String.format("%sEmpty", paramName))
                .withMessage(String.format("Parameter '%s' should not be empty", paramName))
                .build();
    }


    @Nonnull
    public static ValidationError paramInvalid(@Nonnull String paramName) {
        return paramInvalid(paramName, String.format("Parameter '%s' is invalid", paramName));
    }

    @Nonnull
    public static ValidationError paramInvalid(@Nonnull String paramName, @Nonnull String message) {
        return ValidationErrorImpl.builder()
                .withParamName(paramName)
                .withCode(String.format("%sInvalid", paramName))
                .withMessage(message)
                .build();
    }

    /**
     * Default implementation of {@link ValidationError}
     */
    private static class ValidationErrorImpl implements ValidationError {

        private final String paramName;

        private final String code;

        private final String message;

        private ValidationErrorImpl(@Nonnull String paramName,
                                @Nonnull String code,
                                @Nonnull String message) {
            this.paramName = paramName;
            this.code = code;
            this.message = message;
        }

        @Nonnull
        public String getParamName() {
            return paramName;
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
            return "ValidationError{" +
                    "paramName='" + paramName + '\'' +
                    ", code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }

        /**
         * Returns builder to construct {@link ValidationErrorImpl}
         *
         * @return new builder instance
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Helper class to construct instances of {@link ValidationErrorImpl} type
         */
        public static class Builder {

            private String paramName;
            private String code;
            private String message;

            private Builder() {
            }

            @Nonnull
            public Builder withParamName(@Nonnull String paramName) {
                this.paramName = paramName;
                return this;
            }

            @Nonnull
            public Builder withCode(@Nonnull String code) {
                this.code = code;
                return this;
            }

            @Nonnull
            public Builder withMessage(@Nonnull String message) {
                this.message = message;
                return this;
            }

            /**
             * Создает новый объект типа {@link ValidationError}
             *
             * @return новый объект типа {@link ValidationError}
             */
            @Nonnull
            public ValidationErrorImpl build() {
                return new ValidationErrorImpl(
                        paramName,
                        code,
                        message
                );
            }
        }


    }
}
