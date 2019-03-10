package com.revolut.bank.application.domain.money;

import com.revolut.bank.application.engine.core.Enums;
import java.util.Optional;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

/**
 * Enumeration of supported monetary currency
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public enum Currency implements Enums.StringRepr {

    /**
     * USD
     */
    USD("USD", 2),

    /**
     * EUR
     */
    EUR("EUR", 2),

    /**
     * RUB
     */
    RUB("RUB", 2),

    ;

    private final String code;
    private final int centsPower;

    Currency(@Nonnull String code, int centsPower) {
        this.code = requireNonNull(code);
        this.centsPower = centsPower;
    }

    @Nonnull
    public String getCode() {
        return code;
    }

    public int getCentsPower() {
        return centsPower;
    }

    @Nonnull
    public static Currency fromCode(@Nonnull String code) {
        return Enums.fromCode(code, Currency.class);
    }

    @Nonnull
    public static Optional<Currency> fromCodeOptional(@Nonnull String code) {
        return Enums.fromCodeOptional(code, Currency.class);
    }

}
