package com.revolut.bank.application.domain.money;

import com.revolut.bank.application.api.ApiMonetaryAmount;
import java.math.BigDecimal;
import java.util.Objects;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Amount of money with currency
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class MonetaryAmount {

    /**
     * Amount of money
     */
    private final BigDecimal amount;

    /**
     * Monetary currency
     */
    private final Currency currency;

    private MonetaryAmount(
            @Nonnull BigDecimal amount,
            @Nonnull Currency currency
    ) {
        this.amount = requireNonNull(amount, "amount");
        this.currency = requireNonNull(currency, "currency");
    }

    @Nonnull
    public BigDecimal getAmount() {
        return amount;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Add amount to given
     *
     * @param addedAmount amount to add
     * @return new monetary amount with sum of amounts
     */
    @Nonnull
    public MonetaryAmount add(@Nonnull BigDecimal addedAmount) {
        return MonetaryAmount.builder()
                .withAmount(amount.add(addedAmount))
                .withCurrency(currency)
                .build();
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MonetaryAmount other = (MonetaryAmount) obj;
        return Objects.equals(amount, other.amount) &&
                Objects.equals(currency, other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Nonnull
    @Override
    public String toString() {
        return "MonetaryAmount{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }

    /**
     * Returns builder to construct {@link MonetaryAmount}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns builder with data from given account
     *
     * @param copy amount to copy
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder(@Nonnull MonetaryAmount copy) {
        Builder builder = new Builder();
        builder.amount = copy.amount;
        builder.currency = copy.currency;
        return builder;
    }

    /**
     * Helper class to construct instances of {@link ApiMonetaryAmount} type
     */
    public static class Builder {

        private BigDecimal amount;
        private Currency currency;

        private Builder() {
        }

        @Nonnull
        public Builder withAmount(@Nonnull BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        @Nonnull
        public Builder withCurrency(@Nonnull Currency currency) {
            this.currency = currency;
            return this;
        }

        @Nonnull
        public MonetaryAmount build() {
            return new MonetaryAmount(
                    amount,
                    currency
            );
        }

    }


}
