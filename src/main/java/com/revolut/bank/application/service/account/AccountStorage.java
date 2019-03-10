package com.revolut.bank.application.service.account;

import com.revolut.bank.application.domain.account.Account;
import com.revolut.bank.application.domain.account.Uid;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main storage for account information
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class AccountStorage {

    private static final Logger log = LoggerFactory.getLogger(AccountStorage.class);

    private final Map<Uid, Account> accountsByUid = new ConcurrentHashMap<>();

    /**
     * Store account data
     *
     * @param account account
     */
    public void storeAccount(@Nonnull Account account) {
        accountsByUid.put(account.getUid(), account);
        log.info("Account was saved: uid={}", account.getUid());
    }

    /**
     * Search account
     *
     * @param uid account's UID
     * @return {@link Optional} with account data if account was found,
     *         {@link Optional#empty()} - otherwise
     */
    @Nonnull
    public Optional<Account> findAccount(@Nonnull Uid uid) {
        log.debug("Try to find account: uid={}", uid);
        return Optional.ofNullable(accountsByUid.get(uid));
    }

}
