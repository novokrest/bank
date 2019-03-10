package com.revolut.bank.application.service.lock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to hold active locks on accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class LocksHolder {

    private static final Logger log = LoggerFactory.getLogger(LocksHolder.class);

    /**
     * Expiration timeout for acquired locks
     */
    private static final Duration LOCK_EXPIRATION_TIMEOUT = Duration.ofMinutes(1);

    /**
     * Little optimization to wait for already acquired lock before fail
     */
    private static final Duration LOCK_WAIT_TIMEOUT = Duration.ofMillis(100);

    private final LoadingCache<String, ReentrantLock> locks;

    public LocksHolder() {
        this.locks = CacheBuilder.newBuilder()
                .softValues()
                .expireAfterAccess(LOCK_EXPIRATION_TIMEOUT)
                .build(new CacheLoader<String, ReentrantLock>() {
                    @Override
                    public ReentrantLock load(String key) {
                        return new ReentrantLock();
                    }
                });
    }

    /**
     * Tries to acquire lock and execute given action while lock is held
     *
     * @param lockId lock's identifier
     * @param action action to execute
     * @param <ResultT> action's result
     * @return {@link Optional} with action's result if lock was acquired,
     *         {@link Optional#empty()} - otherwise
     */
    @Nonnull
    public <ResultT> Optional<ResultT> tryWithLock(@Nonnull String lockId, @Nonnull Supplier<ResultT> action) {
        return getLock(lockId).flatMap(lock -> {
            if (!tryLock(lock)) {
                return Optional.empty();
            }
            try {
                log.debug("lock: id={}, obj={}", lockId, lock.toString());
                return Optional.of(action.get());
            } finally {
                lock.unlock();
            }
        });
    }

    private boolean tryLock(ReentrantLock lock) {
        try {
            return lock.tryLock(LOCK_WAIT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Nonnull
    private Optional<ReentrantLock> getLock(@Nonnull String lockId) {
        try {
            return Optional.of(locks.get(lockId));
        } catch (ExecutionException e) {
            log.error("Failed to get lock: lockId={}", lockId);
            return Optional.empty();
        }
    }

}
