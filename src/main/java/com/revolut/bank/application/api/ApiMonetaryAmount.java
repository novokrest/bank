package com.revolut.bank.application.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.bank.application.domain.money.Currency;

import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

/**
 * Amount of money with currency
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class ApiMonetaryAmount {

    /**
     * Amount of money
     */
    @ApiModelProperty(
            value = "Monetary amount",
            example = "250.00",
            required = true
    )
    @JsonProperty("amount")
    private final BigDecimal amount;

    /**
     * Monetary currency
     */
    @ApiModelProperty(
            value = "Monetary currency",
            example = "USD",
            allowableValues = "USD,EUR,RUB",
            required = true
    )
    @JsonProperty("currency")
    private final String currency;

    @JsonCreator
    private ApiMonetaryAmount(
            @JsonProperty("amount") @Nonnull BigDecimal amount,
            @JsonProperty("currency") @Nonnull String currency
    ) {
        this.amount = amount;
        this.currency = currency;
    }

    @Nonnull
    public BigDecimal getAmount() {
        return amount;
    }

    @Nonnull
    public String getCurrency() {
        return currency;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ApiMonetaryAmount{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }

    /**
     * Returns builder to construct {@link ApiMonetaryAmount}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link ApiMonetaryAmount} type
     */
    public static class Builder {

        private BigDecimal amount;
        private String currency;

        private Builder() {
        }

        @Nonnull
        public Builder withAmount(@Nonnull BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        @Nonnull
        public Builder withCurrency(@Nonnull Currency currency) {
            this.currency = currency.getCode();
            return this;
        }

        @Nonnull
        public ApiMonetaryAmount build() {
            return new ApiMonetaryAmount(
                    requireNonNull(amount, "amount"),
                    requireNonNull(currency, "currency")
            );
        }

    }

}
