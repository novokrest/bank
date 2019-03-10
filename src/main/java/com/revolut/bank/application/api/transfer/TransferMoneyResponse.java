package com.revolut.bank.application.api.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Response about status of money transfer between accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Response money transfer status")
public class TransferMoneyResponse {

    /**
     * Status of money transfer
     */
    @ApiModelProperty(
            value = "Transfer status",
            example = "Success",
            required = true
    )
    @JsonProperty("status")
    private final TransferStatus status;

    @JsonCreator
    public TransferMoneyResponse(
            @JsonProperty("status") @Nonnull TransferStatus status
    ) {
        this.status = requireNonNull(status, "status");
    }

    @Nonnull
    public TransferStatus getStatus() {
        return status;
    }

    @Nonnull
    @Override
    public String toString() {
        return "TransferMoneyResponse{" +
                "status=" + status +
                '}';
    }

}
