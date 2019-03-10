package com.revolut.bank.application.api.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.bank.application.domain.account.Uid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Response of command to create account
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Response about created account")
public class CreateAccountResponse {

    /**
     * UID of created account
     */
    @ApiModelProperty(
            value = "Created account's ID",
            example = "1000000001",
            required = true
    )
    @JsonProperty("account")
    private final Uid account;

    @JsonCreator
    private CreateAccountResponse(
            @JsonProperty("account") @Nonnull Uid account
    ) {
        this.account = requireNonNull(account, "account");
    }

    @Nonnull
    public Uid getAccount() {
        return account;
    }

    @Nonnull
    @Override
    public String toString() {
        return "CreateAccountResponse{" +
                "account=" + account +
                '}';
    }

    /**
     * Returns builder to construct {@link CreateAccountResponse}
     *
     * @return new builder instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helper class to construct instances of {@link CreateAccountResponse} type
     */
    public static class Builder {

        private Uid account;

        private Builder() {
        }

        @Nonnull
        public Builder withAccount(@Nonnull Uid account) {
            this.account = account;
            return this;
        }

        @Nonnull
        public CreateAccountResponse build() {
            return new CreateAccountResponse(
                    account
            );
        }

    }

}
