package com.revolut.bank.application.api.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.bank.application.api.ApiMonetaryAmount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Request to create account
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Request to create account")
public class CreateAccountRequest {

    /**
     * Required balance of creating account
     */
    @ApiModelProperty(
            value = "Initial monetary balance",
            required = true
    )
    @JsonProperty("balance")
    private final ApiMonetaryAmount balance;

    @JsonCreator
    private CreateAccountRequest(
            @JsonProperty("balance") @Nonnull ApiMonetaryAmount balance
    ) {
        this.balance = balance;
    }

    @Nonnull
    public ApiMonetaryAmount getBalance() {
        return balance;
    }

    @Nonnull
    @Override
    public String toString() {
        return "CreateAccountRequest{" +
                "balance=" + balance +
                '}';
    }

    /**
     * Returns builder to construct {@link CreateAccountRequest}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link CreateAccountRequest} type
     */
    public static class Builder {

        private ApiMonetaryAmount balance;

        private Builder() {
        }

        @Nonnull
        public Builder withBalance(@Nonnull ApiMonetaryAmount balance) {
            this.balance = balance;
            return this;
        }

        @Nonnull
        public CreateAccountRequest build() {
            return new CreateAccountRequest(
                    requireNonNull(balance, "balance")
            );
        }

    }

}
