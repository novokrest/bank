package com.revolut.bank.application.domain.account;

import com.revolut.bank.application.domain.money.MonetaryAmount;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Information about bank account
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class Account {

    /**
     * Account UID
     */
    private final Uid uid;

    /**
     * Account balance
     */
    private final MonetaryAmount balance;

    /**
     * Account creation date and time
     */
    private final ZonedDateTime createdAt;

    private Account(
            @Nonnull Uid uid,
            @Nonnull MonetaryAmount balance,
            @Nonnull ZonedDateTime createdAt
    ) {
        this.uid = requireNonNull(uid, "uid");
        this.balance = requireNonNull(balance, "balance");
        this.createdAt = requireNonNull(createdAt, "createdAt");
    }

    @Nonnull
    public Uid getUid() {
        return uid;
    }

    @Nonnull
    public MonetaryAmount getBalance() {
        return balance;
    }

    @Nonnull
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Account other = (Account) obj;
        return Objects.equals(uid, other.uid) &&
                Objects.equals(balance, other.balance) &&
                Objects.equals(createdAt, other.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, balance, createdAt);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Account{" +
                "uid=" + uid +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * Returns builder to construct {@link Account}
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
     * @param copy account to copy
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder(@Nonnull Account copy) {
        return new Builder()
                .withUid(copy.uid)
                .withBalance(copy.balance)
                .withCreatedAt(copy.createdAt);
    }

    /**
     * Helper class to construct instances of {@link Account} type
     */
    public static class Builder {

        private Uid uid;
        private MonetaryAmount balance;
        private ZonedDateTime createdAt;

        private Builder() {
        }

        @Nonnull
        public Builder withUid(@Nonnull Uid uid) {
            this.uid = uid;
            return this;
        }

        @Nonnull
        public Builder withBalance(@Nonnull MonetaryAmount balance) {
            this.balance = balance;
            return this;
        }

        @Nonnull
        public Builder withCreatedAt(@Nonnull ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public Account build() {
            return new Account(
                    uid,
                    balance,
                    createdAt
            );
        }

    }

}
