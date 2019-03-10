package com.revolut.bank.application.api.transfer;

import com.fasterxml.jackson.annotation.JsonValue;
import com.revolut.bank.application.engine.core.Enums;
import io.swagger.annotations.ApiModel;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Enumeration of statuses of money transfer between accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel(description = "Transfer status")
public enum TransferStatus implements Enums.StringRepr{

    /**
     * Money transfer was succeeded
     */
    SUCCESS("Success"),

    ;

    private final String code;

    TransferStatus(@Nonnull String code) {
        this.code = requireNonNull(code);
    }

    @JsonValue
    @Nonnull
    @Override
    public String getCode() {
        return code;
    }

}
