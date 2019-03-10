package com.revolut.bank.application.engine.core;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static java.util.Objects.requireNonNull;

/**
 * Classes with helper interfaces and method for enumerations
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public final class Enums {

    /**
     * Interface for enumeration backed by scalar value
     * @param <T> scalar type
     */
    @FunctionalInterface
    public interface ScalarRepr<T> {

        /**
         * Returns backed scalar value
         *
         * @return scalar value
         */
        @Nonnull
        T getCode();
    }

    /**
     * Interface for enumeration backed by string value
     */
    @FunctionalInterface
    public interface StringRepr extends ScalarRepr<String> {

    }

    @Nonnull
    public static <T, EnumT extends Enum<EnumT> & ScalarRepr<T>>
    List<EnumT> fromCollection(@Nonnull Collection<T> codes, @Nonnull Class<EnumT> enumClass) {
        requireNonNull(codes, "codes");
        return codes.stream().map(code -> Enums.fromCode(code, enumClass)).collect(Collectors.toList());
    }

    @Nonnull
    public static <T, EnumT extends Enum<EnumT> & ScalarRepr<T>>
    EnumT fromCode(@Nonnull T code, @Nonnull Class<EnumT> enumClass) {
        requireNonNull(code, "code");
        requireNonNull(enumClass, "enumClass");
        return findByCode(code, enumClass).orElseThrow(() -> new RuntimeException("Enum value was not found: code=" + code));
    }

    @Nonnull
    public static <T, EnumT extends Enum<EnumT> & ScalarRepr<T>>
    Optional<EnumT> fromCodeOptional(@Nullable T code, @Nonnull Class<EnumT> enumClass) {
        requireNonNull(enumClass, "enumClass");
        return Objects.isNull(code) ? Optional.empty() : findByCode(code, enumClass);
    }

    @Nonnull
    private static <T, EnumT extends Enum<EnumT> & ScalarRepr<T>>
    Optional<EnumT> findByCode(@Nonnull T code, @Nonnull Class<EnumT> enumClass) {
        for (EnumT value: enumClass.getEnumConstants()) {
            if (Objects.equals(value.getCode(), code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    private Enums() {

    }

}
