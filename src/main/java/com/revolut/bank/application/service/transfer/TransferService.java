package com.revolut.bank.application.service.transfer;

import com.revolut.bank.application.domain.TransferError;
import com.revolut.bank.application.domain.Result;
import com.revolut.bank.application.domain.account.Account;
import com.revolut.bank.application.domain.account.Uid;
import com.revolut.bank.application.domain.money.MonetaryAmount;
import com.revolut.bank.application.service.account.AccountLocker;
import com.revolut.bank.application.service.account.AccountManager;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Service to atomically transfer money between accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountManager accountManager;
    private final AccountLocker accountLocker;

    @Inject
    public TransferService(@Nonnull AccountManager accountManager, @Nonnull AccountLocker accountLocker) {
        this.accountManager = requireNonNull(accountManager, "accountManager");
        this.accountLocker = requireNonNull(accountLocker, "accountLocker");
    }

    /**
     * Transfer given monetary amount between accounts
     *
     * @param srcAccountUid source account's UID
     * @param dstAccountUid destination account's UID
     * @param amount monetary amount
     * @return transfer result
     */
    @Nonnull
    public Result<Void, TransferError> transferMoney(@Nonnull Uid srcAccountUid,
                                                     @Nonnull Uid dstAccountUid,
                                                     @Nonnull MonetaryAmount amount) {
        Account srcAccount = accountManager.getAccount(srcAccountUid);
        Account dstAccount = accountManager.getAccount(dstAccountUid);
        log.info("Try to transfer money: srcAccount={}, dstAccount={}, amount={}", srcAccount, dstAccount, amount);
        TransferError error = checkTransferPossible(srcAccount, dstAccount, amount);
        if (error != null) {
            log.warn("Transfer money is not possible: error={}", error);
            return Result.error(error);
        }
        return accountLocker
                .executeUnderLocks(srcAccount.getUid(), dstAccount.getUid(), () ->
                        transferMoneyUnderLock(srcAccount.getUid(), dstAccount.getUid(), amount))
                .orElseGet(() -> {
                    log.warn("Failed to obtain locks to transfer money: srcAccount={}, dstAccount={}", srcAccount, dstAccount);
                    return Result.error(TransferError.ACCOUNT_BUSY);
                });
    }

    @Nonnull
    private Result<Void, TransferError> transferMoneyUnderLock(@Nonnull Uid srcAccountUid,
                                                               @Nonnull Uid dstAccountUid,
                                                               @Nonnull MonetaryAmount amount) {
        Account srcAccount = accountManager.getAccount(srcAccountUid);
        Account dstAccount = accountManager.getAccount(dstAccountUid);
        TransferError error = checkTransferAllowable(srcAccount, dstAccount, amount);
        if (error != null) {
            log.warn("Transfer money was not allowed: error={}", error);
            return Result.error(error);
        }
        MonetaryAmount srcAccountBalance = srcAccount.getBalance().add(amount.getAmount().negate());
        MonetaryAmount dstAccountBalance = dstAccount.getBalance().add(amount.getAmount());
        Account updatedSrcAccount = accountManager.updateAccount(srcAccount, srcAccountBalance);
        Account updatedDstAccount = accountManager.updateAccount(dstAccount, dstAccountBalance);
        log.info("Money was transferred successfully: srcAccount={}, dstAccount={}, amount={}",
                updatedSrcAccount, updatedDstAccount, amount);
        return Result.success();
    }

    @Nullable
    private TransferError checkTransferPossible(@Nonnull Account srcAccount,
                                                @Nonnull Account dstAccount,
                                                @Nonnull MonetaryAmount amount) {
        if (srcAccount.getBalance().getCurrency() != dstAccount.getBalance().getCurrency()) {
            return TransferError.ACCOUNTS_CURRENCIES_NOT_SAME;
        }
        if (srcAccount.getBalance().getCurrency() != amount.getCurrency()) {
            return TransferError.TRANSFER_AMOUNT_CURRENCY_DIFFERS_FROM_ACCOUNTS;
        }
        TransferError error = checkTransferAllowable(srcAccount, dstAccount, amount);
        if (error != null) {
            return error;
        }
        return null;
    }

    @Nullable
    private TransferError checkTransferAllowable(@Nonnull Account srcAccount,
                                                 @Nonnull Account dstAccount,
                                                 @Nonnull MonetaryAmount amount) {
        if (!accountManager.isBalanceAllowable(srcAccount.getBalance().getAmount().subtract(amount.getAmount()))) {
            return TransferError.INSUFFICIENT_SOURCE_BALANCE;
        }
        if (!accountManager.isBalanceAllowable(dstAccount.getBalance().getAmount().add(amount.getAmount()))) {
            return TransferError.DESTINATION_BALANCE_OVERFLOW;
        }
        return null;
    }

}
