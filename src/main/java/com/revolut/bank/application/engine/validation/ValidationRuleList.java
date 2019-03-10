package com.revolut.bank.application.engine.validation;

import com.revolut.bank.application.engine.error.ValidationError;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of {@link ValidationRuleList}
 *
 * @param <RequestT> request's type
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class ValidationRuleList<RequestT> implements ValidationRules<RequestT> {

    private final List<ValidationRule<RequestT>> validators = new LinkedList<>();

    private final ValidationStrategy strategy;

    /**
     * Creates validation rules that returns all validation errors
     *
     * @param <RequestT> request's type
     * @return validation rules
     */
    @Nonnull
    public static <RequestT> ValidationRuleList<RequestT> validateAll() {
        return new ValidationRuleList<>(ValidateAllStrategy.getInstance());
    }

    /**
     * Creates validation rules that returns only first validation errors
     *
     * @param <RequestT> request's type
     * @return validation rules
     */
    @Nonnull
    public static <RequestT> ValidationRuleList<RequestT> validateUntilFirstError() {
        return new ValidationRuleList<>(ValidateUntilFirstErrorStrategy.getInstance());
    }

    private ValidationRuleList(@Nonnull ValidationStrategy strategy) {
        this.strategy = requireNonNull(strategy, "strategy");
    }

    @Nonnull
    @Override
    public List<ValidationError> validate(@Nonnull RequestT request) {
        return strategy.validate(validators, request);
    }

    /**
     * Creates validation rule based on predicate and adds to current list of rules
     *
     * @param validator validation rule
     * @param validationError error returned when predicate fails
     * @return validation rules
     */
    @Nonnull
    public ValidationRuleList<RequestT> addRule(@Nonnull Predicate<RequestT> validator, @Nonnull ValidationError validationError) {
        requireNonNull(validator, "validator");
        requireNonNull(validationError, "validationError");
        return addRule(request -> validator.test(request) ? Optional.empty() : Optional.of(validationError));
    }

    /**
     * Add validation rule to current list of rules
     *
     * @param validationRule validation rules
     * @return validation rules
     */
    @Nonnull
    public ValidationRuleList<RequestT> addRule(@Nonnull ValidationRule<RequestT> validationRule) {
        requireNonNull(validationRule, "validationRule");
        validators.add(validationRule);
        return this;
    }

}
