package ro.rentea.lidldigitalinterviewapp.entity.validation;

import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.exception.InvalidEmailAddressException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;

public class CustomerValidator {

    public static void validate(Customer customer) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        Optional<ConstraintViolation<Customer>> emailAddressConstraintViolation = violations.stream()
                .filter(violation -> violation.getMessage().contains("Invalid email address"))
                .findFirst();

        if (emailAddressConstraintViolation.isPresent()) {
            throw new InvalidEmailAddressException(customer.getEmailAddress());
        }
    }

}
