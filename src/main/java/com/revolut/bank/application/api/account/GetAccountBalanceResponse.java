package com.revolut.bank.application.api.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.bank.application.api.ApiMonetaryAmount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Response with information about account's balance
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Request to obtain account's balance")
public class GetAccountBalanceResponse {

    /**
     * Account balance
     */
    @ApiModelProperty(
            value = "Account's balance",
            required = true
    )
    @JsonProperty("balance")
    private final ApiMonetaryAmount balance;

    @JsonCreator
    public GetAccountBalanceResponse(
            @JsonProperty("balance") @Nonnull ApiMonetaryAmount balance
    ) {
        this.balance = requireNonNull(balance, "balance");
    }

    @Nonnull
    public ApiMonetaryAmount getBalance() {
        return balance;
    }

    @Nonnull
    @Override
    public String toString() {
        return "GetAccountBalanceResponse{" +
                "balance=" + balance +
                '}';
    }

}
