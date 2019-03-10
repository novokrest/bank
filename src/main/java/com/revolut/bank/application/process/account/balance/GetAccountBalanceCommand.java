package com.revolut.bank.application.process.account.balance;

import com.revolut.bank.application.api.ApiMonetaryAmount;
import com.revolut.bank.application.api.account.GetAccountBalanceApplicationError;
import com.revolut.bank.application.api.account.GetAccountBalanceResponse;
import com.revolut.bank.application.domain.account.Account;
import com.revolut.bank.application.domain.account.Uid;
import com.revolut.bank.application.domain.money.MonetaryAmount;
import com.revolut.bank.application.engine.Command;
import com.revolut.bank.application.engine.CommandResult;
import com.revolut.bank.application.engine.error.factory.ValidationErrorFactory;
import com.revolut.bank.application.engine.validation.ValidationRuleList;
import com.revolut.bank.application.engine.validation.ValidationRules;
import com.revolut.bank.application.service.account.AccountManager;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.requireNonNull;

/**
 * Command to obtain account's balance by UID
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class GetAccountBalanceCommand implements Command<String, GetAccountBalanceResponse> {

    private static final Logger log = LoggerFactory.getLogger(GetAccountBalanceCommand.class);

    private static final ValidationRules<String> REQUEST_VALIDATION_RULES =
            ValidationRuleList.<String>validateUntilFirstError()
                    .addRule(Uid::isValid, ValidationErrorFactory.paramInvalid("uid"));

    private static final CommandResult<GetAccountBalanceResponse> ACCOUNT_NOT_FOUND =
            CommandResult.applicationError(GetAccountBalanceApplicationError.ACCOUNT_NOT_FOUND);

    private final AccountManager accountManager;

    @Inject
    public GetAccountBalanceCommand(@Nonnull AccountManager accountManager) {
        this.accountManager = requireNonNull(accountManager);
    }

    @Nonnull
    @Override
    public ValidationRules<String> getValidationRules() {
        return REQUEST_VALIDATION_RULES;
    }

    @Nonnull
    @Override
    public CommandResult<GetAccountBalanceResponse> execute(@Nonnull String uid) {
        return accountManager.findAccount(Uid.of(uid))
                .map(account -> CommandResult.success(getAccountBalanceResponse(account)))
                .orElseGet(() -> {
                    log.warn("Account was not found: uid={}", uid);
                    return ACCOUNT_NOT_FOUND;
                });
    }

    @Nonnull
    private GetAccountBalanceResponse getAccountBalanceResponse(@Nonnull Account account) {
        MonetaryAmount balance = account.getBalance();
        return new GetAccountBalanceResponse(
                ApiMonetaryAmount.builder()
                        .withAmount(balance.getAmount())
                        .withCurrency(balance.getCurrency())
                        .build()
        );
    }

}
