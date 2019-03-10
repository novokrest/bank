package com.revolut.bank.application.api.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.bank.application.api.ApiMonetaryAmount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Request to transfer money between accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Request to transfer money between accounts")
public class TransferMoneyRequest {

    /**
     * UID of source account
     */
    @ApiModelProperty(
            value = "Source account's ID",
            example = "1000000001",
            required = true
    )
    @JsonProperty("source")
    private final String source;

    /**
     * UID of destination account
     */
    @ApiModelProperty(
            value = "Destination account's ID",
            example = "1000000002",
            required = true
    )
    @JsonProperty("destination")
    private final String destination;

    /**
     * Amount of money to transfer
     */
    @ApiModelProperty(
            value = "Amount to transfer",
            required = true
    )
    @JsonProperty("amount")
    private final ApiMonetaryAmount amount;

    @JsonCreator
    private TransferMoneyRequest(
            @JsonProperty("source") @Nonnull String source,
            @JsonProperty("destination") @Nonnull String destination,
            @JsonProperty("amount") @Nonnull ApiMonetaryAmount amount
    ) {
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    @Nonnull
    public String getSource() {
        return source;
    }

    @Nonnull
    public String getDestination() {
        return destination;
    }

    @Nonnull
    public ApiMonetaryAmount getAmount() {
        return amount;
    }

    @Nonnull
    @Override
    public String toString() {
        return "TransferMoneyRequest{" +
                "source=" + source +
                ", destination=" + destination +
                ", amount=" + amount +
                '}';
    }

    /**
     * Returns builder to construct {@link TransferMoneyRequest}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link TransferMoneyRequest} type
     */
    public static class Builder {

        private String source;
        private String destination;
        private ApiMonetaryAmount amount;

        private Builder() {
        }

        @Nonnull
        public Builder withSource(@Nonnull String source) {
            this.source = source;
            return this;
        }

        @Nonnull
        public Builder withDestination(@Nonnull String destination) {
            this.destination = destination;
            return this;
        }

        @Nonnull
        public Builder withAmount(@Nonnull ApiMonetaryAmount amount) {
            this.amount = amount;
            return this;
        }

        @Nonnull
        public TransferMoneyRequest build() {
            return new TransferMoneyRequest(
                    requireNonNull(source, "source"),
                    requireNonNull(destination, "destination"),
                    requireNonNull(amount, "amount")
            );
        }

    }
    
}
