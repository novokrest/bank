package com.revolut.bank.application.service.account;

import com.revolut.bank.application.domain.AccountCreationError;
import com.revolut.bank.application.domain.Result;
import com.revolut.bank.application.domain.account.Account;
import com.revolut.bank.application.domain.account.Uid;
import com.revolut.bank.application.domain.money.MonetaryAmount;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * Class to manage accounts: create, update, search
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class AccountManager {

    private static final Logger log = LoggerFactory.getLogger(AccountManager.class);

    private final AtomicLong uniqueUidGenerator = new AtomicLong(1000000000);
    private final AccountStorage accountStorage;
    private final BigDecimal minBalanceThreshold;
    private final BigDecimal maxBalanceThreshold;

    @Inject
    public AccountManager(@Nonnull AccountStorage accountStorage,
                          @Nonnull BigDecimal minBalanceThreshold,
                          @Nonnull BigDecimal maxBalanceThreshold) {
        this.accountStorage = requireNonNull(accountStorage, "accountStorage");
        this.minBalanceThreshold = requireNonNull(minBalanceThreshold, "minBalanceThreshold");
        this.maxBalanceThreshold = requireNonNull(maxBalanceThreshold, "maxBalanceThreshold");
    }

    /**
     * Creates account with given balance
     *
     * @param balance required account's balance
     * @return result of account creation
     */
    @Nonnull
    public Result<Account, AccountCreationError> createAccount(@Nonnull MonetaryAmount balance) {
        log.info("Try to create new account: balance={}", balance);
        if (!isBalanceAllowable(balance.getAmount())) {
            log.warn("Failed to create account because given balance is too high: balance={}", balance);
            return Result.error(AccountCreationError.BALANCE_TO_HIGH);
        }
        Uid uid = generateUid();
        Account account = Account.builder()
                .withUid(uid)
                .withBalance(balance)
                .withCreatedAt(ZonedDateTime.now())
                .build();
        accountStorage.storeAccount(account);
        log.info("Fresh account was created: account={}", account);
        return Result.success(account);
    }

    /**
     * Checks if given balance is allowable for any account
     *
     * @param balanceAmount checked amount of money on balance
     * @return {@code true} if given balance is allowable,
     *         {@code false} - otherwise
     */
    public boolean isBalanceAllowable(@Nonnull BigDecimal balanceAmount) {
        return balanceAmount.compareTo(minBalanceThreshold) >= 0
                && balanceAmount.compareTo(maxBalanceThreshold) <= 0;
    }

    /**
     * Updates balance for given account
     *
     * @param account account to update
     * @param accountBalance target balance
     * @return account with updated balance
     */
    public Account updateAccount(@Nonnull Account account, @Nonnull MonetaryAmount accountBalance) {
        Account updatedAccount = Account.builder(account)
                .withBalance(accountBalance)
                .build();
        accountStorage.storeAccount(updatedAccount);
        return updatedAccount;
    }

    /**
     * Checks if account exists with given UID
     *
     * @param uid account's UID
     * @return {@code true} if account exists,
     *         {@code false} - otherwise
     */
    public boolean checkExists(@Nonnull Uid uid) {
        return accountStorage.findAccount(uid).isPresent();
    }

    /**
     * Returns account data by given UID
     *
     * @param uid account's UID
     * @return account data
     * @throws RuntimeException if account was not found
     */
    @Nonnull
    public Account getAccount(@Nonnull Uid uid) {
        return findAccount(uid).orElseThrow(() -> new RuntimeException("Account was not found: uid=" + uid));
    }

    /**
     * Search account by UID
     *
     * @param uid account's UID
     * @return {@link Optional} with account data if account was found,
     *         {@link Optional#empty()} - otherwise
     */
    @Nonnull
    public Optional<Account> findAccount(Uid uid) {
        return accountStorage.findAccount(uid);
    }

    @Nonnull
    private Uid generateUid() {
        return Uid.of(uniqueUidGenerator.incrementAndGet());
    }

}
