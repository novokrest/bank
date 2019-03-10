package com.revolut.bank.application.engine.validation;

import com.revolut.bank.application.engine.error.ValidationError;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Apply validation rules until first error
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class ValidateUntilFirstErrorStrategy implements ValidationStrategy {

    private static final ValidateUntilFirstErrorStrategy INSTANCE = new ValidateUntilFirstErrorStrategy();

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
                .findFirst()
                .map(Optional::get)
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
    }

}
