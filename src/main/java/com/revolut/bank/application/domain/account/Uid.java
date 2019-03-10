package com.revolut.bank.application.domain.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Unique identifier of bank account
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@ApiModel("Account ID")
public class Uid {

    /**
     * Identifier value
     */
    private final Long value;

    /**
     * Returns {@link Uid} instance based on given value
     *
     * @param uid value
     * @return new {@link Uid} instance
     */
    @Nonnull
    public static Uid of(@Nonnull String uid) {
        return new Uid(uid);
    }

    /**
     * Returns {@link Uid} instance based on given value
     *
     * @param uid value
     * @return new {@link Uid} instance
     */
    @Nonnull
    public static Uid of(@Nonnull Long uid) {
        return new Uid(uid);
    }

    /**
     * Check if uid is valid
     *
     * @param uid uid
     * @return {@code true} if uid is valid,
     *         {@code false} otherwise
     */
    public static boolean isValid(@Nullable String uid) {
        if (uid == null) {
            return false;
        }
        try {
            return Long.parseLong(uid) > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Check if uid is valid
     *
     * @param uid uid
     * @return {@code true} if uid is valid,
     *         {@code false} otherwise
     */
    public static boolean isValid(@Nullable Long uid) {
        return uid != null && uid > 0;
    }

    @JsonCreator
    private Uid(@Nonnull String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("UID value is invalid");
        }
        this.value = Long.parseLong(value);
    }

    private Uid(@Nonnull Long value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("UID value is invalid");
        }
        this.value = value;
    }

    @Nonnull
    public Long asLong() {
        return value;
    }

    @JsonValue
    @Nonnull
    public String asString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Uid other = (Uid) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Nonnull
    @Override
    public String toString() {
        return asString();
    }

}
