package com.revolut.bank.application.process.transfer;

import com.revolut.bank.application.api.transfer.TransferMoneyApplicationError;
import com.revolut.bank.application.api.transfer.TransferMoneyRequest;
import com.revolut.bank.application.api.transfer.TransferMoneyResponse;
import com.revolut.bank.application.api.transfer.TransferStatus;
import com.revolut.bank.application.domain.Result;
import com.revolut.bank.application.domain.TransferError;
import com.revolut.bank.application.domain.account.Uid;
import com.revolut.bank.application.domain.money.Currency;
import com.revolut.bank.application.domain.money.MonetaryAmount;
import com.revolut.bank.application.engine.Command;
import com.revolut.bank.application.engine.CommandResult;
import com.revolut.bank.application.engine.error.ValidationError;
import com.revolut.bank.application.engine.error.factory.ValidationErrorFactory;
import com.revolut.bank.application.engine.validation.ValidationRuleList;
import com.revolut.bank.application.engine.validation.ValidationRules;
import com.revolut.bank.application.service.transfer.TransferService;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.requireNonNull;

/**
 * Command to transfer money between accounts
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class TransferMoneyCommand implements Command<TransferMoneyRequest, TransferMoneyResponse> {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyCommand.class);

    private static final ValidationRules<TransferMoneyRequest> REQUEST_VALIDATION_RULES =
            ValidationRuleList.<TransferMoneyRequest>validateUntilFirstError()
                    .addRule(req -> Objects.nonNull(req.getSource()), ValidationErrorFactory.paramNotProvided("source"))
                    .addRule(req -> Uid.isValid(req.getSource()), ValidationErrorFactory.paramInvalid("source"))
                    .addRule(req -> Objects.nonNull(req.getDestination()), ValidationErrorFactory.paramNotProvided("destination"))
                    .addRule(req -> Uid.isValid(req.getDestination()), ValidationErrorFactory.paramInvalid("destination"))
                    .addRule(req -> Objects.nonNull(req.getAmount()), ValidationErrorFactory.paramNotProvided("amount"))
                    .addRule(req -> Objects.nonNull(req.getAmount().getCurrency()),
                            ValidationErrorFactory.paramInvalid("amount", "Amount currency must be provided"))
                    .addRule(req -> Currency.fromCodeOptional(req.getAmount().getCurrency()).isPresent() ,
                            ValidationErrorFactory.paramInvalid("amount", "Amount currency is not supported"))
                    .addRule(req -> Objects.nonNull(req.getAmount().getAmount()),
                            ValidationErrorFactory.paramInvalid("amount", "Amount sum must be provided"))
                    .addRule(req -> BigDecimal.ZERO.compareTo(req.getAmount().getAmount()) < 0,
                            ValidationErrorFactory.paramInvalid("amount", "Amount to transfer must be positive"))
                    .addRule(TransferMoneyCommand::validateTransferAmountPrecision)
                    .addRule(req -> !Objects.equals(req.getSource(), req.getDestination()),
                                ValidationErrorFactory.paramInvalid("destination", "Destination account must differ from source"))
            ;

    @Nonnull
    private static Optional<ValidationError> validateTransferAmountPrecision(@Nonnull TransferMoneyRequest request) {
        Currency currency = Currency.fromCode(request.getAmount().getCurrency());
        if (request.getAmount().getAmount().scale() == currency.getCentsPower()) {
            return Optional.empty();
        }
        ValidationError error = ValidationErrorFactory.paramInvalid("amount",
                String.format("Amount to transfer must have %d decimal places", currency.getCentsPower()));
        return Optional.of(error);
    }

    private final TransferService transferService;

    @Inject
    public TransferMoneyCommand(@Nonnull TransferService transferService) {
        this.transferService = requireNonNull(transferService);
    }

    @Nonnull
    @Override
    public ValidationRules<TransferMoneyRequest> getValidationRules() {
        return REQUEST_VALIDATION_RULES;
    }

    @Nonnull
    @Override
    public CommandResult<TransferMoneyResponse> execute(@Nonnull TransferMoneyRequest request) {
        Uid fromUid = Uid.of(request.getSource());
        Uid toUid = Uid.of(request.getDestination());
        MonetaryAmount amount = MonetaryAmount.builder()
                .withAmount(request.getAmount().getAmount())
                .withCurrency(Currency.fromCode(request.getAmount().getCurrency()))
                .build();
        Result<Void, TransferError> transferResult = transferService.transferMoney(fromUid, toUid, amount);
        return transferResult.isSuccess()
                ? CommandResult.success(new TransferMoneyResponse(TransferStatus.SUCCESS))
                : mapError(transferResult.getErrorOrThrow());
    }

    @Nonnull
    private static CommandResult<TransferMoneyResponse> mapError(@Nonnull TransferError error) {
        log.warn("Failed to transfer money: error={}", error);
        switch (error) {
            case ACCOUNT_BUSY:
                return CommandResult.retryAfter(Duration.ofMillis(100L));
            case ACCOUNTS_CURRENCIES_NOT_SAME:
                return CommandResult.applicationError(TransferMoneyApplicationError.ACCOUNTS_CURRENCIES_NOT_SAME);
            case TRANSFER_AMOUNT_CURRENCY_DIFFERS_FROM_ACCOUNTS:
                return CommandResult.applicationError(TransferMoneyApplicationError.TRANSFER_AMOUNT_CURRENCY_DIFFERS_FROM_ACCOUNTS);
            case INSUFFICIENT_SOURCE_BALANCE:
                return CommandResult.applicationError(TransferMoneyApplicationError.INSUFFICIENT_SOURCE_BALANCE);
            case DESTINATION_BALANCE_OVERFLOW:
                return CommandResult.applicationError(TransferMoneyApplicationError.DESTINATION_BALANCE_LIMIT_EXCEEDED);
            default:
                throw new RuntimeException("Unexpected transfer error: error=" + error);
        }
    }

}
