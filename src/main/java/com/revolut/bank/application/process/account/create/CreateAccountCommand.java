package com.revolut.bank.application.process.account.create;

import com.revolut.bank.application.api.account.CreateAccountApplicationError;
import com.revolut.bank.application.api.account.CreateAccountRequest;
import com.revolut.bank.application.api.account.CreateAccountResponse;
import com.revolut.bank.application.domain.AccountCreationError;
import com.revolut.bank.application.domain.Result;
import com.revolut.bank.application.domain.account.Account;
import com.revolut.bank.application.domain.money.Currency;
import com.revolut.bank.application.domain.money.MonetaryAmount;
import com.revolut.bank.application.engine.Command;
import com.revolut.bank.application.engine.CommandResult;
import com.revolut.bank.application.engine.error.ValidationError;
import com.revolut.bank.application.engine.error.factory.ValidationErrorFactory;
import com.revolut.bank.application.engine.validation.ValidationRuleList;
import com.revolut.bank.application.engine.validation.ValidationRules;
import com.revolut.bank.application.service.account.AccountManager;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

/**
 * Command to create new account with given balance
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class CreateAccountCommand implements Command<CreateAccountRequest, CreateAccountResponse> {

    private static final ValidationRules<CreateAccountRequest> REQUEST_VALIDATION_RULES =
            ValidationRuleList.<CreateAccountRequest>validateUntilFirstError()
                    .addRule(req -> Objects.nonNull(req.getBalance()), ValidationErrorFactory.paramNotProvided("balance"))
                    .addRule(req -> Objects.nonNull(req.getBalance().getCurrency()),
                            ValidationErrorFactory.paramInvalid("balance", "Balance currency must be provided"))
                    .addRule(req -> Currency.fromCodeOptional(req.getBalance().getCurrency()).isPresent() ,
                            ValidationErrorFactory.paramInvalid("balance", "Balance currency is not supported"))
                    .addRule(req -> Objects.nonNull(req.getBalance().getAmount()),
                            ValidationErrorFactory.paramInvalid("balance", "Balance sum must be provided"))
                    .addRule(req -> BigDecimal.ZERO.compareTo(req.getBalance().getAmount()) <= 0,
                            ValidationErrorFactory.paramInvalid("balance", "Balance must be non-negative"))
                    .addRule(CreateAccountCommand::validateBalanceAmountPrecision);

    @Nonnull
    private static Optional<ValidationError> validateBalanceAmountPrecision(@Nonnull CreateAccountRequest request) {
        Currency currency = Currency.fromCode(request.getBalance().getCurrency());
        if (request.getBalance().getAmount().scale() == currency.getCentsPower()) {
            return Optional.empty();
        }
        ValidationError error = ValidationErrorFactory.paramInvalid("balance",
                String.format("Balance sum must have %d decimal places", currency.getCentsPower()));
        return Optional.of(error);
    }

    private final AccountManager accountManager;

    @Inject
    public CreateAccountCommand(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Nonnull
    @Override
    public ValidationRules<CreateAccountRequest> getValidationRules() {
        return REQUEST_VALIDATION_RULES;
    }

    @Nonnull
    @Override
    public CommandResult<CreateAccountResponse> execute(@Nonnull CreateAccountRequest request) {
        MonetaryAmount balance = MonetaryAmount.builder()
                .withAmount(request.getBalance().getAmount())
                .withCurrency(Currency.fromCode(request.getBalance().getCurrency()))
                .build();
        Result<Account, AccountCreationError> accountCreationResult = accountManager.createAccount(balance);
        return accountCreationResult.isSuccess()
                ? CommandResult.success(getSuccessResponse(accountCreationResult.getResultOrThrow()))
                : CommandResult.applicationError(getApplicationError(accountCreationResult.getErrorOrThrow()));
    }

    @Nonnull
    private static CreateAccountResponse getSuccessResponse(@Nonnull Account account) {
        return CreateAccountResponse.builder()
                .withAccount(account.getUid())
                .build();
    }

    @Nonnull
    private CreateAccountApplicationError getApplicationError(@Nonnull AccountCreationError error) {
        switch (error) {
            case BALANCE_TO_HIGH:
                return CreateAccountApplicationError.BALANCE_TO_HIGH;
            default:
                throw new RuntimeException("Unexpected error occurred during account creation: error=" + error);
        }
    }

}
