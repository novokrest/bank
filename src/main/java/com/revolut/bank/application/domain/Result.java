package com.revolut.bank.application.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static java.util.Objects.requireNonNull;

/**
 * Result of operation
 *
 * @param <ResultT> type of operation result
 * @param <ErrorT> type of error during operation
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class Result<ResultT, ErrorT> {

    /**
     * Flag if operation was succeeded
     */
    private final boolean success;

    /**
     * Operation result
     */
    @Nullable
    private final ResultT result;

    /**
     * Error occurred during operation
     */
    @Nullable
    private final ErrorT error;

    @Nonnull
    public static <ErrorT> Result<Void, ErrorT> success() {
        return new Result<>(true, null, null);
    }

    @Nonnull
    public static <ResultT, ErrorT> Result<ResultT, ErrorT> success(@Nonnull ResultT result) {
        return new Result<>(true, requireNonNull(result, "result"), null);
    }

    @Nonnull
    public static <ResultT, ErrorT> Result<ResultT, ErrorT> error(@Nonnull ErrorT error) {
        return new Result<>(false, null, requireNonNull(error, "error"));
    }

    private Result(boolean success, @Nullable ResultT result, @Nullable ErrorT error) {
        this.success = success;
        this.result = result;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }

    @Nonnull
    public ResultT getResultOrThrow() {
        if (result == null) {
            throw new IllegalStateException("Result is absent");
        }
        return result;
    }

    @Nonnull
    public ErrorT getErrorOrThrow() {
        if (error == null) {
            throw new IllegalStateException("Error is absent");
        }
        return error;
    }

}
