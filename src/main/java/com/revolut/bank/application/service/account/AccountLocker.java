package com.revolut.bank.application.service.account;

import com.revolut.bank.application.domain.account.Uid;
import com.revolut.bank.application.service.lock.LocksHolder;
import java.util.function.Function;
import org.jvnet.hk2.annotations.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Used to lock several account for action execution
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class AccountLocker {

    private final LocksHolder locks;

    @Inject
    public AccountLocker(@Nonnull LocksHolder locks) {
        this.locks = requireNonNull(locks, "locks");
    }

    /**
     * Tries to lock accounts and execute action while locks are held
     *
     * @param account1 one account
     * @param account2 another account
     * @param action action to execute
     * @param <ResultT> action's result
     * @return {@link Optional} with action's result if lock was acquired,
     *         {@link Optional#empty()} - otherwise
     */
    public <ResultT> Optional<ResultT> executeUnderLocks(@Nonnull Uid account1,
                                                         @Nonnull Uid account2,
                                                         @Nonnull Supplier<ResultT> action) {
        return account1.asLong() < account2.asLong()
                ? executeUnderLocksOrdered(account1, account2, action)
                : executeUnderLocksOrdered(account2, account1, action);
    }

    private <ResultT> Optional<ResultT> executeUnderLocksOrdered(@Nonnull Uid first,
                                                                 @Nonnull Uid second,
                                                                 @Nonnull Supplier<ResultT> action) {
        return locks.tryWithLock(first.asString(), () ->
                locks.tryWithLock(second.asString(), action)).flatMap(Function.identity());
    }

}
