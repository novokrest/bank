package com.revolut.bank.application.engine.validation;

import com.revolut.bank.application.engine.error.ValidationError;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Apply all validation rules and collect all errors
 */
public class ValidateAllStrategy implements ValidationStrategy {

    private static final ValidateAllStrategy INSTANCE = new ValidateAllStrategy();

    @Nonnull
    public static ValidationStrategy getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public <RequestT> List<ValidationError> validate(@Nonnull List<ValidationRule<RequestT>> validators, @Nonnull RequestT request) {
        return validators.stream()
                .map(rule -> rule.validate(request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
